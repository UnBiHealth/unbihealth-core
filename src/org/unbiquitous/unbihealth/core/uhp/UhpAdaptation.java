package org.unbiquitous.unbihealth.core.uhp;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Describes an adaptation (mapping/conversion) from one type into another.
 *
 * @author Luciano Santos
 */
public class UhpAdaptation {
    public static final String JSON_NAME_KEY = "name";
    public static final String JSON_SOURCE_TYPE_KEY = "sourceType";
    public static final String JSON_TARGET_TYPE_KEY = "targetType";
    public static final String JSON_PARAMS_KEY = "params";

    @JsonProperty(value = JSON_NAME_KEY)
    private String name;

    @JsonProperty(value = JSON_SOURCE_TYPE_KEY)
    private UhpType sourceType;

    @JsonProperty(value = JSON_TARGET_TYPE_KEY)
    private UhpType targetType;

    @JsonProperty(value = JSON_PARAMS_KEY)
    private Map<String, Object> params;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UhpType getSourceType() {
        return sourceType;
    }

    public void setSourceType(UhpType sourceType) {
        this.sourceType = sourceType;
    }

    public UhpType getTargetType() {
        return targetType;
    }

    public void setTargetType(UhpType targetType) {
        this.targetType = targetType;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
}
