package org.unbiquitous.unbihealth.core.uhp;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class UhpPinTest {
	private static final ObjectMapper mapper = new ObjectMapper();

	@Test
	public void testToJsonEmpty() {
		ObjectNode node = mapper.createObjectNode();
		node.set(UhpPin.JSON_NAME_KEY, null);
		node.set(UhpPin.JSON_MODE_KEY, null);
		node.set(UhpPin.JSON_TYPE_KEY, null);
		assertThat(mapper.valueToTree(new UhpPin())).isEqualTo(node);
	}
	
	@Test
	public void testToJson() {
		//assertThat(mapper.valueToTree(dummyUhPin())).isEqualTo(dummyUhPinJson());
	}
}
