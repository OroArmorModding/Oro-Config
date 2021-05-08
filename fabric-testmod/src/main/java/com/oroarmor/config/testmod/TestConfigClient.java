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

import com.oroarmor.config.Config;
import com.oroarmor.config.ConfigItemGroup;
import com.oroarmor.config.DoubleConfigItem;
import me.shedaniel.math.Color;

import net.fabricmc.loader.api.FabricLoader;
import static com.google.common.collect.ImmutableList.of;

public class TestConfigClient extends Config {
    public static final ConfigItemGroup mainGroup = new ConfigGroupLevel1();

    public static final List<ConfigItemGroup> configs = of(mainGroup);

    public TestConfigClient() {
        super(configs, new File(FabricLoader.getInstance().getConfigDir().toFile(), "oroarmor_config_testmod_client.json"), "oroarmor_config_testmod_client");
    }

    public static class ConfigGroupLevel1 extends ConfigItemGroup {
        public static final DoubleConfigItem testDouble = new DoubleConfigItem("test_double", 0d, "test_double", null, -1, 1);

        public static final ColorConfigItem testColor = new ColorConfigItem("test_color", Color.ofTransparent(0x00FF0000), "test_color");

        public ConfigGroupLevel1() {
            super(of(testDouble, testColor), "group");
        }
    }
}
