package me.autobot.itementitypicker.client;

import me.autobot.itementitypicker.command.Commands;
import net.fabricmc.api.ClientModInitializer;

public class ItementitypickerClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        new Commands();
    }
}
