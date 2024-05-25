package me.autobot.itementitypicker.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.autobot.itementitypicker.config.ConfigManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

import java.util.Arrays;

public class Commands {
    public Commands() {
        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> {
            LiteralCommandNode<FabricClientCommandSource> rootNode = ClientCommandManager
                    .literal("picker")
                    .executes(context -> {
                        ConfigManager.MOD_ENABLE = !ConfigManager.MOD_ENABLE;
                        context.getSource().sendFeedback(Component.literal("Picker " + (ConfigManager.MOD_ENABLE ? "enabled" : "disabled")));
                        return 1;
                    })
                    .build();

            dispatcher.getRoot().addChild(rootNode);

            LiteralCommandNode<FabricClientCommandSource> onNode = ClientCommandManager
                    .literal("on")
                    .executes(context -> {
                        if (ConfigManager.MOD_ENABLE) {
                            return 1;
                        }
                        ConfigManager.MOD_ENABLE = true;
                        context.getSource().sendFeedback(Component.literal("Picker enabled"));
                        return 1;
                    })
                    .build();

            LiteralCommandNode<FabricClientCommandSource> offNode = ClientCommandManager
                    .literal("off")
                    .executes(context -> {
                        if (!ConfigManager.MOD_ENABLE) {
                            return 1;
                        }
                        ConfigManager.MOD_ENABLE = false;
                        context.getSource().sendFeedback(Component.literal("Picker disabled"));
                        return 1;
                    })
                    .build();

            rootNode.addChild(offNode);
            rootNode.addChild(onNode);


            LiteralCommandNode<FabricClientCommandSource> itemAddNode = ClientCommandManager
                    .literal("add").then(ClientCommandManager.argument("item", ItemArgument.item(registryAccess))
                            .executes(context -> {
                                ItemInput input = context.getArgument("item", ItemInput.class);
                                ConfigManager.pickMode pickMode = ConfigManager.PICK_MODE;
                                context.getSource().sendFeedback(
                                        Component.literal("added item: " + input.getItem() + " for mode " + pickMode)
                                );
                                pickMode.getList().add(input.getItem());
                                //blockAdd(input);
                                return 1;
                            }))
                    .build();

            LiteralCommandNode<FabricClientCommandSource> itemRemoveNode = ClientCommandManager
                    .literal("del").then(ClientCommandManager.argument("item", PickItemArgument.item(registryAccess))
                            .executes(context -> {
                                ItemInput input = context.getArgument("item", ItemInput.class);
                                ConfigManager.pickMode pickMode = ConfigManager.PICK_MODE;
                                context.getSource().sendFeedback(
                                        Component.literal("deleted item: " + input.getItem() + " for mode " + pickMode)
                                );
                                pickMode.getList().remove(input.getItem());
                                return 1;
                            }))
                    .build();

            LiteralCommandNode<FabricClientCommandSource> modeNode = ClientCommandManager
                    .literal("mode")
                    .then(ClientCommandManager.argument("modes", StringArgumentType.word())
                            .suggests((context, builder) -> {
                                builder.suggest("blacklist",()->"mode");
                                builder.suggest("whitelist",()->"mode");
                                return builder.buildFuture();
                            })
                            .executes(context -> {
                                String modes = context.getArgument("modes",String.class);
                                if (modes.equalsIgnoreCase("whitelist")) {
                                    ConfigManager.PICK_MODE = ConfigManager.pickMode.WHITELIST;
                                    context.getSource().sendFeedback(Component.literal("Pick mode changed to " + ConfigManager.PICK_MODE));
                                } else if (modes.equalsIgnoreCase("blacklist")) {
                                    ConfigManager.PICK_MODE = ConfigManager.pickMode.BLACKLIST;
                                    context.getSource().sendFeedback(Component.literal("Pick mode changed to " + ConfigManager.PICK_MODE));
                                } else {
                                    context.getSource().sendFeedback(Component.literal("Wrong argument"));
                                }
                                return 1;
                            })
                    )
                    .executes(context -> {
                        context.getSource().sendFeedback(Component.literal("Current mode: " + ConfigManager.PICK_MODE));
                        return 1;
                    })
                    .build();
            LiteralCommandNode<FabricClientCommandSource> listNode = ClientCommandManager
                    .literal("list")
                    .executes(context -> {
                        context.getSource().sendFeedback(Component.literal("Current " + "Blacklisted" + " items : " + Arrays.toString(ConfigManager.BLACKLIST_ITEMS.toArray(new Item[0]))));
                        context.getSource().sendFeedback(Component.literal("Current " + "Whitelisted" + " items : " + Arrays.toString(ConfigManager.WHITELIST_ITEMS.toArray(new Item[0]))));
                        return 1;
                    })
                    .build();

            LiteralCommandNode<FabricClientCommandSource> helpNode = ClientCommandManager
                    .literal("help")
                    .executes(context -> {
                        context.getSource().sendFeedback(Component.literal("picker: toggle mod enable / disable"));
                        context.getSource().sendFeedback(Component.literal("subCommands: add, del, mode, list"));
                        context.getSource().sendFeedback(Component.literal("| mode: blacklist, whitelist"));
                        context.getSource().sendFeedback(Component.literal("| | blacklist: Throw all picked item in list"));
                        context.getSource().sendFeedback(Component.literal("| | whitelist: Throw all picked item Not in list"));
                        context.getSource().sendFeedback(Component.literal("| add <item>: Add item to current mode"));
                        context.getSource().sendFeedback(Component.literal("| del <item>: Delete item from current mode"));
                        context.getSource().sendFeedback(Component.literal("| list: List all blacklisted and whitelisted items"));
                        return 1;
                    })
                    .build();


            rootNode.addChild(itemAddNode);
            rootNode.addChild(itemRemoveNode);
            rootNode.addChild(modeNode);
            rootNode.addChild(listNode);
            rootNode.addChild(helpNode);

        }));

    }
}
