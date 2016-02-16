package org.unbiquitous.unbihealth.core.drivers;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unbiquitous.unbihealth.core.uhp.UhpPin;

public class PinDAOTest {
	private PinDAO dao = null;

	@Before
	public void setUp() throws Exception {
		dao = new PinDAO();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testEmpty() {
		assertThat(dao.list()).isEmpty();
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowForNullPin() {
		dao.put(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowForNullPinName() {
		dao.put(new UhpPin(null));
	}

	@Test
	public void testPutRemoveListClear() {
		dao.put(new UhpPin("pin1"));
		assertThat(dao.list()).containsOnly(new UhpPin("pin1"));
		dao.put(new UhpPin("pin2"));
		assertThat(dao.list()).containsOnly(new UhpPin("pin1"), new UhpPin("pin2"));
		dao.put(new UhpPin("pin3"));
		assertThat(dao.list()).containsOnly(new UhpPin("pin1"), new UhpPin("pin2"), new UhpPin("pin3"));
		dao.remove("pin1");
		assertThat(dao.list()).containsOnly(new UhpPin("pin2"), new UhpPin("pin3"));
		dao.clear();
		assertThat(dao.list()).isEmpty();
	}

	@Test
	public void testFind() {
		UhpPin pin1 = new UhpPin("pin1");
		UhpPin pin2 = new UhpPin("pin2");
		dao.put(pin1);
		assertThat(dao.find("pin1")).isEqualTo(pin1);
		assertThat(dao.find("pin2")).isNull();
		dao.put(pin2);
		assertThat(dao.find("pin2")).isEqualTo(pin2);
	}
}
