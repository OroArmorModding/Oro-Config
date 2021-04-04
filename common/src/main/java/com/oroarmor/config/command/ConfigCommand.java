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

import java.util.function.Predicate;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.oroarmor.config.Config;
import com.oroarmor.config.ConfigItem;
import com.oroarmor.config.ConfigItemGroup;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.command.CommandSource;
import net.minecraft.network.MessageType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.HoverEvent.Action;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

/**
 * Creates a com.oroarmor.config.command register callback that is based of of a config. <br>
 * <br>
 * Register with:
 * <code>CommandRegistrationCallback.EVENT.register(new ConfigCommand(yourConfigInstance));</code>
 *
 * @author Eli Orona
 */
public class ConfigCommand<S extends CommandSource> {

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

    protected MutableText createItemText(ConfigItem<?> item) {
        MutableText configListText = new LiteralText("");
        configListText.append(new LiteralText("[" + I18n.translate(item.getDetails()) + "]"));
        configListText.append(" : ");
        configListText.append(new LiteralText("[" + item.getCommandValue() + "]")
                .formatted(item.atDefaultValue() ? Formatting.GREEN : Formatting.DARK_GREEN)
                .styled(s -> s.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, new LiteralText((item.atDefaultValue() ? "At Default " : "") + "Value: " + (item.atDefaultValue() ? item.getCommandDefaultValue() : item.getCommandValue()))))));
        return configListText;
    }

    private int listConfigGroup(CommandContext<S> c, ConfigItemGroup group) {
        MutableText configList = new LiteralText("");

        configList.append(new LiteralText(group.getName() + "\n").formatted(Formatting.BOLD));
        for (ConfigItem<?> item : group.getConfigs())
            parseConfigItemText(configList, item, "  ");

        configList.append("/");

        try {
            sendFeedback(c, configList);
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }

        return 1;
    }

    private int listConfigGroups(CommandContext<S> c) {
        MutableText configList = new LiteralText("");

        for (ConfigItemGroup group : config.getConfigs()) {
            configList.append(new LiteralText(group.getName() + "\n").formatted(Formatting.BOLD));
            for (ConfigItem<?> item : group.getConfigs()) {
                parseConfigItemText(configList, item, "  ");
            }
            configList.append("/");
        }

        try {
            sendFeedback(c, configList);
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }

        return 1;
    }

    protected void parseConfigItemText(MutableText configList, ConfigItem<?> item, String padding) {
        configList.append(padding);
        configList.append("|--> ");
        if (item instanceof ConfigItemGroup) {
            configList.append(new LiteralText(item.getName() + "\n").formatted(Formatting.BOLD));
            for (ConfigItem<?> item2 : ((ConfigItemGroup) item).getConfigs()) {
                parseConfigItemText(configList, item2, padding + "| ");
            }
            configList.append(padding + "/\n");
        } else {
            configList.append(createItemText(item));
            configList.append("\n");
        }
    }

    protected int listItem(CommandContext<S> c, ConfigItem<?> item) {
        try {
            Text text = createItemText(item);
            sendFeedback(c, text);
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }

        return 1;
    }

    private void sendFeedback(CommandContext<S> c, Text text) throws CommandSyntaxException {
        if (c.getSource() instanceof ServerCommandSource) {
            ((ServerCommandSource) c.getSource()).getPlayer().sendSystemMessage(text, Util.NIL_UUID);
        } else if (c.getSource().getClass().getSimpleName().toLowerCase().contains("client")) {
            MinecraftClient.getInstance().inGameHud.addChatMessage(MessageType.CHAT, text, Util.NIL_UUID);
        }
    }

    public void register(CommandDispatcher<S> dispatcher, Predicate<S> usable) {
        LiteralArgumentBuilder<S> literalArgumentBuilder = LiteralArgumentBuilder.<S>literal(config.getID()).requires(usable).executes(this::listConfigGroups);

        for (ConfigItemGroup group : config.getConfigs()) {
            parseConfigItemGroupCommand(literalArgumentBuilder, group);
        }

        dispatcher.register(literalArgumentBuilder);
    }

    protected void parseConfigItemGroupCommand(LiteralArgumentBuilder<S> literalArgumentBuilder, ConfigItemGroup group) {
        LiteralArgumentBuilder<S> configGroupCommand = LiteralArgumentBuilder.<S>literal(group.getName()).executes((c) -> listConfigGroup(c, group));
        for (ConfigItem<?> item : group.getConfigs()) {
            if (item instanceof ConfigItemGroup) {
                parseConfigItemGroupCommand(configGroupCommand, (ConfigItemGroup) item);
            } else {
                LiteralArgumentBuilder<S> configItemCommand = LiteralArgumentBuilder.<S>literal(item.getName()).executes((c) -> listItem(c, item));
                configItemCommand.then(getCommand(item, group, config));
                configGroupCommand.then(configItemCommand);
            }
        }
        literalArgumentBuilder.then(configGroupCommand);
    }

    private <T> ArgumentBuilder<S, ?> getCommand(ConfigItem<T> configItem, ConfigItemGroup group, Config config) {
        return ConfigItemCommands.getCommandBuilder(configItem).getCommand(configItem, group, config);
    }
}
