package com.oroarmor.config.testmod;

import net.fabricmc.api.ModInitializer;

public class OroConfigTestMod implements ModInitializer {
    @Override
    public void onInitialize() {
        TestMod.initialize();
    }
}
