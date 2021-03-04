package com.oroarmor.config.testmod;

import com.oroarmor.config.Config;
import com.oroarmor.config.command.ConfigCommand;
import com.oroarmor.config.screen.ForgeConfigScreen;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;

import net.minecraft.server.command.CommandManager;

@Mod("oroconfig-testmod")
public class OroConfigTestMod {
    public static final Config CONFIG = new TestConfig();

    public OroConfigTestMod() {
        CONFIG.readConfigFromFile();
        MinecraftForge.EVENT_BUS.addListener(OroConfigTestMod::serverStoppedEvent);
        MinecraftForge.EVENT_BUS.addListener(OroConfigTestMod::registerCommandEvent);
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> new ForgeConfigScreen(CONFIG));
    }

    public static void serverStoppedEvent(FMLServerStoppedEvent event) {
        CONFIG.saveConfigToFile();
    }

    public static void registerCommandEvent(RegisterCommandsEvent event) {
        new ConfigCommand(CONFIG).register(event.getDispatcher(), event.getEnvironment() == CommandManager.RegistrationEnvironment.DEDICATED);
    }
}
