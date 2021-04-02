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

package com.oroarmor.config.screen;


import com.oroarmor.config.Config;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;

import net.minecraft.text.TranslatableText;

/**
 * This class allows for the easy addition of a Mod Menu config screen to your
 * mod. The abstract modifier is so that your {@link ModMenuConfigScreen} can be
 * used as a entry point for modmenu, as you need to set the config in the
 * constructor for this to work. <br>
 * <br>
 * Add this to your entrypoint list in {@code fabric.mod.json}: <br>
 * <code>
 * "modmenu" : [ <br>
 * &emsp;"your.package.structure.YourModMenuConfigScreen" <br>
 * ]
 * </code>
 *
 * @author Eli Orona
 */
public abstract class ModMenuConfigScreen extends ConfigScreen implements ModMenuApi {

    /**
     * Creates a new {@link ModMenuConfigScreen}
     *
     * @param config The config
     */
    public ModMenuConfigScreen(Config config) {
        super(config);
    }


    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return screen -> {
            ConfigBuilder builder = ConfigBuilder.create().setParentScreen(screen).setTitle(new TranslatableText("config." + config.getID()));
            builder.setSavingRunnable(config::saveConfigToFile);

            ConfigEntryBuilder entryBuilder = ConfigEntryBuilder.create();

            config.getConfigs().forEach(group -> {
                ConfigCategory groupCategory = createCategory(builder, "config." + config.getID() + "." + group.getName());
                group.getConfigs().forEach(configItem -> setupConfigItem(configItem, groupCategory, entryBuilder));
            });

            return builder.build();
        };
    }
}
