package org.unbiquitous.unbihealth.core.uhp;

import static org.unbiquitous.uos.core.ClassLoaderUtils.chainHashCode;
import static org.unbiquitous.uos.core.ClassLoaderUtils.compare;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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

	public static final UhpType bit;
	public static final UhpType uniform;
	public static final UhpType v2;
	public static final UhpType v3;

	static {
		bit = discrete(0l, 2l);
		uniform = continuous(-1.0, 2.0);
		v2 = array(continuous(), 2);
		v3 = array(continuous(), 3);
	}

	private static final ObjectMapper mapper = new ObjectMapper();

	@JsonProperty(value = JSON_BASE_TYPE_KEY)
	@JsonInclude(value = Include.ALWAYS)
	private BaseType baseType;

	@JsonProperty(value = JSON_ARRAY_DIMENSION_KEY)
	@JsonInclude(value = Include.NON_NULL)
	private Integer dimension;

	@JsonProperty(value = JSON_ARRAY_ELEMENT_TYPE_KEY)
	@JsonInclude(value = Include.NON_NULL)
	private UhpType elementType;

	@JsonProperty(value = JSON_STRUCT_FIELDS_KEY)
	@JsonInclude(value = Include.NON_EMPTY)
	private Map<String, UhpType> fields;

	@JsonProperty(value = JSON_DISCRETE_RANGE_START_KEY)
	@JsonInclude(value = Include.NON_NULL)
	private Long discRangeStart;

	@JsonProperty(value = JSON_DISCRETE_RANGE_SIZE_KEY)
	@JsonInclude(value = Include.NON_NULL)
	private Long discRangeSize;

	@JsonProperty(value = JSON_CONTINUOUS_RANGE_START_KEY)
	@JsonInclude(value = Include.NON_NULL)
	private Double contRangeStart;

	@JsonProperty(value = JSON_CONTINUOUS_RANGE_SIZE_KEY)
	@JsonInclude(value = Include.NON_NULL)
	private Double contRangeSize;

	public static UhpType discrete() {
		return newDiscrete(null, null);
	}

	public static UhpType discrete(long start) {
		return newDiscrete(start, null);
	}

	public static UhpType discrete(long start, long size) {
		return newDiscrete(start, size);
	}

	private static UhpType newDiscrete(Long start, Long size) {
		UhpType type = new UhpType(BaseType.DISCRETE);
		type.setDiscRangeStart(start);
		type.setDiscRangeSize(size);
		return type;
	}

	public static UhpType continuous() {
		return newContinuous(null, null);
	}

	public static UhpType continuous(double start) {
		return newContinuous(start, null);
	}

	public static UhpType continuous(double start, double size) {
		return newContinuous(start, size);
	}

	private static UhpType newContinuous(Double start, Double size) {
		UhpType type = new UhpType(BaseType.CONTINUOUS);
		type.setContRangeStart(start);
		type.setContRangeSize(size);
		return type;
	}

	public static UhpType array(UhpType elementType, int dimension) {
		UhpType type = new UhpType(BaseType.ARRAY);
		type.setElementType(elementType);
		type.setDimension(dimension);
		return type;
	}

	public static UhpType struct(Map<String, UhpType> fields) {
		UhpType type = new UhpType(BaseType.STRUCTURED);
		type.setFields(fields);
		return type;
	}

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

	public Long getDiscRangeStart() {
		return discRangeStart;
	}

	public void setDiscRangeStart(Long discRangeStart) {
		this.discRangeStart = discRangeStart;
	}

	public Long getDiscRangeSize() {
		return discRangeSize;
	}

	public void setDiscRangeSize(Long discRangeSize) {
		this.discRangeSize = discRangeSize;
	}

	public Double getContRangeStart() {
		return contRangeStart;
	}

	public void setContRangeStart(Double contRangeStart) {
		this.contRangeStart = contRangeStart;
	}

	public Double getContRangeSize() {
		return contRangeSize;
	}

	public void setContRangeSize(Double contRangeSize) {
		this.contRangeSize = contRangeSize;
	}

	public Integer getDimension() {
		return dimension;
	}

	public void setDimension(Integer dimension) {
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

	@JsonIgnore
	public boolean isValid() {
		if (baseType == null)
			return false;

		switch (baseType) {
		case DISCRETE:
			return ((discRangeSize == null) || (discRangeSize.intValue() > 0));

		case CONTINUOUS:
			return ((contRangeSize == null) || (contRangeSize.doubleValue() > 0));

		case ARRAY:
			return (dimension != null) && (dimension.intValue() >= 1) && (elementType != null) && elementType.isValid();

		case STRUCTURED:
			if ((fields == null) || fields.isEmpty())
				return false;
			for (Map.Entry<String, UhpType> entry : fields.entrySet()) {
				if ((entry.getKey() == null) || entry.getKey().isEmpty() || (entry.getValue() == null)
								|| (!entry.getValue().isValid()))
					return false;
			}
			break;
		}
		return true;
	}

	/**
	 * From JSON based object, extracts locally represented valid object for
	 * this type, if possible.
	 * 
	 * @param src
	 *            the JSON based object.
	 * 
	 * @return a local representation of the value valid for this type.
	 * 
	 * @throws IllegalStateException
	 *             if this type is not in a valid state (incoherent or missing
	 *             information).
	 * @throws NullPointerException
	 *             if the src is null.
	 * @throws IllegalArgumentException
	 *             if the source value is invalid for this type.
	 */
	public Object extractValue(Object src) {
		if (baseType == null)
			throw new IllegalStateException("no base type defined");
		if (src == null)
			throw new NullPointerException("source object");

		Object result = null;
		switch (baseType) {
		case DISCRETE:
			if (src instanceof Integer)
				result = validateLong(((Integer) src).intValue());
			else if (src instanceof Long)
				result = validateLong(((Long) src).longValue());
			else if (src instanceof JsonNode) {
				JsonNode node = (JsonNode) src;
				if (node.isInt())
					result = validateLong(node.asInt());
				else if (node.isLong())
					result = validateLong(node.asLong());
				else
					throw new IllegalArgumentException("expected a json integral value");
			} else
				throw new IllegalArgumentException("expected an integral value");
			break;

		case CONTINUOUS:
			if (src instanceof Float)
				result = validateDouble(((Float) src).floatValue());
			else if (src instanceof Double)
				result = validateDouble(((Double) src).doubleValue());
			else if (src instanceof Integer)
				result = validateDouble(((Integer) src).intValue());
			else if (src instanceof Long)
				result = validateDouble(((Long) src).longValue());
			else if (src instanceof JsonNode) {
				JsonNode node = (JsonNode) src;
				if (node.isFloat() || node.isDouble() || node.isInt() || node.isLong())
					result = validateDouble(node.asDouble());
				else
					throw new IllegalArgumentException("expected a json floating point value");
			} else
				throw new IllegalArgumentException("expected a floating point value");
			break;

		case ARRAY:
			if (src instanceof Collection<?>)
				result = validateArray(((Collection<?>) src).toArray(new Object[] {}));
			else if (src.getClass().isArray())
				result = validateArray((Object[]) src);
			else if (src instanceof JsonNode) {
				JsonNode node = (JsonNode) src;
				try {
					result = validateArray(mapper.treeToValue(node, Object[].class));
				} catch (JsonProcessingException e) {
					throw new IllegalArgumentException("exptected a json array", e);
				}
			} else
				throw new IllegalArgumentException("exptected an array or collection");
			break;

		case STRUCTURED:
			if (src instanceof Map<?, ?>)
				result = validateMap((Map<?, ?>) src);
			else if (src instanceof JsonNode) {
				JsonNode node = (JsonNode) src;
				try {
					result = validateMap(mapper.treeToValue(node, Map.class));
				} catch (JsonProcessingException e) {
					throw new IllegalArgumentException("exptected a json object node", e);
				}
			} else
				throw new IllegalArgumentException("exptected a map");
			break;
		}

		return result;
	}

	private Long validateLong(long value) {
		if (discRangeStart != null) {
			if ((discRangeSize != null) && (discRangeSize.longValue() <= 0))
				throw new IllegalStateException("range size is invalid");
			if ((value < discRangeStart.longValue())
							|| ((discRangeSize != null)
											&& (value >= discRangeStart.longValue() + discRangeSize.longValue())))
				throw new IllegalArgumentException("value outside of defined range");
		}
		return value;
	}

	private Double validateDouble(double value) {
		if (contRangeStart != null) {
			if ((contRangeSize != null) && (contRangeSize.doubleValue() <= 0))
				throw new IllegalStateException("range size is invalid");
			if ((value < contRangeStart.doubleValue()) || ((contRangeSize != null)
							&& (value >= contRangeStart.doubleValue() + contRangeSize.doubleValue())))
				throw new IllegalArgumentException("value outside of defined range");
		}
		return value;
	}

	private Object[] validateArray(Object[] array) {
		if ((dimension == null) || (dimension.intValue() < 1))
			throw new IllegalStateException("array dimension is invalid");
		if (elementType == null)
			throw new IllegalStateException("array element type is invalid");
		if (array.length != dimension.intValue())
			throw new IllegalArgumentException("expected array of size " + dimension.intValue());

		Object[] result = new Object[array.length];
		for (int i = 0; i < array.length; ++i) {
			try {
				result[i] = elementType.extractValue(array[i]);
			} catch (RuntimeException e) {
				throw new IllegalArgumentException("array element at " + i + " is invalid", e);
			}
		}
		return result;
	}

	private Map<String, Object> validateMap(Map<?, ?> map) {
		if (fields == null)
			throw new IllegalStateException("no map fields defined");

		Map<String, Object> result = new HashMap<String, Object>();
		for (Map.Entry<String, UhpType> entry : fields.entrySet()) {
			String fieldName = entry.getKey();
			Object value = map.get(fieldName);
			if (value == null)
				throw new IllegalArgumentException("field " + fieldName + " not found in src");
			try {
				result.put(fieldName, entry.getValue().extractValue(value));
			} catch (RuntimeException e) {
				throw new IllegalArgumentException("field " + fieldName + " is invalid", e);
			}
		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof UhpType))
			return false;
		UhpType other = (UhpType) obj;

		if (!compare(this.baseType, other.baseType))
			return false;
		if (!compare(this.dimension, other.dimension))
			return false;
		if (!compare(this.elementType, other.elementType))
			return false;
		if (!compare(this.fields, other.fields))
			return false;
		if (!compare(this.discRangeStart, other.discRangeStart))
			return false;
		if (!compare(this.discRangeSize, other.discRangeSize))
			return false;
		if (!compare(this.contRangeStart, other.contRangeStart))
			return false;
		if (!compare(this.contRangeSize, other.contRangeSize))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int hash = chainHashCode(0, this.baseType);
		hash = chainHashCode(hash, this.dimension);
		hash = chainHashCode(hash, this.elementType);
		hash = chainHashCode(hash, this.fields);
		hash = chainHashCode(hash, this.discRangeStart);
		hash = chainHashCode(hash, this.discRangeSize);
		hash = chainHashCode(hash, this.contRangeStart);
		hash = chainHashCode(hash, this.contRangeSize);
		return hash;
	}

	@Override
	public String toString() {
		try {
			return mapper.writeValueAsString(this);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
