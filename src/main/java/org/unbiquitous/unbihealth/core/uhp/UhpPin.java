package org.unbiquitous.unbihealth.core.uhp;

import org.unbiquitous.json.JSONException;
import org.unbiquitous.json.JSONObject;

/**
 * Describes an application control pin. A pin is a way for applications to
 * receive input or output from a unbihealth controller. It must have a unique
 * name within the application context, a communication mode (input, output or
 * both) and a type, such as scalar or vectorial data.
 * 
 * @author Luciano Santos
 */
public class UhpPin {
	public enum IOMode {
		IN, OUT, INOUT
	}

	private String name;
	private IOMode mode;
	private UhpType type;
	private String mnemonic;
	private String description;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public IOMode getMode() {
		return mode;
	}

	public void setMode(IOMode mode) {
		this.mode = mode;
	}

	public UhpType getType() {
		return type;
	}

	public void setType(UhpType type) {
		this.type = type;
	}

	public String getMnemonic() {
		return mnemonic;
	}

	public void setMnemonic(String mnemonic) {
		this.mnemonic = mnemonic;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public static final String JSON_NAME_KEY = "name";
	public static final String JSON_MODE_KEY = "mode";
	public static final String JSON_TYPE_KEY = "type";
	public static final String JSON_MNEMONIC_KEY = "mnemonic";
	public static final String JSON_DESCRIPTION_KEY = "description";

	public JSONObject toJSON() throws JSONException {
		JSONObject json = new JSONObject();

		json.put(JSON_NAME_KEY, getName());
		json.put(JSON_MODE_KEY, getMode().name());
		json.put(JSON_TYPE_KEY, getType().toJSON());
		if (getMnemonic() != null)
			json.put(JSON_MNEMONIC_KEY, getMnemonic());
		if (getDescription() != null)
			json.put(JSON_DESCRIPTION_KEY, getDescription());

		return json;
	}

	public static UhpPin fromJSON(JSONObject json) throws JSONException {
		UhpPin pin = new UhpPin();

		pin.setName(json.getString(JSON_NAME_KEY));
		pin.setMode(IOMode.valueOf(json.getString(JSON_MODE_KEY)));
		pin.setType(UhpType.fromJSON(json.getJSONObject(JSON_TYPE_KEY)));
		pin.setMnemonic(json.optString(JSON_MNEMONIC_KEY, null));
		pin.setDescription(json.optString(JSON_DESCRIPTION_KEY, null));

		return pin;
	}
}
