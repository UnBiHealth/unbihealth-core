package org.unbiquitous.unbihealth.core.drivers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.unbiquitous.unbihealth.core.uhp.UhpPin;

public class PinDAO {
	private Map<String, UhpPin> pins = new HashMap<String, UhpPin>();

	public PinDAO() {
	}

	public synchronized void clear() {
		pins.clear();
	}

	public synchronized void put(UhpPin pin) {
		if (pin == null)
			throw new IllegalArgumentException("pin must not be null");
		if ((pin.getName() == null) || pin.getName().isEmpty())
			throw new IllegalArgumentException("pin must have non-empty name");

		pins.put(pin.getName(), pin);
	}

	public synchronized void remove(String name) {
		pins.remove(name);
	}

	public synchronized List<UhpPin> list() {
		return new ArrayList<UhpPin>(pins.values());
	}

	public synchronized UhpPin find(String name) {
		return pins.get(name);
	}
}
