package com.oroarmor.config;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConfigTest {
    @Test
    public void getValue() {
        Config testConfig = new TestConfig();
        assertEquals("Get value gets correct value", TestConfig.ConfigGroupLevel1.testItem.getDefaultValue(), testConfig.getValue("group.test_boolean", Boolean.class));
        assertEquals("Get value gets correct value", TestConfig.ConfigGroupLevel1.NestedGroup.nestedItem.getDefaultValue(), testConfig.getValue("group.nested.test_int", Integer.class));
        assertEquals("Get value gets correct enum value", TestConfig.ConfigGroupLevel1.testEnum.getDefaultValue(), testConfig.getValue("group.test_enum", EnumTest.class));
    }
}
