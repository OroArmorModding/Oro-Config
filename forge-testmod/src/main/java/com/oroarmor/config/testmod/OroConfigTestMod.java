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
