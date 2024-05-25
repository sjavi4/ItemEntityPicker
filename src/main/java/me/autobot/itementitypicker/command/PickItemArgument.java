package me.autobot.itementitypicker.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.autobot.itementitypicker.config.ConfigManager;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.commands.arguments.item.ItemParser;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;


public class PickItemArgument implements ArgumentType<ItemInput> {
    private final HolderLookup<Item> items;
    public PickItemArgument(CommandBuildContext commandBuildContext) {
        this.items = commandBuildContext.holderLookup(Registries.ITEM).filterElements(ConfigManager.PICK_MODE.getList()::contains);
    }
    public static PickItemArgument item(CommandBuildContext commandBuildContext) {
        return new PickItemArgument(commandBuildContext);
    }

    public ItemInput parse(StringReader stringReader) throws CommandSyntaxException {
        ItemParser.ItemResult itemResult = ItemParser.parseForItem(this.items, stringReader);
        return new ItemInput(itemResult.item(), itemResult.nbt());
    }

    public static <S> ItemInput getItem(CommandContext<S> commandContext, String string) {
        return (ItemInput)commandContext.getArgument(string, ItemInput.class);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
        return ItemParser.fillSuggestions(this.items, suggestionsBuilder, false);
    }
}
