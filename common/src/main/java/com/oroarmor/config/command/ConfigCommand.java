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

package com.oroarmor.config.command;

import java.util.Arrays;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.oroarmor.config.ArrayConfigItem;
import com.oroarmor.config.Config;
import com.oroarmor.config.ConfigItem;
import com.oroarmor.config.ConfigItem.Type;
import com.oroarmor.config.ConfigItemGroup;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.HoverEvent.Action;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

/**
 * Creates a com.oroarmor.config.command register callback that is based of of a config. <br>
 * <br>
 * Register with:
 * <code>CommandRegistrationCallback.EVENT.register(new ConfigCommand(yourConfigInstance));</code>
 *
 * @author Eli Orona
 */
public class ConfigCommand {

    /**
     * The config
     */
    protected final Config config;

    /**
     * Creates a new ConfigCommand with the config
     *
     * @param config The config
     */
    public ConfigCommand(Config config) {
        this.config = config;
    }

    protected MutableText createItemText(ConfigItem<?> item, ConfigItemGroup group) {
        MutableText configListText = new LiteralText("");
        boolean atDefault = item.getDefaultValue().equals(item.getValue());
        configListText.append(new LiteralText("[" + I18n.translate(item.getDetails()) + "]"));
        configListText.append(" : ");
        configListText.append(new LiteralText("[" + item.getValue() + "]")
                .formatted(atDefault ? Formatting.GREEN : Formatting.DARK_GREEN)
                .styled(s -> s.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, new LiteralText((atDefault ? "At Default " : "") + "Value: " + (atDefault ? item.getDefaultValue() + ". Click to change value." : item.getValue() + ". Click to reset value."))))
                        .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + config.getID() + " " + group.getName() + " " + item.getName() + " " + (atDefault ? "value" : item.getDefaultValue())))));
        return configListText;
    }

    protected MutableText createArrayItemText(ArrayConfigItem<?> item, ConfigItemGroup group) {
        MutableText configListText = new LiteralText("");
        boolean atDefault = Arrays.equals(item.getDefaultValue(), item.getValue());
        configListText.append(new LiteralText("[" + I18n.translate(item.getDetails()) + "]"));
        configListText.append(" : ");

        StringBuilder array = new StringBuilder();
        for (int i = 0; i < item.getValue().length; i++) {
            if (i != 0) {
                array.append(", ");
            }
            array.append(item.getValue(i));
        }

        StringBuilder defaultArray = new StringBuilder();
        for (int i = 0; i < item.getDefaultValue().length; i++) {
            if (i != 0) {
                defaultArray.append(", ");
            }
            defaultArray.append(item.getDefaultValue(i));
        }

        configListText.append(new LiteralText("[" + array.toString() + "]")
                .formatted(atDefault ? Formatting.GREEN : Formatting.DARK_GREEN)
                .styled(s -> s.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, new LiteralText((atDefault ? "At Default " : "") + "Value: " + (atDefault ? defaultArray.toString() + ". Click to change value." : array.toString() + ". Click to reset value."))))
                        .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + config.getID() + " " + group.getName() + " " + item.getName() + " " + (atDefault ? "value" : item.getDefaultValue())))));
        return configListText;
    }

    private int listConfigGroup(CommandContext<ServerCommandSource> c, ConfigItemGroup group) {
        MutableText configList = new LiteralText("");

        configList.append(new LiteralText(group.getName() + "\n").formatted(Formatting.BOLD));
        for (ConfigItem<?> item : group.getConfigs())
            parseConfigItemText(configList, group, item, "  ");

        configList.append("/");

        try {
            c.getSource().getPlayer().sendSystemMessage(configList, Util.NIL_UUID);
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }

        return 1;
    }

    private int listConfigGroups(CommandContext<ServerCommandSource> c) {

        MutableText configList = new LiteralText("");

        for (ConfigItemGroup group : config.getConfigs()) {
            configList.append(new LiteralText(group.getName() + "\n").formatted(Formatting.BOLD));
            for (ConfigItem<?> item : group.getConfigs()) {
                parseConfigItemText(configList, group, item, "  ");
            }
            configList.append("/");
        }

        try {
            c.getSource().getPlayer().sendSystemMessage(configList, Util.NIL_UUID);
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }

        return 1;
    }

    protected void parseConfigItemText(MutableText configList, ConfigItemGroup group, ConfigItem<?> item, String padding) {
        configList.append(padding);
        configList.append("|--> ");
        if (item.getType() == Type.GROUP) {
            configList.append(new LiteralText(item.getName() + "\n").formatted(Formatting.BOLD));
            for (ConfigItem<?> item2 : ((ConfigItemGroup) item).getConfigs()) {
                parseConfigItemText(configList, (ConfigItemGroup) item, item2, padding + "| ");
            }
            configList.append(padding + "/\n");
        } else {
            configList.append(item instanceof ArrayConfigItem ? createArrayItemText((ArrayConfigItem<?>) item, group) : createItemText(item, group));
            configList.append("\n");
        }
    }

    protected int listItem(CommandContext<ServerCommandSource> c, ConfigItem<?> item, ConfigItemGroup group) {
        try {
            c.getSource().getPlayer().sendSystemMessage(createItemText(item, group), Util.NIL_UUID);
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }

        return 1;
    }

    public void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = literal(config.getID()).requires(p -> p.hasPermissionLevel(2)).executes(this::listConfigGroups);

        for (ConfigItemGroup group : config.getConfigs()) {
            parseConfigItemGroupCommand(literalArgumentBuilder, group);
        }

        dispatcher.register(literalArgumentBuilder);
    }

    @SuppressWarnings("unchecked")
    protected void parseConfigItemGroupCommand(LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder, ConfigItemGroup group) {
        LiteralArgumentBuilder<ServerCommandSource> configGroupCommand = literal(group.getName()).executes((c) -> listConfigGroup(c, group));
        for (ConfigItem<?> item : group.getConfigs()) {
            if (item.getType() == Type.GROUP) {
                parseConfigItemGroupCommand(configGroupCommand, (ConfigItemGroup) item);
            } else {
                LiteralArgumentBuilder<ServerCommandSource> configItemCommand = literal(item.getName()).executes((c) -> listItem(c, item, group));

                if (item instanceof ArrayConfigItem) {
                    RequiredArgumentBuilder<ServerCommandSource, Integer> arrayIndex = argument("index", IntegerArgumentType.integer(0, ((ArrayConfigItem<?>) item).getValue().length));

                    switch (item.getType()) {
                        case BOOLEAN:
                            arrayIndex.then(argument("boolean", BoolArgumentType.bool()).executes(c -> setArrayItemBoolean(c, (ArrayConfigItem<Boolean>) item, group)));
                            break;
                        case DOUBLE:
                            arrayIndex.then(argument("double", DoubleArgumentType.doubleArg()).executes(c -> setArrayItemDouble(c, (ArrayConfigItem<Double>) item, group)));
                            break;
                        case INTEGER:
                            arrayIndex.then(argument("int", IntegerArgumentType.integer()).executes(c -> setArrayItemInteger(c, (ArrayConfigItem<Integer>) item, group)));
                            break;
                        case STRING:
                            arrayIndex.then(argument("string", StringArgumentType.string()).executes(c -> setArrayItemString(c, (ArrayConfigItem<String>) item, group)));
                            break;
                        case ENUM:
                            Enum<?>[] enums = ((Enum<?>) item.getValue()).getClass().getEnumConstants();
                            for (Enum<?> _enum : enums) {
                                arrayIndex.then(literal(_enum.toString()).executes(c -> setArrayItemEnum(c, _enum, (ArrayConfigItem<Enum<?>>) item, group)));
                            }
                            break;

                        default:
                            break;
                    }
                    configItemCommand.then(arrayIndex);
                } else {
                    switch (item.getType()) {
                        case BOOLEAN:
                            configItemCommand.then(argument("boolean", BoolArgumentType.bool()).executes(c -> setItemBoolean(c, (ConfigItem<Boolean>) item, group)));
                            break;
                        case DOUBLE:
                            configItemCommand.then(argument("double", DoubleArgumentType.doubleArg()).executes(c -> setItemDouble(c, (ConfigItem<Double>) item, group)));
                            break;
                        case INTEGER:
                            configItemCommand.then(argument("int", IntegerArgumentType.integer()).executes(c -> setItemInteger(c, (ConfigItem<Integer>) item, group)));
                            break;
                        case STRING:
                            configItemCommand.then(argument("string", StringArgumentType.string()).executes(c -> setItemString(c, (ConfigItem<String>) item, group)));
                            break;
                        case ENUM:
                            Enum<?>[] enums = ((Enum<?>) item.getValue()).getClass().getEnumConstants();
                            for (Enum<?> _enum : enums) {
                                configItemCommand.then(literal(_enum.toString()).executes(c -> setItemEnum(c, _enum, (ConfigItem<Enum<?>>) item, group)));
                            }
                            break;

                        default:
                            break;
                    }
                }
                configGroupCommand.then(configItemCommand);
            }
        }
        literalArgumentBuilder.then(configGroupCommand);
    }

    protected int setItemBoolean(CommandContext<ServerCommandSource> c, ConfigItem<Boolean> item, ConfigItemGroup group) {
        boolean result = BoolArgumentType.getBool(c, "boolean");
        return setAndSaveConfig(c, item, result);
    }

    protected int setItemDouble(CommandContext<ServerCommandSource> c, ConfigItem<Double> item, ConfigItemGroup group) {
        double result = DoubleArgumentType.getDouble(c, "double");
        return setAndSaveConfig(c, item, result);
    }

    protected int setItemInteger(CommandContext<ServerCommandSource> c, ConfigItem<Integer> item, ConfigItemGroup group) {
        int result = IntegerArgumentType.getInteger(c, "int");
        return setAndSaveConfig(c, item, result);
    }

    private int setItemString(CommandContext<ServerCommandSource> c, ConfigItem<String> item, ConfigItemGroup group) {
        String result = StringArgumentType.getString(c, "string");
        return setAndSaveConfig(c, item, result);
    }

    private int setItemEnum(CommandContext<ServerCommandSource> c, Enum<?> _enum, ConfigItem<Enum<?>> item, ConfigItemGroup group) {
        return setAndSaveConfig(c, item, _enum);
    }

    protected <T> int setAndSaveConfig(CommandContext<ServerCommandSource> c, ConfigItem<T> item, T result) {
        item.setValue(result);
        config.saveConfigToFile();
        return 1;
    }

    protected int setArrayItemBoolean(CommandContext<ServerCommandSource> c, ArrayConfigItem<Boolean> item, ConfigItemGroup group) {
        int index = IntegerArgumentType.getInteger(c, "index");
        boolean result = BoolArgumentType.getBool(c, "boolean");
        return setArrayAndSaveConfig(c, item, result, index);
    }

    protected int setArrayItemDouble(CommandContext<ServerCommandSource> c, ArrayConfigItem<Double> item, ConfigItemGroup group) {
        int index = IntegerArgumentType.getInteger(c, "index");
        double result = DoubleArgumentType.getDouble(c, "double");
        return setArrayAndSaveConfig(c, item, result, index);
    }

    protected int setArrayItemInteger(CommandContext<ServerCommandSource> c, ArrayConfigItem<Integer> item, ConfigItemGroup group) {
        int index = IntegerArgumentType.getInteger(c, "index");
        int result = IntegerArgumentType.getInteger(c, "int");
        return setArrayAndSaveConfig(c, item, result, index);
    }

    private int setArrayItemString(CommandContext<ServerCommandSource> c, ArrayConfigItem<String> item, ConfigItemGroup group) {
        int index = IntegerArgumentType.getInteger(c, "index");
        String result = StringArgumentType.getString(c, "string");
        return setArrayAndSaveConfig(c, item, result, index);
    }

    private int setArrayItemEnum(CommandContext<ServerCommandSource> c, Enum<?> _enum, ArrayConfigItem<Enum<?>> item, ConfigItemGroup group) {
        int index = IntegerArgumentType.getInteger(c, "index");
        return setArrayAndSaveConfig(c, item, _enum, index);
    }

    protected <T> int setArrayAndSaveConfig(CommandContext<ServerCommandSource> c, ArrayConfigItem<T> item, T result, int index) {
        item.setValue(result, index);
        config.saveConfigToFile();
        return 1;
    }
}
