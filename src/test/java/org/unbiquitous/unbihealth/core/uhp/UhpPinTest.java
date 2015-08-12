package org.unbiquitous.unbihealth.core.uhp;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Test;
import org.unbiquitous.unbihealth.core.uhp.UhpPin;
import org.unbiquitous.unbihealth.core.uhp.UhpType;
import org.unbiquitous.unbihealth.core.uhp.UhpPin.IOMode;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class UhpPinTest {
	private static final ObjectMapper mapper = new ObjectMapper();

	@Test
	public void testConstructor() {
		assertThat(new UhpPin().getName()).isNull();
		assertThat(new UhpPin("pin").getName()).isEqualTo("pin");
	}

	@Test
	public void testToJsonEmpty() {
		ObjectNode node = mapper.createObjectNode();
		node.set(UhpPin.JSON_NAME_KEY, null);
		node.set(UhpPin.JSON_MODE_KEY, null);
		node.set(UhpPin.JSON_TYPE_KEY, null);
		assertThat(mapper.valueToTree(new UhpPin())).isEqualTo(node);
	}

	@Test
	public void testToJson() throws IOException {
		assertThat(mapper.valueToTree(dummyUhpPin())).isEqualTo(dummyUhpPinJson());

		UhpPin pin = dummyUhpPin();
		ObjectNode json = dummyUhpPinJson();
		pin.setName("name2");
		json.put(UhpPin.JSON_NAME_KEY, "name2");
		assertThat(mapper.valueToTree(pin)).isEqualTo(json);

		pin.setMode(IOMode.IN);
		json.put(UhpPin.JSON_MODE_KEY, IOMode.IN.name());
		assertThat(mapper.valueToTree(pin)).isEqualTo(json);
	}

	@Test
	public void testHashcode() {
		assertThat(new UhpPin().hashCode()).isEqualTo(new UhpPin().hashCode());
		assertThat(new UhpPin("pin").hashCode()).isEqualTo(new UhpPin("pin").hashCode());
		assertThat(dummyUhpPin().hashCode()).isEqualTo(dummyUhpPin().hashCode());
		assertThat(new UhpPin("pin1").hashCode()).isNotEqualTo(new UhpPin("pin2").hashCode());
	}
	
	@Test
	public void testEquals() {
		assertThat(new UhpPin()).isEqualTo(new UhpPin());
		assertThat(new UhpPin("pin")).isEqualTo(new UhpPin("pin"));
		assertThat(dummyUhpPin()).isEqualTo(dummyUhpPin());
		assertThat(new UhpPin("pin1")).isNotEqualTo(new UhpPin("pin2"));
	}
	
	private UhpPin dummyUhpPin() {
		UhpPin pin = new UhpPin("dummypin");
		pin.setMode(IOMode.INOUT);
		pin.setType(new UhpType());
		pin.setDescription("description");
		pin.setMnemonic("mnemonic");
		return pin;
	}

	private ObjectNode dummyUhpPinJson() {
		ObjectNode node = mapper.createObjectNode();
		node.put(UhpPin.JSON_NAME_KEY, "dummypin");
		node.put(UhpPin.JSON_MODE_KEY, IOMode.INOUT.name());
		node.set(UhpPin.JSON_TYPE_KEY, mapper.valueToTree(new UhpType()));
		node.put(UhpPin.JSON_DESCRIPTION_KEY, "description");
		node.put(UhpPin.JSON_MNEMONIC_KEY, "mnemonic");
		return node;
	}
}
