package net.Flint.mixin;

import net.Flint.api.FlintAPI;
import net.Flint.logs.logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// 这是一个示例Mixin，展示了如何使用Mixin功能
// 使用@Pseudo注解来避免在编译时查找目标类
@Pseudo
@Mixin(targets = "net.minecraft.client.main.Main") // 示例：对Minecraft主类进行mixin
public class ExampleMixin {
    
    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        logger.info("Flint Mixin System successfully injected!");
        logger.info("Available mods: " + FlintAPI.getModCount());
    }
}