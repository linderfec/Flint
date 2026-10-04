package org.linderfec.mixin;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenTransformer {

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void onRender(GuiGraphicsExtractor graphics,
                          int mouseX,
                          int mouseY,
                          float partialTick,
                          CallbackInfo ci
    ) {
        int screenHeight = graphics.guiHeight();
        graphics.text(
                net.minecraft.client.Minecraft.getInstance().font,
                "你的自定义文字",
                2,                          // x 坐标
                screenHeight - 22,          // y 坐标
                0xFFFFFF,                   // 白色
                true                        // 带阴影
        );
    }
}