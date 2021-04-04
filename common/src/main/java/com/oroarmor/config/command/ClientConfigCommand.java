package com.oroarmor.config.command;

import java.util.function.Predicate;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.oroarmor.config.Config;
import com.oroarmor.config.ConfigItemGroup;
import com.oroarmor.config.screen.ConfigScreen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.command.CommandSource;

public class ClientConfigCommand<S extends CommandSource> extends ConfigCommand<S>{
    public static Screen openScreen;

    /**
     * Creates a new ConfigCommand with the config
     *
     * @param config The config
     */
    public ClientConfigCommand(Config config) {
        super(config);

    }

    @Override
    public void register(CommandDispatcher<S> dispatcher, Predicate<S> usable) {
        LiteralArgumentBuilder<S> literalArgumentBuilder = LiteralArgumentBuilder.<S>literal(config.getID()).requires(usable).executes(this::listConfigGroups);

        for (ConfigItemGroup group : config.getConfigs()) {
            parseConfigItemGroupCommand(literalArgumentBuilder, group);
        }

        literalArgumentBuilder.then(LiteralArgumentBuilder.<S>literal("gui").executes(context -> {
            openScreen = new ConfigScreen(config){}.createScreen(MinecraftClient.getInstance().currentScreen);
            return 1;
        }));

        dispatcher.register(literalArgumentBuilder);
    }
}
