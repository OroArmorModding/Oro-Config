package com.oroarmor.config.testmod;

import com.oroarmor.config.screen.ForgeConfigScreen;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

@Mod("oroconfig-testmod")
public class OroConfigTestMod {
    public OroConfigTestMod() {
        TestMod.initialize();

        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> new ForgeConfigScreen(TestMod.CONFIG));
    }
}
