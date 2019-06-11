package eu.mikroskeem.worldeditcui.mixins;

import com.mumfrey.worldeditcui.WorldEditCUI;
import com.mumfrey.worldeditcui.event.listeners.CUIListenerChannel;
import com.mumfrey.worldeditcui.event.listeners.CUIListenerWorldRender;
import eu.mikroskeem.worldeditcui.FabricModWorldEditCUI;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author Mark Vainomaa
 */
@Mixin(value = MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Inject(method = "init", at = @At(value = "TAIL"))
    private void onInitDone(CallbackInfo ci) {
        MinecraftClient minecraft = (MinecraftClient) (Object) this;

        FabricModWorldEditCUI.controller = new WorldEditCUI();
        FabricModWorldEditCUI.controller.initialise(minecraft);
        FabricModWorldEditCUI.worldRenderListener = new CUIListenerWorldRender(FabricModWorldEditCUI.controller, minecraft);
        FabricModWorldEditCUI.channelListener = new CUIListenerChannel(FabricModWorldEditCUI.controller);
    }
}
