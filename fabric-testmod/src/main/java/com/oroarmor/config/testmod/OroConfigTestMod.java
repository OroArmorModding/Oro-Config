package com.oroarmor.config.testmod;

import com.oroarmor.config.Config;
import com.oroarmor.config.command.ConfigCommand;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class OroConfigTestMod implements ModInitializer {
    public static Config CONFIG = new TestConfig();
    @Override
    public void onInitialize() {
        CONFIG.readConfigFromFile();
        ServerLifecycleEvents.SERVER_STOPPED.register(instance -> CONFIG.saveConfigToFile());
        CommandRegistrationCallback.EVENT.register(new ConfigCommand(CONFIG)::register);
    }
}
