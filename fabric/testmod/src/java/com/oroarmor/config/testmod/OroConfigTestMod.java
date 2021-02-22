package com.oroarmor.config.testmod;

import com.oroarmor.config.Config;
import com.oroarmor.config.command.ConfigCommand;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

public class OroConfigTestMod implements ModInitializer {
    public static final Config CONFIG = new TestConfig();

    @Override
    public void onInitialize() {
        CONFIG.readConfigFromFile();
        CONFIG.saveConfigToFile();

        System.out.println(CONFIG);

        CommandRegistrationCallback.EVENT.register(new ConfigCommand(CONFIG)::register);
    }
}
