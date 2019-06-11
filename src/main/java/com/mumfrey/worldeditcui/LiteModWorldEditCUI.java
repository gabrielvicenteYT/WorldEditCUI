package com.mumfrey.worldeditcui;

import io.netty.buffer.Unpooled;

import java.io.File;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketJoinGame;

import org.lwjgl.input.Keyboard;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mumfrey.liteloader.Configurable;
import com.mumfrey.liteloader.InitCompleteListener;
import com.mumfrey.liteloader.JoinGameListener;
import com.mumfrey.liteloader.PluginChannelListener;
import com.mumfrey.liteloader.PostRenderListener;
import com.mumfrey.liteloader.Tickable;
import com.mumfrey.liteloader.core.ClientPluginChannels;
import com.mumfrey.liteloader.core.LiteLoader;
import com.mumfrey.liteloader.core.PluginChannels.ChannelPolicy;
import com.mumfrey.liteloader.messaging.Message;
import com.mumfrey.liteloader.messaging.Messenger;
import com.mumfrey.liteloader.modconfig.ConfigPanel;
import com.mumfrey.liteloader.util.Input;
import com.mumfrey.worldeditcui.config.CUIConfiguration;
import com.mumfrey.worldeditcui.event.listeners.CUIListenerChannel;
import com.mumfrey.worldeditcui.event.listeners.CUIListenerWorldRender;
import com.mumfrey.worldeditcui.gui.CUIConfigPanel;

/**
 * Main litemod entry point
 * 
 * @author Adam Mummery-Smith
 */
public class LiteModWorldEditCUI implements Tickable, InitCompleteListener, PluginChannelListener, PostRenderListener, Configurable, JoinGameListener, Messenger
{
	@Override
	public void onJoinGame(INetHandler netHandler, SPacketJoinGame loginPacket, ServerData serverData, RealmsServer realmsServer)
	{
		this.visible = true;
		this.controller.getDebugger().debug("Joined game, sending initial handshake");
		this.helo();
	}
	
	@Override
	public void receiveMessage(Message message)
	{
		if (message.isChannel("wecui:wecui"))
		{
			try
			{
				Object value = message.<Object>getValue();
				if (value instanceof String)
				{
					this.channelListener.onMessage((String)value);
				}
				else if (value instanceof List)
				{
					@SuppressWarnings("unchecked")
					List<Object> list = (List<Object>)value;
					this.channelListener.onMessage(Joiner.on('|').join(list));
				}
			}
			catch (Exception ex) {}
		}
	}
	
	@Override
	public void onPostRenderEntities(float partialTicks)
	{
		if (this.visible && !this.alwaysOnTop)
		{
			RenderHelper.disableStandardItemLighting();
			this.worldRenderListener.onRender(partialTicks);
			RenderHelper.enableStandardItemLighting();
		}
	}
	
	@Override
	public void onPostRender(float partialTicks)
	{
		if (this.visible && this.alwaysOnTop)
		{
			this.worldRenderListener.onRender(partialTicks);
		}
	}
}
