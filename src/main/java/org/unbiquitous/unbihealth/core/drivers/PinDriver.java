package org.unbiquitous.unbihealth.core.drivers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.unbiquitous.uos.core.InitialProperties;
import org.unbiquitous.uos.core.adaptabitilyEngine.Gateway;
import org.unbiquitous.uos.core.adaptabitilyEngine.UosEventListener;
import org.unbiquitous.uos.core.applicationManager.CallContext;
import org.unbiquitous.uos.core.driverManager.UosDriver;
import org.unbiquitous.uos.core.driverManager.UosEventDriver;
import org.unbiquitous.uos.core.messageEngine.dataType.UpDevice;
import org.unbiquitous.uos.core.messageEngine.dataType.UpDriver;
import org.unbiquitous.uos.core.messageEngine.messages.Call;
import org.unbiquitous.uos.core.messageEngine.messages.Notify;
import org.unbiquitous.uos.core.messageEngine.messages.Response;

/**
 * {@link UosDriver} responsible for managing all input and output events at
 * connected applications and pins.
 * 
 * @author Luciano Santos
 */
public class PinDriver implements UosEventDriver, UosEventListener {
	Map<String, UpDevice> listeners = new HashMap<String, UpDevice>();

	@Override
	public UpDriver getDriver() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<UpDriver> getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init(Gateway gateway, InitialProperties properties, String instanceId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleEvent(Notify event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerListener(Call serviceCall, Response serviceResponse, CallContext messageContext) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregisterListener(Call serviceCall, Response serviceResponse, CallContext messageContext) {
		// TODO Auto-generated method stub

	}
}
