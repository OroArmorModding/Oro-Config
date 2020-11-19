package com.oroarmor.config;

import static com.google.common.collect.ImmutableList.of;

import java.util.List;

public class TestConfig extends Config {
	public static final ConfigItemGroup mainGroup = new ConfigGroupLevel1();

	public static final List<ConfigItemGroup> configs = of(mainGroup);

	public TestConfig() {
		super(configs, null, "oroarmor_config_testmod");
	}

	public static class ConfigGroupLevel1 extends ConfigItemGroup {
		public static class NestedGroup extends ConfigItemGroup {
			public static class TripleNested extends ConfigItemGroup {
				public static final ConfigItem<String> testString = new ConfigItem<String>("test_string", "Default", "test_string");

				public TripleNested() {
					super(of(testString), "triple");
				}
			}

			public static final ConfigItem<Integer> nestedItem = new ConfigItem<Integer>("test_int", 0, "test_integer");

			public NestedGroup() {
				super(of(nestedItem, new TripleNested()), "nested");
			}
		}

		public static final ConfigItem<Boolean> testItem = new ConfigItem<Boolean>("test_boolean", true, "test_boolean");

		public ConfigGroupLevel1() {
			super(of(new NestedGroup(), testItem), "group");
		}
	}
}
