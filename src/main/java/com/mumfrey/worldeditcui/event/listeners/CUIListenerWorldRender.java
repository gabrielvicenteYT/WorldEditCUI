package com.mumfrey.worldeditcui.event.listeners;

import com.mumfrey.worldeditcui.WorldEditCUI;
import com.mumfrey.worldeditcui.util.Vector3;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OpenGlHelper;

import static org.lwjgl.opengl.GL11.glAlphaFunc;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11C.GL_GREATER;
import static org.lwjgl.opengl.GL11C.GL_LEQUAL;
import static org.lwjgl.opengl.GL11C.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11C.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11C.glBlendFunc;
import static org.lwjgl.opengl.GL11C.glDepthFunc;
import static org.lwjgl.opengl.GL11C.glDepthMask;

/**
 * Listener for WorldRenderEvent
 * 
 * @author lahwran
 * @author yetanotherx
 * @author Adam Mummery-Smith
 */
public class CUIListenerWorldRender
{
	private WorldEditCUI controller;
	
	private MinecraftClient minecraft;
	
	public CUIListenerWorldRender(WorldEditCUI controller, MinecraftClient minecraft)
	{
		this.controller = controller;
		this.minecraft = minecraft;
	}
	
	public void onRender(float partialTicks)
	{
		try
		{
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
			
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			glEnableBlend();
			glEnableAlphaTest();
			glAlphaFunc(GL_GREATER, 0.0F);
			glDisableTexture2D();
			glEnableDepthTest();
			glDepthMask(false);
			glPushMatrix();
			glDisableFog();
			
			try
			{
				Vector3 cameraPos = new Vector3(this.minecraft.getCameraEntity(), partialTicks);
				glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
				this.controller.renderSelections(cameraPos, partialTicks);
			}
			catch (Exception e)
			{
			}
			
			glDepthFunc(GL_LEQUAL);
			glPopMatrix();
			
			glDepthMask(true);
			glEnableTexture2D();
			glDisableBlend();
			glAlphaFunc(GL_GREATER, 0.1F);
		}
		catch (Exception ex) {}
	}
}
