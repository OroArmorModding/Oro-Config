package com.oroarmor.config.screen;

import java.util.function.BiFunction;

import com.oroarmor.config.Config;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.TranslatableText;

public class ForgeConfigScreen extends ConfigScreen implements BiFunction<MinecraftClient, Screen, Screen> {
     public ForgeConfigScreen(Config config) {
        super(config);
    }

    @Override
    public Screen apply(MinecraftClient minecraftClient, Screen screen) {
        ConfigBuilder builder = ConfigBuilder.create().setParentScreen(screen).setTitle(new TranslatableText("config." + config.getID()));
        builder.setSavingRunnable(config::saveConfigToFile);

        ConfigEntryBuilder entryBuilder = ConfigEntryBuilder.create();

        config.getConfigs().forEach(group -> {
            ConfigCategory groupCategory = createCategory(builder, "config." + config.getID() + "." + group.getName());
            group.getConfigs().forEach(configItem -> setupConfigItem(configItem, groupCategory, entryBuilder));
        });

        return builder.build();
    }
}
