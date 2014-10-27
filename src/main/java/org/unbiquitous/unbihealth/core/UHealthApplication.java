package org.unbiquitous.unbihealth.core;

import java.util.Map;

import org.unbiquitous.unbihealth.core.uhp.UhpPin;
import org.unbiquitous.uos.core.applicationManager.UosApplication;

/**
 * Basic interface for all compatible applications.
 * 
 * All applications must implement at least the uOS services defined by this
 * interface, in compliance to uOS's protocols.
 * 
 * @author Luciano Santos
 */
public interface UHealthApplication extends UosApplication {

	/**
	 * Must put at the response map a serialized JSON array, mapped by key
	 * "pins", containing a list of serialized {@link UhpPin}. The pins must all
	 * have unique ids.
	 * 
	 * @param parameters
	 * @return
	 */
	Map<String, Object> listPins(Map<String, Object> parameters);
}
