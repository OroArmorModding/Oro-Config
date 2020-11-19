package com.oroarmor.config;

import static org.junit.Assert.assertEquals;

import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonPrimitive;
import com.oroarmor.config.ConfigItem.Type;

public class ConfigItemTest {
	@Test
	public void testTypeGetType() {
		Map<Object, Type> objectsToTypes = ImmutableMap.of("String", Type.STRING, 123, Type.INTEGER, 0.5d, Type.DOUBLE, true, Type.BOOLEAN, new ConfigItemGroup(null, null), Type.GROUP);
		for (Entry<Object, Type> e : objectsToTypes.entrySet()) {
			assertEquals("Type " + e.getValue() + " is recieved correctly", e.getValue(), Type.getTypeFrom(e.getKey()));
		}
	}

	@Test
	public void gettersAreCorrect() {
		Integer defaultValue = 10;
		String name = "name";
		String details = "details";
		ConfigItem<Integer> configItem = new ConfigItem<Integer>(name, defaultValue, details);

		assertEquals("Default Value", defaultValue, configItem.getDefaultValue());
		assertEquals("Name", name, configItem.getName());
		assertEquals("Details", details, configItem.getDetails());
		assertEquals("To String", name + ":" + defaultValue, configItem.toString());
	}

	@Test
	public void settingValue() {
		Integer defaultValue = 10;
		String name = "name";
		String details = "details";
		ConfigItem<Integer> configItem = new ConfigItem<Integer>(name, defaultValue, details, (ci) -> ci.value = (ci.getValue() / 2));
		configItem.setValue(2 * defaultValue);

		assertEquals("Correct value after setting and with consumer", defaultValue, configItem.getValue());
	}

	@Test
	public void readFromJSON() {
		ConfigItem<Integer> test = new ConfigItem<Integer>("name", 0, "details");
		test.fromJson(new JsonPrimitive(10));
		assertEquals("Correct value from json", (Integer) 10, test.getValue());
	}
}
