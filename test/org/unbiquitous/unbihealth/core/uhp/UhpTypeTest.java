package org.unbiquitous.unbihealth.core.uhp;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.unbiquitous.unbihealth.core.uhp.UhpType.BaseType;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class UhpTypeTest {
	private static final ObjectMapper mapper = new ObjectMapper();

	@Test
	public void testConstructor() {
		assertThat(new UhpType().getBaseType()).isNull();
		assertThat(new UhpType(BaseType.DISCRETE).getBaseType()).isEqualTo(BaseType.DISCRETE);
	}

	@Test
	public void testDefaultTypes() {
		UhpType bit = UhpType.bit;
		assertThat(bit.getBaseType()).isEqualTo(BaseType.DISCRETE);
		assertThat(bit.getDiscRangeStart()).isEqualTo(0l);
		assertThat(bit.getDiscRangeSize()).isEqualTo(2l);

		UhpType uniform = UhpType.uniform;
		assertThat(uniform.getBaseType()).isEqualTo(BaseType.CONTINUOUS);
		assertThat(uniform.getContRangeStart()).isEqualTo(-1.0);
		assertThat(uniform.getContRangeSize()).isEqualTo(2.0);

		UhpType v2 = UhpType.v2;
		assertThat(v2.getBaseType()).isEqualTo(BaseType.ARRAY);
		assertThat(v2.getDimension()).isEqualTo(2);
		assertThat(v2.getElementType().getBaseType()).isEqualTo(BaseType.CONTINUOUS);
		assertThat(v2.getElementType().getContRangeStart()).isNull();
		assertThat(v2.getElementType().getContRangeSize()).isNull();

		UhpType v3 = UhpType.v3;
		assertThat(v3.getBaseType()).isEqualTo(BaseType.ARRAY);
		assertThat(v3.getDimension()).isEqualTo(3);
		assertThat(v3.getElementType().getBaseType()).isEqualTo(BaseType.CONTINUOUS);
		assertThat(v3.getElementType().getContRangeStart()).isNull();
		assertThat(v3.getElementType().getContRangeSize()).isNull();
	}

	@Test
	public void testHashcode() {
		assertThat(new UhpType().hashCode()).isEqualTo(new UhpType().hashCode());
		assertThat(new UhpType(BaseType.DISCRETE).hashCode()).isEqualTo(new UhpType(BaseType.DISCRETE).hashCode());
		assertThat(dummyUhpType().hashCode()).isEqualTo(dummyUhpType().hashCode());
		assertThat(new UhpType(BaseType.DISCRETE).hashCode()).isNotEqualTo(new UhpType(BaseType.CONTINUOUS).hashCode());
	}

	@Test
	public void testEquals() {
		assertThat(new UhpType()).isEqualTo(new UhpType());
		assertThat(new UhpType(BaseType.DISCRETE)).isEqualTo(new UhpType(BaseType.DISCRETE));
		assertThat(dummyUhpType()).isEqualTo(dummyUhpType());
		assertThat(new UhpType(BaseType.DISCRETE)).isNotEqualTo(new UhpType(BaseType.CONTINUOUS));
	}

	@SuppressWarnings("serial")
	@Test
	public void testIsValid() throws IOException {
		assertFalse(new UhpType().isValid());

		assertTrue(UhpType.bit.isValid());
		assertTrue(UhpType.uniform.isValid());
		assertTrue(UhpType.v2.isValid());
		assertTrue(UhpType.v3.isValid());

		assertFalse(UhpType.discrete(0, -1).isValid());
		assertFalse(UhpType.discrete(0, 0).isValid());
		assertTrue(UhpType.discrete(0, 1).isValid());

		assertFalse(UhpType.continuous(0, -1).isValid());
		assertFalse(UhpType.continuous(0, 0).isValid());
		assertTrue(UhpType.continuous(0, 1).isValid());

		assertFalse(UhpType.array(null, -1).isValid());
		assertFalse(UhpType.array(null, 0).isValid());
		assertFalse(UhpType.array(null, 1).isValid());

		assertFalse(UhpType.array(UhpType.discrete(0, 1), -1).isValid());
		assertFalse(UhpType.array(UhpType.discrete(0, 1), 0).isValid());
		assertTrue(UhpType.array(UhpType.discrete(0, 1), 1).isValid());

		assertFalse(UhpType.array(UhpType.discrete(0, -1), -1).isValid());
		assertFalse(UhpType.array(UhpType.discrete(0, -1), 0).isValid());
		assertFalse(UhpType.array(UhpType.discrete(0, -1), 1).isValid());

		assertFalse(UhpType.struct(null).isValid());
		assertFalse(UhpType.struct(new HashMap<String, UhpType>()).isValid());
		assertFalse(UhpType.struct(new HashMap<String, UhpType>() {
			{
				put(null, null);
			}
		}).isValid());
		assertFalse(UhpType.struct(new HashMap<String, UhpType>() {
			{
				put("", null);
			}
		}).isValid());
		assertFalse(UhpType.struct(new HashMap<String, UhpType>() {
			{
				put("field", null);
			}
		}).isValid());
		assertFalse(UhpType.struct(new HashMap<String, UhpType>() {
			{
				put("field", UhpType.discrete(0, -1));
			}
		}).isValid());
		assertTrue(UhpType.struct(new HashMap<String, UhpType>() {
			{
				put("field", UhpType.discrete(0, 1));
			}
		}).isValid());
	}

	@SuppressWarnings({ "rawtypes", "serial" })
	@Test
	public void testExtractValid() {
		UhpType type = dummyExtractType();

		Map<String, Object> src = new HashMap<String, Object>();
		src.put("array", new Integer[] { 0, 1, 0, 1, 0 });
		src.put("uniform", 0.5f);
		Object extracted = type.extractValue(src);
		assertThat(extracted).isInstanceOf(Map.class);
		Map extractedMap = (Map) extracted;
		assertThat(extractedMap.get("array")).isInstanceOf(Object[].class);
		Object[] extractedArray = (Object[]) extractedMap.get("array");
		assertArrayEquals(new Object[] { 0l, 1l, 0l, 1l, 0l }, extractedArray);
		assertThat(extractedMap.get("uniform")).isInstanceOf(Double.class);

		src.put("array", new ArrayList<Integer>() {
			{
				add(0);
				add(1);
				add(0);
				add(1);
				add(0);
			}
		});
		extracted = type.extractValue(src);
		assertThat(extracted).isInstanceOf(Map.class);
		extractedMap = (Map) extracted;
		assertThat(extractedMap.get("array")).isInstanceOf(Object[].class);
		extractedArray = (Object[]) extractedMap.get("array");
		assertArrayEquals(new Object[] { 0l, 1l, 0l, 1l, 0l }, extractedArray);
		assertThat(extractedMap.get("uniform")).isInstanceOf(Double.class);
		assertThat((Double) extractedMap.get("uniform")).isEqualTo(0.5);

		ArrayNode arrayJson = mapper.createArrayNode();
		arrayJson.add(0);
		arrayJson.add(1);
		arrayJson.add(0);
		arrayJson.add(1);
		arrayJson.add(0);
		src.put("array", arrayJson);
		extracted = type.extractValue(src);
		assertThat(extracted).isInstanceOf(Map.class);
		extractedMap = (Map) extracted;
		assertThat(extractedMap.get("array")).isInstanceOf(Object[].class);
		extractedArray = (Object[]) extractedMap.get("array");
		assertArrayEquals(new Object[] { 0l, 1l, 0l, 1l, 0l }, extractedArray);
		assertThat(extractedMap.get("uniform")).isInstanceOf(Double.class);
		assertThat((Double) extractedMap.get("uniform")).isEqualTo(0.5);

		src.put("uniform", 0.5);
		extracted = type.extractValue(src);
		assertThat(extracted).isInstanceOf(Map.class);
		extractedMap = (Map) extracted;
		assertThat(extractedMap.get("array")).isInstanceOf(Object[].class);
		extractedArray = (Object[]) extractedMap.get("array");
		assertArrayEquals(new Object[] { 0l, 1l, 0l, 1l, 0l }, extractedArray);
		assertThat(extractedMap.get("uniform")).isInstanceOf(Double.class);
		assertThat((Double) extractedMap.get("uniform")).isEqualTo(0.5);

		src.put("uniform", 0);
		extracted = type.extractValue(src);
		assertThat(extracted).isInstanceOf(Map.class);
		extractedMap = (Map) extracted;
		assertThat(extractedMap.get("array")).isInstanceOf(Object[].class);
		extractedArray = (Object[]) extractedMap.get("array");
		assertArrayEquals(new Object[] { 0l, 1l, 0l, 1l, 0l }, extractedArray);
		assertThat(extractedMap.get("uniform")).isInstanceOf(Double.class);
		assertThat((Double) extractedMap.get("uniform")).isEqualTo(0);

		src.put("uniform", 0l);
		extracted = type.extractValue(src);
		assertThat(extracted).isInstanceOf(Map.class);
		extractedMap = (Map) extracted;
		assertThat(extractedMap.get("array")).isInstanceOf(Object[].class);
		extractedArray = (Object[]) extractedMap.get("array");
		assertArrayEquals(new Object[] { 0l, 1l, 0l, 1l, 0l }, extractedArray);
		assertThat(extractedMap.get("uniform")).isInstanceOf(Double.class);
		assertThat((Double) extractedMap.get("uniform")).isEqualTo(0);

		ObjectNode structJson = mapper.createObjectNode();
		structJson.set("array", arrayJson);
		structJson.put("uniform", 0.5f);
		extracted = type.extractValue(structJson);
		assertThat(extracted).isInstanceOf(Map.class);
		extractedMap = (Map) extracted;
		assertThat(extractedMap.get("array")).isInstanceOf(Object[].class);
		extractedArray = (Object[]) extractedMap.get("array");
		assertArrayEquals(new Object[] { 0l, 1l, 0l, 1l, 0l }, extractedArray);
		assertThat(extractedMap.get("uniform")).isInstanceOf(Double.class);
		assertThat((Double) extractedMap.get("uniform")).isEqualTo(0.5);

		structJson.put("uniform", 0.5);
		extracted = type.extractValue(structJson);
		assertThat(extracted).isInstanceOf(Map.class);
		extractedMap = (Map) extracted;
		assertThat(extractedMap.get("array")).isInstanceOf(Object[].class);
		extractedArray = (Object[]) extractedMap.get("array");
		assertArrayEquals(new Object[] { 0l, 1l, 0l, 1l, 0l }, extractedArray);
		assertThat(extractedMap.get("uniform")).isInstanceOf(Double.class);
		assertThat((Double) extractedMap.get("uniform")).isEqualTo(0.5);

		structJson.put("uniform", 0);
		extracted = type.extractValue(structJson);
		assertThat(extracted).isInstanceOf(Map.class);
		extractedMap = (Map) extracted;
		assertThat(extractedMap.get("array")).isInstanceOf(Object[].class);
		extractedArray = (Object[]) extractedMap.get("array");
		assertArrayEquals(new Object[] { 0l, 1l, 0l, 1l, 0l }, extractedArray);
		assertThat(extractedMap.get("uniform")).isInstanceOf(Double.class);
		assertThat((Double) extractedMap.get("uniform")).isEqualTo(0);

		structJson.put("uniform", 0l);
		extracted = type.extractValue(structJson);
		assertThat(extracted).isInstanceOf(Map.class);
		extractedMap = (Map) extracted;
		assertThat(extractedMap.get("array")).isInstanceOf(Object[].class);
		extractedArray = (Object[]) extractedMap.get("array");
		assertArrayEquals(new Object[] { 0l, 1l, 0l, 1l, 0l }, extractedArray);
		assertThat(extractedMap.get("uniform")).isInstanceOf(Double.class);
		assertThat((Double) extractedMap.get("uniform")).isEqualTo(0);

		structJson.set("uniform", mapper.getNodeFactory().numberNode(0.5f));
		extracted = type.extractValue(structJson);
		assertThat(extracted).isInstanceOf(Map.class);
		extractedMap = (Map) extracted;
		assertThat(extractedMap.get("array")).isInstanceOf(Object[].class);
		extractedArray = (Object[]) extractedMap.get("array");
		assertArrayEquals(new Object[] { 0l, 1l, 0l, 1l, 0l }, extractedArray);
		assertThat(extractedMap.get("uniform")).isInstanceOf(Double.class);
		assertThat((Double) extractedMap.get("uniform")).isEqualTo(0.5);

		structJson.set("uniform", mapper.getNodeFactory().numberNode(0.5));
		extracted = type.extractValue(structJson);
		assertThat(extracted).isInstanceOf(Map.class);
		extractedMap = (Map) extracted;
		assertThat(extractedMap.get("array")).isInstanceOf(Object[].class);
		extractedArray = (Object[]) extractedMap.get("array");
		assertArrayEquals(new Object[] { 0l, 1l, 0l, 1l, 0l }, extractedArray);
		assertThat(extractedMap.get("uniform")).isInstanceOf(Double.class);
		assertThat((Double) extractedMap.get("uniform")).isEqualTo(0.5);

		structJson.set("uniform", mapper.getNodeFactory().numberNode(0));
		extracted = type.extractValue(structJson);
		assertThat(extracted).isInstanceOf(Map.class);
		extractedMap = (Map) extracted;
		assertThat(extractedMap.get("array")).isInstanceOf(Object[].class);
		extractedArray = (Object[]) extractedMap.get("array");
		assertArrayEquals(new Object[] { 0l, 1l, 0l, 1l, 0l }, extractedArray);
		assertThat(extractedMap.get("uniform")).isInstanceOf(Double.class);
		assertThat((Double) extractedMap.get("uniform")).isEqualTo(0);

		structJson.set("uniform", mapper.getNodeFactory().numberNode(0l));
		extracted = type.extractValue(structJson);
		assertThat(extracted).isInstanceOf(Map.class);
		extractedMap = (Map) extracted;
		assertThat(extractedMap.get("array")).isInstanceOf(Object[].class);
		extractedArray = (Object[]) extractedMap.get("array");
		assertArrayEquals(new Object[] { 0l, 1l, 0l, 1l, 0l }, extractedArray);
		assertThat(extractedMap.get("uniform")).isInstanceOf(Double.class);
		assertThat((Double) extractedMap.get("uniform")).isEqualTo(0);
	}

	@Test
	public void testExtractInvalid() {
		try {
			new UhpType().extractValue(null);
			fail("should not extract with no base type");
		} catch (IllegalStateException e) {
			assertThat(e.getMessage()).contains("base type");
		}

		try {
			UhpType type = new UhpType(BaseType.DISCRETE);
			type.setDiscRangeStart(0l);
			type.setDiscRangeSize(0l);
			type.extractValue(new Integer(0));
			fail("should not extract with invalid discrete range size");
		} catch (IllegalStateException e) {
			assertThat(e.getMessage()).contains("range size");
		}

		try {
			UhpType type = new UhpType(BaseType.CONTINUOUS);
			type.setContRangeStart(0.0);
			type.setContRangeSize(0.0);
			type.extractValue(new Double(0));
			fail("should not extract with invalid continuous range size");
		} catch (IllegalStateException e) {
			assertThat(e.getMessage()).contains("range size");
		}

		try {
			UhpType type = new UhpType(BaseType.ARRAY);
			type.setDimension(2);
			type.extractValue(new Object[2]);
			fail("should not extract with no array element type");
		} catch (IllegalStateException e) {
			assertThat(e.getMessage()).contains("element type");
		}

		try {
			UhpType type = new UhpType(BaseType.ARRAY);
			type.setElementType(UhpType.bit);
			type.extractValue(new Object[0]);
			fail("should not extract with no array dimension");
		} catch (IllegalStateException e) {
			assertThat(e.getMessage()).contains("dimension");
		}

		try {
			UhpType type = new UhpType(BaseType.ARRAY);
			type.setElementType(UhpType.bit);
			type.setDimension(0);
			type.extractValue(new Object[0]);
			fail("should not extract with invalid array dimension");
		} catch (IllegalStateException e) {
			assertThat(e.getMessage()).contains("dimension");
		}

		UhpType type = dummyExtractType();

		try {
			type.extractValue(null);
			fail("should not extract a null object");
		} catch (NullPointerException e) {
			assertThat(e.getMessage()).contains("source");
		}

		Map<String, Object> src = new HashMap<String, Object>();
		try {
			type.extractValue(src);
			fail("should not extract an empty map");
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage()).contains("not found");
		}

		src.put("array", new Float[] { 0f, 1f, 0f, 1f, 0f });
		src.put("uniform", 0.5f);
		try {
			type.extractValue(src);
			fail("should not extract a wrong typed array");
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage()).contains("field");
			assertThat(e.getCause().getMessage()).contains("array element at");
			assertThat(((RuntimeException) e.getCause()).getCause().getMessage()).contains("integral");
		}
		src.put("array", new Integer[] { 0, 1, 0, 1, 0 });
		src.put("uniform", -1.00001);
		try {
			type.extractValue(src);
			fail("should complain about range");
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage()).contains("field");
			assertThat(e.getCause().getMessage()).contains("range");
		}
		src.put("uniform", 2);
		try {
			type.extractValue(src);
			fail("should complain about range");
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage()).contains("field");
			assertThat(e.getCause().getMessage()).contains("range");
		}
	}

	@Test
	public void testToJsonEmpty() {
		ObjectNode node = mapper.createObjectNode();
		node.put(UhpType.JSON_BASE_TYPE_KEY, (String) null);
		assertThat((JsonNode) mapper.valueToTree(new UhpType())).isEqualTo(node);
	}

	@Test
	public void testToJson() {
		assertThat((JsonNode) mapper.valueToTree(dummyUhpType())).isEqualTo(dummyUhpTypeJson());

		UhpType type = dummyUhpType();
		ObjectNode json = dummyUhpTypeJson();
		type.setBaseType(BaseType.CONTINUOUS);
		json.put(UhpType.JSON_BASE_TYPE_KEY, BaseType.CONTINUOUS.name());
		assertThat((JsonNode) mapper.valueToTree(type)).isEqualTo(json);

		UhpType arrayType = new UhpType(BaseType.ARRAY);
		arrayType.setElementType(type);
		arrayType.setDimension(5);
		ObjectNode arrayJson = mapper.createObjectNode();
		arrayJson.put(UhpType.JSON_BASE_TYPE_KEY, BaseType.ARRAY.name());
		arrayJson.set(UhpType.JSON_ARRAY_ELEMENT_TYPE_KEY, json);
		arrayJson.put(UhpType.JSON_ARRAY_DIMENSION_KEY, 5);
		assertThat((JsonNode) mapper.valueToTree(arrayType)).isEqualTo(arrayJson);

		UhpType structType = new UhpType(BaseType.STRUCTURED);
		structType.addField("array", arrayType);
		structType.addField("bit", UhpType.bit);
		ObjectNode structJson = mapper.createObjectNode();
		structJson.put(UhpType.JSON_BASE_TYPE_KEY, BaseType.STRUCTURED.name());
		ObjectNode fieldsJson = structJson.putObject(UhpType.JSON_STRUCT_FIELDS_KEY);
		fieldsJson.set("array", arrayJson);
		ObjectNode bitJson = fieldsJson.putObject("bit");
		bitJson.put(UhpType.JSON_BASE_TYPE_KEY, BaseType.DISCRETE.name());
		bitJson.put(UhpType.JSON_DISCRETE_RANGE_START_KEY, 0l);
		bitJson.put(UhpType.JSON_DISCRETE_RANGE_SIZE_KEY, 2l);
		assertThat((JsonNode) mapper.valueToTree(structType)).isEqualTo(structJson);
	}

	private UhpType dummyUhpType() {
		UhpType type = new UhpType(BaseType.DISCRETE);
		type.setDiscRangeStart(0l);
		type.setDiscRangeSize(1l);
		return type;
	}

	private ObjectNode dummyUhpTypeJson() {
		ObjectNode node = mapper.createObjectNode();
		node.put(UhpType.JSON_BASE_TYPE_KEY, BaseType.DISCRETE.name());
		node.put(UhpType.JSON_DISCRETE_RANGE_START_KEY, 0l);
		node.put(UhpType.JSON_DISCRETE_RANGE_SIZE_KEY, 1l);
		return node;
	}

	private UhpType dummyExtractType() {
		UhpType arrayType = new UhpType(BaseType.ARRAY);
		arrayType.setElementType(UhpType.bit);
		arrayType.setDimension(5);
		UhpType structType = new UhpType(BaseType.STRUCTURED);
		structType.addField("array", arrayType);
		structType.addField("uniform", UhpType.uniform);
		return structType;
	}
}
