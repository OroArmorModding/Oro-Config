package com.oroarmor.config.command;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class ClientCommandHelper {
    public void registerOpenScreen() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (ClientConfigCommand.openScreen != null) {
                client.openScreen(ClientConfigCommand.openScreen);
                ClientConfigCommand.openScreen = null;
            }
        });
    }
}
