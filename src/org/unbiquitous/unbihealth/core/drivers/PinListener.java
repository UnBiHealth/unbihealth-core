package org.unbiquitous.unbihealth.core.drivers;

import org.unbiquitous.unbihealth.core.uhp.UhpPin;

/**
 * Models an interested listeners in pin change events.
 * 
 * @author Luciano Santos
 * 
 * @see PinDriver#addPinListener(String, PinListener)
 */
public interface PinListener {

	/**
	 * Called whenever a watched pin's value changed.
	 * 
	 * @param pin
	 *            the changed pin.
	 * @param newValue
	 *            the new value.
	 */
	void valueChanged(UhpPin pin, Object newValue);
}
