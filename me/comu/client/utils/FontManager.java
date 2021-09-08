package me.comu.client.utils;


import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class FontManager
{
    private ResourceLocation darrow;
    private FontRenderUtils defaultFont;
    private FontManager instance;
    private HashMap<String, FontRenderUtils> fonts;

    public FontManager getInstance() {
        return this.instance;
    }

    public  FontRenderUtils getFont(final String key) {
        return fonts.getOrDefault(key, defaultFont);
    }

    public FontManager() {
        this.darrow = new ResourceLocation("SF-UI-Display-Regular.otf");
        this.fonts = new HashMap<String, FontRenderUtils>();
        this.instance = this;
        final ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(8);
        final ConcurrentLinkedQueue<TextureData> textureQueue = new ConcurrentLinkedQueue<TextureData>();
        this.defaultFont = new FontRenderUtils(executorService, textureQueue, new Font("Verdana", 0, 18));
        try {
            for (final int i : new int[] { 6, 7, 8, 10, 11, 12, 14 }) {
                final InputStream istream = this.getClass().getResourceAsStream("/assets/minecraft/SF-UI-Display-Regular.otf");
                Font myFont = Font.createFont(0, istream);
                myFont = myFont.deriveFont(0, (float)i);
                this.fonts.put("SFR " + i, new FontRenderUtils(executorService, textureQueue, myFont));
            }
            for (final int i : new int[] { 6, 7, 8, 9, 11 }) {
                final InputStream istream = this.getClass().getResourceAsStream("/assets/minecraft/SF-UI-Display-Bold.otf");
                Font myFont = Font.createFont(0, istream);
                myFont = myFont.deriveFont(0, (float)i);
                this.fonts.put("SFB " + i, new FontRenderUtils(executorService, textureQueue, myFont));
            }
            for (final int i : new int[] { 6, 7, 8, 9, 11, 12 }) {
                final InputStream istream = this.getClass().getResourceAsStream("/assets/minecraft/SF-UI-Display-Medium.otf");
                Font myFont = Font.createFont(0, istream);
                myFont = myFont.deriveFont(0, (float)i);
                this.fonts.put("SFM " + i, new FontRenderUtils(executorService, textureQueue, myFont));
            }
            for (final int i : new int[] { 17, 10, 9, 8, 7, 6 }) {
                final InputStream istream = this.getClass().getResourceAsStream("/assets/minecraft/SF-UI-Display-Light.otf");
                Font myFont = Font.createFont(0, istream);
                myFont = myFont.deriveFont(0, (float)i);
                this.fonts.put("SFL " + i, new FontRenderUtils(executorService, textureQueue, myFont));
            }
            for (final int i : new int[] { 19 }) {
                final InputStream istream = this.getClass().getResourceAsStream("/assets/minecraft/Jigsaw-Regular.otf");
                Font myFont = Font.createFont(0, istream);
                myFont = myFont.deriveFont(0, (float)i);
                this.fonts.put("JIGR " + i, new FontRenderUtils(executorService, textureQueue, myFont));
            }
            this.fonts.put("Verdana 12", new FontRenderUtils(executorService, textureQueue, new Font("Verdana", 0, 12)));
            this.fonts.put("Verdana Bold 16", new FontRenderUtils(executorService, textureQueue, new Font("Verdana Bold", 0, 16)));
            this.fonts.put("Verdana Bold 20", new FontRenderUtils(executorService, textureQueue, new Font("Verdana Bold", 0, 20)));
        }
        catch (Exception ex) {}
        executorService.shutdown();
        while (!executorService.isTerminated()) {
            try {
                Thread.sleep(10L);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (!textureQueue.isEmpty()) {
                final TextureData textureData = textureQueue.poll();
                GlStateManager.func_179144_i(textureData.getTextureId());
                GL11.glTexParameteri(3553, 10241, 9728);
                GL11.glTexParameteri(3553, 10240, 9728);
                GL11.glTexImage2D(3553, 0, 6408, textureData.getWidth(), textureData.getHeight(), 0, 6408, 5121, textureData.getBuffer());
            }
        }
    }
}
