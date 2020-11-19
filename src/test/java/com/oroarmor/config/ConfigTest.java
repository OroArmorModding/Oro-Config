package com.oroarmor.config;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ConfigTest {
	@Test
	public void getValue() {
		Config testConfig = new TestConfig();
		assertEquals("Get value gets correct value", TestConfig.ConfigGroupLevel1.testItem.getDefaultValue(), testConfig.getValue("group.test_boolean", Boolean.class));
	}
}
