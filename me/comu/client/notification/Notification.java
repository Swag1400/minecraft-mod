package me.comu.client.notification;


import me.comu.api.minecraft.render.CustomFont;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;

import java.awt.*;

public class Notification {

    private NotificationType type;
    private String title;
    private String message;


    private long start;
    private long fadeIn;
    private long fadeOut;
    private long end;

    public Notification(NotificationType type, String title, String message, int duration) {
        this.type = type;
        this.title = title;
        this.message = message;

        fadeIn = 200 * duration;
        fadeOut = fadeIn + 500 * duration;
        end = fadeOut + fadeIn;
    }

    public void Show() {
        start = System.currentTimeMillis();
    }

    public boolean isShown() {
        return getTime() <= end;
    }

    private long getTime() {
        return System.currentTimeMillis() - start;
    }

    public void render() {
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        double offset;
        int width = fontRenderer.getStringWidth(title.length() > message.length() ? title : message) + 10;
        int height = 100;
        long time = getTime();

        if (time < fadeIn) {
            offset = Math.tanh(((float) time / fadeOut) * 3.0) * width;
        } else if (time > fadeOut) {
            offset = (Math.tanh(3.0 - (time - fadeOut) / (double) (end - fadeOut) * width));
        } else {
            offset = width;
        }

        Color colour = new Color(0x90101010);
        Color colour1 = new Color(0, 0, 0, 220);
        if (type == NotificationType.INFO) {
            colour1 = new Color(0xFF909090);
        } else if (type == NotificationType.WARNING) {
            colour1 = new Color(0xFFFFFF10);
            int i = Math.max(0, Math.min(255, (int) (Math.sin(time / 100.0) * 255.0 / 2 + 10.5)));
            colour = new Color(i, i, 0, 220);
        } else if (type == NotificationType.ERROR) {
            colour1 = new Color(0xFFFF1010);
            int i = Math.max(0, Math.min(255, (int) (Math.sin(time / 100.0) * 255.0 / 2 + 127.5)));
            colour = new Color(i, 0, 0, 220);
        }

        final CustomFont guiFont = new CustomFont("Verdana", 18);
        drawRect(GuiScreen.width - offset, GuiScreen.height - 40 - height, GuiScreen.width, GuiScreen.height - 100, colour.getRGB());
        drawRect(GuiScreen.width - offset, GuiScreen.height - 40 - height, GuiScreen.width - offset + 4, GuiScreen.height - 100, colour1.getRGB());

        guiFont.drawCenteredString(title, (int) (GuiScreen.width - offset + 34), GuiScreen.height - 40 - height, -1);
        guiFont.drawCenteredString(message, (int) (GuiScreen.width - offset + 20), GuiScreen.height - 28 - height, -1);
    }

    public static void drawRect(double left, double top, double right, double bottom, int color) {
        if (left < right) {
            double i = left;
            left = right;
            right = i;
        }

        if (top < bottom) {
            double j = top;
            top = bottom;
            bottom = j;
        }

        float var11 = (float) (color >> 24 & 255) / 255.0F;
        float var6 = (float) (color >> 16 & 255) / 255.0F;
        float var7 = (float) (color >> 8 & 255) / 255.0F;
        float var8 = (float) (color & 255) / 255.0F;
        Tessellator var9 = Tessellator.getInstance();
        WorldRenderer var10 = var9.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableLightmap();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(var6, var7, var8, var11);
        var10.startDrawingQuads();
        var10.addVertex(left, bottom, 0.0D);
        var10.addVertex(right, bottom, 0.0D);
        var10.addVertex(right, top, 0.0D);
        var10.addVertex(left, top, 0.0D);
        var9.draw();
        GlStateManager.enableLightmap();
        GlStateManager.disableBlend();
    }


}
