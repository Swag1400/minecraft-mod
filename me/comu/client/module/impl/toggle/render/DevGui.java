package me.comu.client.module.impl.toggle.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import me.comu.api.minecraft.render.CustomFont;
import me.comu.client.module.impl.toggle.render.clickgui.ClickGui;

public final class DevGui extends GuiScreen
{
private static ClickGui clickGui;
public final CustomFont guiFont = new CustomFont("Verdana", 18);

	public DevGui() {
	
} 
	public void onEnable() {
		drawDefaultBackground();
	}
	public boolean doesGuiPauseGame() {
		return false;
	}
	public void drawScreen(int i, int j, float f) {
		drawDefaultBackground();
		drawCenteredString(Minecraft.getMinecraft().fontRenderer, "Test", width/2, 45, 0x44ff11);
		super.drawScreen(i, j, f);
		i = height/2;
		j = width/2;
		f = Minecraft.getSystemTime();
		//drawHoveringText(); 
	}
    public static ClickGui getClickGui()
    {
        return clickGui == null ? clickGui = new ClickGui() : clickGui;
    }
}
  
// TODO: Attempt to make Skeet GUI or any GUI at all, HDA Viirus on YouTube for reference on GUIs