package me.autobot.itementitypicker.mixin;

import me.autobot.itementitypicker.config.ConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Final
    @Shadow
    public Options options;

    @Inject(at = @At("HEAD"), method = "close")
    private void close(CallbackInfo info) {
        options.save();
        ConfigManager.saveConfig();
    }
}
