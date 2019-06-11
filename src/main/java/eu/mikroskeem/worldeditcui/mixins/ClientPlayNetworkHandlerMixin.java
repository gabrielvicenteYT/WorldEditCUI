package eu.mikroskeem.worldeditcui.mixins;

import eu.mikroskeem.worldeditcui.FabricModWorldEditCUI;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.packet.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.charset.StandardCharsets;

/**
 * @author Mark Vainomaa
 */
@Mixin(value = ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
    @Inject(method = "onCustomPayload", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/ThreadExecutor;)V",
            shift = At.Shift.AFTER
    ), cancellable = true)
    private void onPacket(CustomPayloadS2CPacket packet, CallbackInfo ci) {
        Identifier channel = packet.getChannel();
        if (channel.equals(FabricModWorldEditCUI.CHANNEL_WECUI)) {
            PacketByteBuf data = packet.getData();

            try {
                int readableBytes = data.readableBytes();
                if (readableBytes > 0) {
                    byte[] payload = new byte[readableBytes];
                    data.readBytes(payload);
                    FabricModWorldEditCUI.channelListener.onMessage(new String(payload, StandardCharsets.UTF_8));
                } else {
                    FabricModWorldEditCUI.controller.getDebugger().debug("Warning, invalid (zero length) payload received from server");
                }
            } catch (Exception ignored) {}

            ci.cancel();
        }
    }
}
