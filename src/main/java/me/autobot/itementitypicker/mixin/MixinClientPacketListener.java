package me.autobot.itementitypicker.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import me.autobot.itementitypicker.config.ConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ClientPacketListener.class)
public abstract class MixinClientPacketListener {
    @Unique
    private ItemStack itemStack;

    @Inject(method = "handleTakeItemEntity",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V", shift = At.Shift.BEFORE),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void handleTakeItemEntity(ClientboundTakeItemEntityPacket packet, CallbackInfo ci, Entity entity, LivingEntity livingEntity, ItemEntity itemEntity, ItemStack itemStack) {
        if (!ConfigManager.MOD_ENABLE) {
            return;
        }
        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;
        if (player == null) {
            return;
        }
        this.itemStack = itemStack.copy();
    }

    @Inject(method = "handleContainerSetSlot",
            at = @At(value = "TAIL")
    )
    private void HandleContainerSetSlot(ClientboundContainerSetSlotPacket packet, CallbackInfo ci) {
        if (!ConfigManager.MOD_ENABLE) {
            return;
        }
        ItemStack item = packet.getItem();
        if (item.isEmpty()) {
            return;
        }
        if (!this.itemStack.getItem().equals(item.getItem())) {
            return;
        }
        ConfigManager.pickMode pickMode = ConfigManager.PICK_MODE;
        boolean contains = pickMode.getList().contains(this.itemStack.getItem());
        if (pickMode == ConfigManager.pickMode.WHITELIST) {
            // Throw when Not in List
            if (contains) {
                return;
            }
        } else if (pickMode == ConfigManager.pickMode.BLACKLIST) {
            // Throw when in List
            if (!contains) {
                return;
            }
        }
        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;
        if (player == null) {
            return;
        }
        player.connection.send(new ServerboundContainerClickPacket(0, 0, packet.getSlot(), 1, ClickType.THROW, ItemStack.EMPTY, Int2ObjectMaps.emptyMap()));

    }
}
