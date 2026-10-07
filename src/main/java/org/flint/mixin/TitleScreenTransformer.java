package org.flint.mixin;

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
        String number_mod_pub = "";
        String pubnum = "";
        String modnum = "";

        int screenHeight = graphics.guiHeight();
        graphics.text(
                net.minecraft.client.Minecraft.getInstance().font,
                "Flint (" + "pub:" + pubnum +", mod:" + modnum + ")",
                80,
                screenHeight - 10,
                0xFFFFFFFF,
                true
        );
    }
}