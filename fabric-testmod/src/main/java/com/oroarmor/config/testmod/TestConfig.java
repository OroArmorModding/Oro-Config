/*
 * MIT License
 *
 * Copyright (c) 2021 OroArmor (Eli Orona)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.oroarmor.config.testmod;

import java.io.File;
import java.util.List;

import com.oroarmor.config.*;

import net.fabricmc.loader.api.FabricLoader;
import static com.google.common.collect.ImmutableList.of;

public class TestConfig extends Config {
    public static final ConfigItemGroup mainGroup = new ConfigGroupLevel1();

    public static final List<ConfigItemGroup> configs = of(mainGroup);

    public TestConfig() {
        super(configs, new File(FabricLoader.getInstance().getConfigDir().toFile(), "oroarmor_config_testmod.json"), "oroarmor_config_testmod");
    }

    public static class ConfigGroupLevel1 extends ConfigItemGroup {
        public static final EnumConfigItem<EnumTest> testEnum = new EnumConfigItem<>("test_enum", EnumTest.A, "test_enum");
        public static final BooleanConfigItem testItem = new BooleanConfigItem("test_boolean", true, "test_boolean");

        public static final ArrayConfigItem<Integer> testArray = new ArrayConfigItem<>("test_array", new Integer[]{1, 2, 3}, "test_array");

        public ConfigGroupLevel1() {
            super(of(new NestedGroup(), testItem, testEnum, testArray), "group");
        }

        public static class NestedGroup extends ConfigItemGroup {
            public static final IntegerConfigItem nestedItem = new IntegerConfigItem("test_int", 0, "test_integer");

            public NestedGroup() {
                super(of(nestedItem, new TripleNested()), "nested");
            }

            public static class TripleNested extends ConfigItemGroup {
                public static final StringConfigItem testString = new StringConfigItem("test_string", "Default", "test_string");

                public TripleNested() {
                    super(of(testString), "triple");
                }
            }
        }
    }
}
