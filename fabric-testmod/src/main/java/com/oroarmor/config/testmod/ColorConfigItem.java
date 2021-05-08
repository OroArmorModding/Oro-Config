package com.oroarmor.config.testmod;

import java.util.function.Consumer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.oroarmor.config.Config;
import com.oroarmor.config.ConfigItem;
import com.oroarmor.config.ConfigItemGroup;
import com.oroarmor.config.command.ConfigItemCommands;
import com.oroarmor.config.screen.ConfigScreenBuilders;
import me.shedaniel.math.Color;
import org.jetbrains.annotations.Nullable;

import net.minecraft.command.CommandSource;
import net.minecraft.text.TranslatableText;

public class ColorConfigItem extends ConfigItem<Color> {
    public ColorConfigItem(String name, Color defaultValue, String details) {
        super(name, defaultValue, details);
    }

    public ColorConfigItem(String name, Color defaultValue, String details, @Nullable Consumer<ConfigItem<Color>> onChange) {
        super(name, defaultValue, details, onChange);
    }

    @Override
    public void fromJson(JsonElement element) {
        JsonObject colorObject = element.getAsJsonObject();
        int r = colorObject.get("r").getAsInt();
        int g = colorObject.get("g").getAsInt();
        int b = colorObject.get("b").getAsInt();
        int a = colorObject.get("a").getAsInt();
        this.value = Color.ofRGBA(r, g, b, a);
    }

    @Override
    public void toJson(JsonObject object) {
        JsonObject colorObject = new JsonObject();
        colorObject.addProperty("r", this.value.getRed());
        colorObject.addProperty("g", this.value.getGreen());
        colorObject.addProperty("b", this.value.getBlue());
        colorObject.addProperty("a", this.value.getAlpha());
        object.add(this.name, colorObject);
    }

    @Override
    public <T1> boolean isValidType(Class<T1> clazz) {
        return clazz == Color.class;
    }

    @Override
    public String getCommandValue() {
        return this.value.toString();
    }

    @Override
    public String getCommandDefaultValue() {
        return this.defaultValue.toString();
    }

    static {
        ConfigItemCommands.register(ColorConfigItem.class, new ConfigItemCommands.CommandBuilder<Color>() {
            @Override
            public <S extends CommandSource> ArgumentBuilder<S, ?> getCommand(ConfigItem<Color> configItem, ConfigItemGroup group, Config config) {
                LiteralArgumentBuilder<S> set = LiteralArgumentBuilder.literal("set");
                set.then(LiteralArgumentBuilder.<S>literal("r")
                        .then(RequiredArgumentBuilder.<S, Integer>argument("val", IntegerArgumentType.integer(0, 255))
                                .executes(context -> {
                                    configItem.setValue(Color.ofTransparent(configItem.getValue().getColor() & 0xFF00FFFF | (IntegerArgumentType.getInteger(context, "val") << 16)));
                                    return 1;
                                })
                        ));
                set.then(LiteralArgumentBuilder.<S>literal("g")
                        .then(RequiredArgumentBuilder.<S, Integer>argument("val", IntegerArgumentType.integer(0, 255))
                                .executes(context -> {
                                    configItem.setValue(Color.ofTransparent(configItem.getValue().getColor() & 0xFFFF00FF | (IntegerArgumentType.getInteger(context, "val") << 8)));
                                    return 1;
                                })
                        ));
                set.then(LiteralArgumentBuilder.<S>literal("b")
                        .then(RequiredArgumentBuilder.<S, Integer>argument("val", IntegerArgumentType.integer(0, 255))
                                .executes(context -> {
                                    configItem.setValue(Color.ofTransparent(configItem.getValue().getColor() & 0xFFFFFF00 | (IntegerArgumentType.getInteger(context, "val") << 0)));
                                    return 1;
                                })
                        ));
                set.then(LiteralArgumentBuilder.<S>literal("a")
                        .then(RequiredArgumentBuilder.<S, Integer>argument("val", IntegerArgumentType.integer(0, 255))
                                .executes(context -> {
                                    configItem.setValue(Color.ofTransparent(configItem.getValue().getColor() & 0x00FFFFFF | (IntegerArgumentType.getInteger(context, "val") << 24)));
                                    return 1;
                                })
                        ));
                return set;
            }
        });

        ConfigScreenBuilders.register(ColorConfigItem.class, (ConfigScreenBuilders.EntryBuilder<Color>) (configItem, entryBuilder, config) -> entryBuilder.startColorField(new TranslatableText(configItem.getName()), configItem.getValue()).setDefaultValue2(configItem::getDefaultValue).setSaveConsumer2(configItem::setValue).setAlphaMode(true).build());
    }
}
