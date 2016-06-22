package org.unbiquitous.unbihealth.core.drivers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.unbiquitous.unbihealth.core.uhp.UhpPin;
import org.unbiquitous.uos.core.InitialProperties;
import org.unbiquitous.uos.core.UOSLogging;
import org.unbiquitous.uos.core.adaptabitilyEngine.Gateway;
import org.unbiquitous.uos.core.adaptabitilyEngine.NotifyException;
import org.unbiquitous.uos.core.adaptabitilyEngine.UosEventListener;
import org.unbiquitous.uos.core.applicationManager.CallContext;
import org.unbiquitous.uos.core.driverManager.UosDriver;
import org.unbiquitous.uos.core.driverManager.UosEventDriver;
import org.unbiquitous.uos.core.messageEngine.dataType.UpDevice;
import org.unbiquitous.uos.core.messageEngine.dataType.UpDriver;
import org.unbiquitous.uos.core.messageEngine.dataType.UpService.ParameterType;
import org.unbiquitous.uos.core.messageEngine.messages.Call;
import org.unbiquitous.uos.core.messageEngine.messages.Notify;
import org.unbiquitous.uos.core.messageEngine.messages.Response;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * {@link UosDriver} responsible for managing all input and output events at
 * connected applications and pins.
 * 
 * @author Luciano Santos
 */
public class PinDriver implements UosEventDriver, UosEventListener {
	public static final String DRIVER_NAME = "unbihealth.PinDriver";
	public static final String LIST_SERVICE_NAME = "list";
	public static final String PINS_FIELD_NAME = "pins";
	public static final String CONNECT_SERVICE_NAME = "connect";
	public static final String DISCONNECT_SERVICE_NAME = "disconnect";
	public static final String UPDATE_EVENT_NAME = "update";
	public static final String PIN_PARAM_NAME = "pin";
	public static final String VALUE_PARAM_NAME = "value";
	public static final String DESTROYED_EVENT_NAME = "destroyed";

	private static Logger logger = UOSLogging.getLogger();
	private static final UpDriver _driver;

	static {
		_driver = new UpDriver(DRIVER_NAME);

		_driver.addService(LIST_SERVICE_NAME);

		_driver.addService(CONNECT_SERVICE_NAME)
						.addParameter(PIN_PARAM_NAME, ParameterType.MANDATORY);

		_driver.addService(DISCONNECT_SERVICE_NAME)
						.addParameter(PIN_PARAM_NAME, ParameterType.MANDATORY);

		_driver.addEvent(UPDATE_EVENT_NAME)
						.addParameter(PIN_PARAM_NAME, ParameterType.MANDATORY)
						.addParameter(VALUE_PARAM_NAME, ParameterType.MANDATORY);
	}

	private static final ObjectMapper mapper = new ObjectMapper();

	private Gateway gateway;
	private String instanceId;
	private PinDAO pinDao = new PinDAO();
	private Map<String, Set<UpDevice>> driverListeners = new HashMap<String, Set<UpDevice>>();
	private Map<String, Set<PinListener>> pinListeners = new HashMap<String, Set<PinListener>>();

	/**
	 * Declares a pin to the PinDriver, with an optional listener.
	 * 
	 * @param pin
	 *            the pin.
	 * 
	 * @see {@link PinDriver#add(UhpPin, PinListener)}
	 */
	public synchronized void add(UhpPin pin) {
		add(pin, null);
	}

	/**
	 * Declares a pin to the PinDriver, with an optional listener.
	 * 
	 * @param pin
	 *            the pin.
	 * @param listener
	 *            the listener.
	 */
	public synchronized void add(UhpPin pin, PinListener listener) {
		if ((pin == null) || (pin.getName() == null) || pin.getName().isEmpty())
			throw new IllegalArgumentException("pin must not be null and must have non empty name");
		if (pinDao.find(pin.getName()) != null)
			throw new IllegalArgumentException("pin already declared");
		if (pin.getMode() == null)
			throw new IllegalArgumentException("pin with no mode set");
		if ((pin.getType() == null) || (!pin.getType().isValid()))
			throw new IllegalArgumentException("pin with invalid type");

		pinDao.put(pin);
		if (listener != null)
			addPinListener(pin.getName(), listener);
	}

	/**
	 * Removes a pin from the driver, and unregisters any previously registered
	 * pins.
	 * 
	 * @param pinName
	 */
	public synchronized void remove(String pinName) {
		if ((pinName == null) || pinName.isEmpty())
			throw new IllegalArgumentException("pin name must not be null or empty");
		if (pinDao.find(pinName) == null)
			throw new IllegalArgumentException("informed pin wasn't found");

		pinDao.remove(pinName);
		pinListeners.remove(pinName);

		fireDriverEvent(pinName, DESTROYED_EVENT_NAME, null);
		driverListeners.remove(pinName);
	}

