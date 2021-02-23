package com.oroarmor.config;

import java.util.Map.Entry;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonPrimitive;
import com.oroarmor.config.ConfigItem.Type;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConfigItemTest {
    @Test
    public void testTypeGetType() {
        ImmutableMap<Object, Object> objectsToTypes = ImmutableMap.builder()
                .put("String", Type.STRING)
                .put(123, Type.INTEGER)
                .put(0.5d, Type.DOUBLE)
                .put(true, Type.BOOLEAN)
                .put(new ConfigItemGroup(null, null), Type.GROUP)
                .put(EnumTest.A, Type.ENUM)
                .build();
        for (Entry<Object, Object> e : objectsToTypes.entrySet()) {
            assertEquals("Type " + e.getValue() + " is recieved correctly", e.getValue(), Type.getTypeFrom(e.getKey()));
        }
    }

    @Test
    public void gettersAreCorrect() {
        Integer defaultValue = 10;
        String name = "name";
        String details = "details";
        ConfigItem<Integer> configItem = new ConfigItem<>(name, defaultValue, details);

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
        ConfigItem<Integer> configItem = new ConfigItem<>(name, defaultValue, details, (ci) -> ci.value = (ci.getValue() / 2));
        configItem.setValue(2 * defaultValue);

        assertEquals("Correct value after setting and with consumer", defaultValue, configItem.getValue());
    }

    @Test
    public void readFromJSON() {
        ConfigItem<Integer> test = new ConfigItem<>("name", 0, "details");
        test.fromJson(new JsonPrimitive(10));
        assertEquals("Correct value from json", (Integer) 10, test.getValue());
    }
}
