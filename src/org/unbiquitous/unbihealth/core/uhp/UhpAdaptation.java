package org.unbiquitous.unbihealth.core.uhp;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Describes an adaptation (mapping/conversion) from one type into another.
 *
 * @author Luciano Santos
 */
public class UhpAdaptation {
    public static final String JSON_ADAPTER_KEY = "adapter";
    public static final String JSON_PARAMS_KEY = "params";
    public static final String JSON_MNEMONIC_KEY = "mnemonic";

    @JsonProperty(value = JSON_ADAPTER_KEY)
    private String adapter;

    @JsonProperty(value = JSON_PARAMS_KEY)
    private Map<String, Object> params;

    @JsonProperty(value = JSON_MNEMONIC_KEY)
    private String mnemonic;

    public String getAdapter() {
        return adapter;
    }

    public void setAdapter(String adapter) {
        this.adapter = adapter;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }
}
