package org.unbiquitous.unbihealth.core.uhp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

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

	public static final String JSON_NAME_KEY = "name";
	public static final String JSON_MODE_KEY = "mode";
	public static final String JSON_TYPE_KEY = "type";
	public static final String JSON_MNEMONIC_KEY = "mnemonic";
	public static final String JSON_DESCRIPTION_KEY = "description";

	@JsonProperty(value = JSON_NAME_KEY)
	@JsonInclude(value = Include.ALWAYS)
	private String name;

	@JsonProperty(value = JSON_MODE_KEY)
	@JsonInclude(value = Include.ALWAYS)
	private IOMode mode;

	@JsonProperty(value = JSON_TYPE_KEY)
	@JsonInclude(value = Include.ALWAYS)
	private UhpType type;

	@JsonProperty(value = JSON_MNEMONIC_KEY)
	@JsonInclude(value = Include.NON_EMPTY)
	private String mnemonic;

	@JsonProperty(value = JSON_DESCRIPTION_KEY)
	@JsonInclude(value = Include.NON_EMPTY)
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
}
