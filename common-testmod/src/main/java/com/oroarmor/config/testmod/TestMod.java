package com.oroarmor.config.testmod;

import com.oroarmor.config.Config;
import com.oroarmor.config.command.ConfigCommand;
import me.shedaniel.architectury.event.events.CommandRegistrationEvent;
import me.shedaniel.architectury.event.events.LifecycleEvent;

import net.minecraft.server.command.CommandManager;

public class TestMod {
    public static Config CONFIG = new TestConfig();

    public static void initialize() {
        CONFIG.readConfigFromFile();
        LifecycleEvent.SERVER_STOPPED.register(instance -> CONFIG.saveConfigToFile());
        CommandRegistrationEvent.EVENT.register((dispatcher, selection) -> new ConfigCommand(CONFIG).register(dispatcher, selection == CommandManager.RegistrationEnvironment.DEDICATED));
    }
}
