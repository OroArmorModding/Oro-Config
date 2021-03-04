package com.oroarmor.config.screen;


import com.oroarmor.config.Config;
import com.oroarmor.config.screen.ConfigScreen;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;

import net.minecraft.text.TranslatableText;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;

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
