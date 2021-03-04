package com.oroarmor.config.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
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
            configList.append(createItemText(item, group));
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
}