	/**
	 * Adds a listener to events on a given pin.
	 * 
	 * @param pinName
	 * @param listener
	 */
	public synchronized void addPinListener(String pinName, PinListener listener) {
		if (listener == null)
			throw new IllegalArgumentException("null listener");
		UhpPin pin = pinDao.find(pinName);
		if (pin == null)
			throw new IllegalArgumentException("informed pin does not exist");
		Set<PinListener> listeners = pinListeners.getOrDefault(pinName, new HashSet<PinListener>());
		if (listeners.contains(listener))
			throw new IllegalArgumentException("listener already associated to this pin");
		listeners.add(listener);
		pinListeners.put(pinName, listeners);
	}

	/**
	 * Removes a listener from events on a given pin.
	 * 
	 * @param pinName
	 * @param listener
	 */
	public synchronized void removePinListener(String pinName, PinListener listener) {
		UhpPin pin = pinDao.find(pinName);
		if (pin == null)
			throw new IllegalArgumentException("informed pin does not exist");
		Set<PinListener> listeners = pinListeners.getOrDefault(pinName, new HashSet<PinListener>());
		listeners.remove(listener);
	}

	/**
	 * Handles an external pin change event.
	 * 
	 * @param pinName
	 * @param newValue
	 */
	public synchronized void pinValueChanged(String pinName, Object newValue) {
		if (pinDao.find(pinName) == null)
			throw new IllegalArgumentException("informed pin does not exist");
		fireDriverEvent(pinName, UPDATE_EVENT_NAME, newValue);
	}

	// UOS interface...
	public UpDriver getDriver() {
		return _driver;
	}

	public List<UpDriver> getParent() {
		return null;
	}

	public void init(Gateway gateway, InitialProperties properties, String instanceId) {
		this.gateway = gateway;
		this.instanceId = instanceId;
		try {
			gateway.register(this, null, DRIVER_NAME, UPDATE_EVENT_NAME);
		} catch (NotifyException e) {
			logger.log(Level.SEVERE, "Failed to register for update events.", e);
		}
		pinDao.clear();
		driverListeners.clear();
	}

	public void destroy() {
		try {
			gateway.unregister(this);
		} catch (NotifyException e) {
			logger.log(Level.SEVERE, "Failed to unregister for update events.", e);
		}
	}

	/**
	 * This service lists all available pins.
	 * 
	 * @param gateway
	 * @param properties
	 * @param instanceId
	 */
	public void list(Call call, Response response, CallContext context) {
		try {
			List<UhpPin> pins = pinDao.list();
			response.addParameter(PINS_FIELD_NAME, mapper.valueToTree(pins));
		} catch (Throwable t) {
			response.setError(t.toString());
		}
	}

	/**
	 * This service connects a device to a pin.
	 * 
	 * @param gateway
	 * @param properties
	 * @param instanceId
	 */
	public synchronized void connect(Call call, Response response, CallContext context) {
		UpDevice device = context.getCallerDevice();
		if (device == null)
			throw new NullPointerException("device");

		String pinName = call.getParameterString(PIN_PARAM_NAME);
		if ((pinName == null) || pinName.isEmpty()) {
			response.setError("no pin informed");
			return;
		}
		UhpPin pin = pinDao.find(pinName);
		if (pin == null) {
			response.setError("informed pin does not exist");
			return;
		}
		Set<UpDevice> devices = driverListeners.getOrDefault(pinName, new HashSet<UpDevice>());
		if (devices.contains(device)) {
			response.setError("device already connected to this pin");
			return;
		}
		devices.add(device);
		driverListeners.put(pinName, devices);
		response.addParameter("result", "ok");
	}

	/**
	 * Handles an external pin change event.
	 * 
	 * @param event
	 *            the captured event.
	 */
	public synchronized void handleEvent(Notify event) {
		UhpPin pin = pinDao.find((String) event.getParameter(PIN_PARAM_NAME));
		if (pin == null)
			return;
		Set<PinListener> listeners = pinListeners.get(pin.getName());
		if (listeners == null)
			return;

		try {
			JsonNode src = mapper.readTree(event.getParameter(VALUE_PARAM_NAME).toString());
			Object newValue = pin.getType().extractValue(src);
			for (PinListener listener : listeners)
				listener.valueChanged(pin, newValue);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed while handling incoming event.", e);
		}
	}

	private void fireDriverEvent(String pinName, String eventKey, Object newValue) {
		try {
			Set<UpDevice> listeners = driverListeners.get(pinName);
			if (listeners != null) {
				Notify n = new Notify(eventKey, DRIVER_NAME, instanceId);
				n.addParameter(PIN_PARAM_NAME, pinName);
				if (UPDATE_EVENT_NAME.equals(eventKey))
					n.addParameter(VALUE_PARAM_NAME, mapper.writeValueAsString(newValue));
				for (UpDevice device : listeners)
					gateway.notify(n, device);
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed while firing outgoing event.", e);
		}
	}

	public void registerListener(Call call, Response response, CallContext context) {
	}

	public void unregisterListener(Call call, Response response, CallContext context) {
	}
}
