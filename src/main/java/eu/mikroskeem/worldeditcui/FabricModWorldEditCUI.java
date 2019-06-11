package eu.mikroskeem.worldeditcui;

import com.google.common.base.Charsets;
import com.mumfrey.worldeditcui.WorldEditCUI;
import com.mumfrey.worldeditcui.config.CUIConfiguration;
import com.mumfrey.worldeditcui.event.listeners.CUIListenerChannel;
import com.mumfrey.worldeditcui.event.listeners.CUIListenerWorldRender;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.InputUtil;
import net.minecraft.server.network.packet.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Fabric mod entrypoint
 *
 * @author Mark Vainomaa
 */
public final class FabricModWorldEditCUI implements ModInitializer {
    private static final int DELAYED_HELO_TICKS = 10;
    public static final Identifier CHANNEL_WECUI = new Identifier("worldedit", "cui");

    private final Identifier keybindToggleUIId = new Identifier("wecui", "keys.toggle");
    private final Identifier keybindClearSelId = new Identifier("wecui", "keys.clear");
    private final Identifier keybindChunkBorderId = new Identifier("wecui", "keys.chunk");
    private final FabricKeyBinding keyBindToggleUI = FabricKeyBinding.Builder.create(keybindToggleUIId, InputUtil.Type.SCANCODE, InputUtil.UNKNOWN_KEYCODE.getKeyCode(), "wecui.keys.category").build();
    private final FabricKeyBinding keyBindClearSel = FabricKeyBinding.Builder.create(keybindClearSelId, InputUtil.Type.SCANCODE, InputUtil.UNKNOWN_KEYCODE.getKeyCode(), "wecui.keys.category").build();
    private final FabricKeyBinding keyBindChunkBorder = FabricKeyBinding.Builder.create(keybindChunkBorderId, InputUtil.Type.SCANCODE, InputUtil.UNKNOWN_KEYCODE.getKeyCode(), "wecui.keys.category").build();

    public static WorldEditCUI controller;
    public static CUIListenerWorldRender worldRenderListener;
    public static CUIListenerChannel channelListener;

    private World lastWorld;
    private ClientPlayerEntity lastPlayer;

    private boolean visible = true;
    private boolean alwaysOnTop = false;
    private int delayedHelo = 0;

    @Override
    public void onInitialize() {
        ClientTickCallback.EVENT.register(mc -> {
            CUIConfiguration config = controller.getConfiguration();
            boolean inGame = true; // TODO!
            boolean clock = true; // TODO!

            if (inGame && mc.currentScreen == null) {
                if (this.keyBindToggleUI.isPressed()) {
                    if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
                        config.setAlwaysOnTop(!config.isAlwaysOnTop());
                    } else {
                        this.visible = !this.visible;
                    }
                }

                if (this.keyBindClearSel.isPressed()) {
                    if (mc.player != null) {
                        mc.player.sendChatMessage("//sel");
                    }

                    if (config.isClearAllOnKey()) {
                        controller.clearRegions();
                    }
                }

                if (this.keyBindChunkBorder.isPressed()) {
                    controller.toggleChunkBorders();
                }
            }

            if (inGame && clock && controller != null) {
                this.alwaysOnTop = config.isAlwaysOnTop();

                if (mc.world != this.lastWorld || mc.player != this.lastPlayer) {
                    this.lastWorld = mc.world;
                    this.lastPlayer = mc.player;

                    controller.getDebugger().debug("World change detected, sending new handshake");
                    controller.clear();
                    this.helo();
                    this.delayedHelo = FabricModWorldEditCUI.DELAYED_HELO_TICKS;
                    if (mc.player != null && config.isPromiscuous()) {
                        mc.player.sendChatMessage("/we cui"); //Tricks WE to send the current selection
                    }
                }

                if (this.delayedHelo > 0) {
                    this.delayedHelo--;
                    if (this.delayedHelo == 0) {
                        this.helo();
                        if (LiteLoader.getClientPluginChannels().isRemoteChannelRegistered(CHANNEL_WECUI) && mc.player != null) {
                            mc.player.sendChatMessage("/we cui");
                        }
                    }
                }
            }
        });
    }

    private void helo() {
        PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
        String message = "v|" + WorldEditCUI.PROTOCOL_VERSION;
        buffer.writeBytes(message.getBytes(Charsets.UTF_8));

        CustomPayloadC2SPacket packet = new CustomPayloadC2SPacket(CHANNEL_WECUI, buffer);
        MinecraftClient.getInstance().getNetworkHandler().sendPacket(packet);
    }

    public WorldEditCUI getController()
    {
        return FabricModWorldEditCUI.controller;
    }
}
