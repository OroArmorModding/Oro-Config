package com.oroarmor.config.command;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

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
import com.oroarmor.config.ConfigItemGroup;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.HoverEvent.Action;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

public class ConfigCommand implements CommandRegistrationCallback {

	protected final Config config;

	public ConfigCommand(Config config) {
		this.config = config;
	}

	protected MutableText createItemText(ConfigItem<?> item, ConfigItemGroup group) {
		MutableText configListText = new LiteralText("");
		boolean atDefault = item.getDefaultValue().equals(item.getValue());
		configListText.append(new LiteralText("[" + I18n.translate(item.getDetails()) + "]"));
		configListText.append(" : ");
		configListText.append(new LiteralText("[" + item.getValue() + "]").formatted(atDefault ? Formatting.GREEN : Formatting.DARK_GREEN).styled(s -> s.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, new LiteralText((atDefault ? "At Default " : "") + "Value: " + (atDefault ? item.getDefaultValue() + ". Click to change value." : item.getValue() + ". Click to reset value.")))).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/netherite_plus " + group.getName() + " " + item.getName() + " " + (atDefault ? "value" : item.getDefaultValue())))));
		return configListText;
	}

	private int listConfigGroup(CommandContext<ServerCommandSource> c, ConfigItemGroup group) {
		MutableText configList = new LiteralText("");

		configList.append(new LiteralText(group.getName() + "\n").formatted(Formatting.BOLD));
		for (ConfigItem<?> item : group.getConfigs()) {
			configList.append("  |--> ");
			configList.append(createItemText(item, group));
			configList.append("\n");
		}

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
				parseConfigItemText(configList, group, item, 1);
			}
		}

		try {
			c.getSource().getPlayer().sendSystemMessage(configList, Util.NIL_UUID);
		} catch (CommandSyntaxException e) {
			e.printStackTrace();
		}

		return 1;
	}

	protected void parseConfigItemText(MutableText configList, ConfigItemGroup group, ConfigItem<?> item, int i) {
		for (int j = 0; j < i; j++) {
			configList.append("  ");
		}

		configList.append("|--> ");
		switch (item.getType()) {
		case GROUP:
			configList.append(new LiteralText(item.getName() + "\n").formatted(Formatting.BOLD));
			for (ConfigItem<?> item2 : ((ConfigItemGroup) item).getConfigs()) {
				parseConfigItemText(configList, (ConfigItemGroup) item, item2, i + 1);
			}
			break;
		default:
			configList.append(createItemText(item, group));
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

	@Override
	public void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
		LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = literal(config.getID()).requires(p -> p.hasPermissionLevel(2)).executes(c -> listConfigGroups(c));

		for (ConfigItemGroup group : config.getConfigs()) {
			parseConfigItemGroupCommand(literalArgumentBuilder, group);
		}

		dispatcher.register(literalArgumentBuilder);
	}

	@SuppressWarnings("unchecked")
	protected void parseConfigItemGroupCommand(LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder, ConfigItemGroup group) {
		LiteralArgumentBuilder<ServerCommandSource> configGroupCommand = literal(group.getName()).executes((c) -> listConfigGroup(c, group));
		for (ConfigItem<?> item : group.getConfigs()) {
			LiteralArgumentBuilder<ServerCommandSource> configItemCommand = literal(item.getName()).executes((c) -> listItem(c, item, group));

			switch (item.getType()) {
			case BOOLEAN:
				configItemCommand.then(argument("boolean", BoolArgumentType.bool()).executes(c -> setItemBoolean(c, (ConfigItem<Boolean>) item, group)));
				break;
			case DOUBLE:
				configItemCommand.then(argument("double", DoubleArgumentType.doubleArg()).executes(c -> setItemDouble(c, (ConfigItem<Double>) item, group)));
				break;
			case GROUP:
				parseConfigItemGroupCommand(literalArgumentBuilder, (ConfigItemGroup) item);
				break;
			case INTEGER:
				configItemCommand.then(argument("int", IntegerArgumentType.integer()).executes(c -> setItemInteger(c, (ConfigItem<Integer>) item, group)));
				break;
			case STRING:
				configItemCommand.then(argument("string", StringArgumentType.string()).executes(c -> setItemString(c, (ConfigItem<String>) item, group)));
				break;
			}
			configGroupCommand.then(configItemCommand);
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

	protected <T> int setAndSaveConfig(CommandContext<ServerCommandSource> c, ConfigItem<T> item, T result) {
		item.setValue(result);
		config.saveConfigToFile();
		return 1;
	}
}
