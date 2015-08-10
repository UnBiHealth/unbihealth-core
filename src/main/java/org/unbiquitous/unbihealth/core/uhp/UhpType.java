package org.unbiquitous.unbihealth.core.uhp;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Describes the type of a communication pin.
 * 
 * A type is described first by its {@link BaseType}.
 * 
 * If the type is {@link BaseType#DISCRETE} (or {@link BaseType#CONTINUOUS}),
 * then you may use {@link #setRange(int, int)} (
 * {@link #setRange(double, double)}) to define a range of accepted values. The
 * default behaviour is to accept any valid values.
 * 
 * If the type is an {@link BaseType#ARRAY}, then you must use
 * {@link #setDimension(int)} to define the array size and
 * {@link #setElementType(UhpType)} to define the type of the array elements.
 * 
 * If the type is a {@link BaseType#STRUCT}, then you must use
 * {@link #setFields(Map)} or {@link #addField(String, UhpType)} to set the name
 * and type for each of its fields.
 * 
 * @author Luciano Santos
 */
public class UhpType {
	public enum BaseType {
		DISCRETE, CONTINUOUS, ARRAY, STRUCTURED
	}

	public static final String JSON_BASE_TYPE_KEY = "baseType";
	public static final String JSON_DISCRETE_RANGE_START_KEY = "discreteRangeStart";
	public static final String JSON_DISCRETE_RANGE_SIZE_KEY = "discreteRangeSize";
	public static final String JSON_CONTINUOUS_RANGE_START_KEY = "continuousRangeStart";
	public static final String JSON_CONTINUOUS_RANGE_SIZE_KEY = "continuousRangeSize";
	public static final String JSON_ARRAY_DIMENSION_KEY = "dimension";
	public static final String JSON_ARRAY_ELEMENT_TYPE_KEY = "elementType";
	public static final String JSON_STRUCT_FIELDS_KEY = "fields";

	public static UhpType bit;
	public static UhpType uniform;
	public static UhpType v2;
	public static UhpType v3;

	{
		bit = new UhpType(BaseType.DISCRETE);
		bit.setRange(0, 1);
		uniform = new UhpType(BaseType.CONTINUOUS);
		uniform.setRange(-1.0, 2.0);
		v2 = new UhpType(BaseType.ARRAY);
		v2.setElementType(new UhpType(BaseType.CONTINUOUS));
		v2.setDimension(2);
		v3 = new UhpType(BaseType.ARRAY);
		v3.setElementType(new UhpType(BaseType.CONTINUOUS));
		v3.setDimension(3);
	}

	@JsonProperty(value = JSON_BASE_TYPE_KEY)
	@JsonInclude(value = Include.ALWAYS)
	private BaseType baseType;

	@JsonProperty(value = JSON_ARRAY_DIMENSION_KEY)
	@JsonInclude(value = Include.ALWAYS)
	private int dimension;

	@JsonProperty(value = JSON_ARRAY_ELEMENT_TYPE_KEY)
	@JsonInclude(value = Include.NON_NULL)
	private UhpType elementType;

	@JsonProperty(value = JSON_STRUCT_FIELDS_KEY)
	@JsonInclude(value = Include.NON_EMPTY)
	private Map<String, UhpType> fields;

	@JsonProperty(value = JSON_DISCRETE_RANGE_START_KEY)
	@JsonInclude(value = Include.NON_NULL)
	private Integer discRangeStart;

	@JsonProperty(value = JSON_DISCRETE_RANGE_SIZE_KEY)
	@JsonInclude(value = Include.NON_NULL)
	private Integer discRangeSize;

	@JsonProperty(value = JSON_CONTINUOUS_RANGE_START_KEY)
	@JsonInclude(value = Include.NON_NULL)
	private Double contRangeStart;

	@JsonProperty(value = JSON_CONTINUOUS_RANGE_SIZE_KEY)
	@JsonInclude(value = Include.NON_NULL)
	private Double contRangeSize;

	public UhpType() {
	}

	public UhpType(BaseType base) {
		setBaseType(base);
	}

	public BaseType getBaseType() {
		return baseType;
	}

	public void setBaseType(BaseType baseType) {
		this.baseType = baseType;
	}

	public void setRange(Double start, Double size) {
		this.contRangeStart = start;
		this.contRangeSize = size;
	}

	public Double getContinuousRangeStart() {
		return contRangeStart;
	}

	public Double getContinuousRangeSize() {
		return contRangeSize;
	}

	public void setRange(Integer start, Integer size) {
		this.discRangeStart = start;
		this.discRangeSize = size;
	}

	public Integer getDiscreteRangeStart() {
		return discRangeStart;
	}

	public Integer getDiscreteRangeSize() {
		return discRangeSize;
	}

	public int getDimension() {
		return dimension;
	}

	public void setDimension(int dimension) {
		this.dimension = dimension;
	}

	public UhpType getElementType() {
		return elementType;
	}

	public void setElementType(UhpType elementType) {
		this.elementType = elementType;
	}

	public Map<String, UhpType> getFields() {
		return fields;
	}

	public void setFields(Map<String, UhpType> fields) {
		this.fields = fields;
	}

	public void addField(String name, UhpType type) {
		if (fields == null)
			fields = new HashMap<String, UhpType>();
		fields.put(name, type);
	}
}
