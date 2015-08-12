package org.unbiquitous.unbihealth.core.drivers;

import java.util.List;

import org.unbiquitous.uos.core.InitialProperties;
import org.unbiquitous.uos.core.adaptabitilyEngine.Gateway;
import org.unbiquitous.uos.core.driverManager.UosDriver;
import org.unbiquitous.uos.core.messageEngine.dataType.UpDriver;

public class HealthDataDriver implements UosDriver {

	public UpDriver getDriver() {
		return null;
	}

	public List<UpDriver> getParent() {
		return null;
	}

	public void init(Gateway gateway, InitialProperties properties, String instanceId) {
	}

	public void destroy() {
	}
}
