package me.comu.client.module.impl.toggle.render;

import me.comu.api.event.Listener;
import me.comu.api.minecraft.render.RenderMethods;
import me.comu.client.core.Gun;
import me.comu.client.events.RenderEvent;
import me.comu.client.events.RenderGameInfoEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.module.impl.toggle.combat.KillAura;
import me.comu.client.utils.ColorHelper;
import me.comu.client.utils.Vector4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

public final class InfoTags extends ToggleableModule {



    @Override
    protected void onDisable() {
        super.onDisable();
    }

    public InfoTags() {
        super("InfoTags", new String[]{"infotags", "infotag", "info", "itag", "info-tag", "info-tags"}, ModuleType.RENDER);
        this.listeners.add(new Listener<RenderGameInfoEvent>("middle_click_input_listener") {
            @Override
            public void call(RenderGameInfoEvent event) {
                ScaledResolution scaledResolution = event.getScaledResolution();
                KillAura ka = (KillAura) Gun.getInstance().getModuleManager().getModuleByAlias("KillAura");
//                final ResourceLocation res = new ResourceLocation("https://cravatar.eu/avatar/" + ka.target.getName()+ "/64.png");
//                String skin = "https://cravatar.eu/avatar/" + ka.target.getName()+ "/64.png";
                if (ka.isRunning() && ka.lastTarget != null) {
                    EntityPlayer e = ka.lastTarget;
                    int h = (int) e.getHealth() * 5;
                    int d = (int) e.getDistanceToEntity(minecraft.thePlayer);
                    String health = Integer.toString(h) + '%';
                    String distance = Integer.toString(d);
                    String name = e.getName();
                    int color;
                    if (e.getHealth() > 18) {
                        color = 0x55FF55; // light green
                    } else if (e.getHealth() > 16) {
                        color = 0x00AA00; // dark green
                    } else if (e.getHealth() > 12) {
                        color = 0xFFFF55; // yellow
                    } else if (e.getHealth() > 8) {
                        color = 0xFFAA00; //gold
                    } else if (e.getHealth() > 5) {
                        color = 0xFF5555; //red
                    } else {
                        color = 0xAA0000; //dark red
                    }
                    minecraft.fontRenderer.drawCenteredString(distance, scaledResolution.getScaledWidth() + 100F, scaledResolution.getScaledHeight() / 2.3F, 0xffffff, true);
                    minecraft.fontRenderer.drawCenteredString(health, scaledResolution.getScaledWidth() + 100F, scaledResolution.getScaledHeight() / 2.4F, color, true);
                    minecraft.fontRenderer.drawCenteredString(name, scaledResolution.getScaledWidth() + 100F, scaledResolution.getScaledHeight() / 2.51F, 0xffffff, true);
                   // Gui.drawRect(scaledResolution.getScaledHeight() + 81, scaledResolution.getScaledWidth() / 23, scaledResolution.getScaledWidth() / 2, scaledResolution.getScaledHeight() / 60, 0x40787878);
                    RenderMethods.drawGradientBorderedRect(scaledResolution.getScaledWidth() / 2f + 110, scaledResolution.getScaledHeight() - 380, scaledResolution.getScaledWidth() / 2f, scaledResolution.getScaledHeight() / 12f + 215, 1F, 0xDD000000, 0x60787878, 0x60787878);
                    final Vector4f transformed = new Vector4f(scaledResolution.getScaledWidth() * 2.0f, scaledResolution.getScaledHeight() * 40.0f, -50.0f, -60.0f);
                    transformed.setX(Math.min(transformed.getX(), scaledResolution.getScaledWidth() / 2f));
                    transformed.setY(Math.min(transformed.getY(), 272));
                    transformed.setW(Math.max(transformed.getW(), 40));
                    transformed.setH(Math.max(transformed.getH(), 309));
                    drawHealthBar(transformed, e);
                 //   drawFace(ka.target.getName(), scaledResolution.getScaledWidth() / 2 + 115, scaledResolution.getScaledHeight() / 2 - 246, 38, 38, false);
                    //drawAltBody(ka.target.getName(), scaledResolution.getScaledWidth() / 2 + 115, scaledResolution.getScaledHeight() / 2 - 246, 500, 500);
                }
                //todo: move down closer to crosshair

            }


        });


        this.listeners.add(new Listener<RenderEvent>("middle_click_input_listener") {
            @Override
            public void call(RenderEvent e) {
                GlStateManager.pushMatrix();
                GlStateManager.popMatrix();


            }
        });
    }


    private void drawHealthBar(final Vector4f vector, final EntityPlayer entity) {
        final int dmg = (int) Math.round(255.0 - entity.getHealth() * 255.0 / entity.getMaxHealth());
        final int dmgint = 255 - dmg << 8 | dmg << 16;
        final float hpHeight = entity.getHealth() * (vector.h - vector.y) / entity.getMaxHealth();
        RenderMethods.drawRectESP(vector.x - 3.0f, vector.y - 0.5f, vector.x - 1.0f, vector.h + 0.5f, -1726934767);
        RenderMethods.drawRectESP(vector.x - 2.5f, vector.h - hpHeight, vector.x - 1.5f, vector.h, ColorHelper.changeAlpha(dmgint, 255));
    }

    public static void drawFace(final String name, final int x, final int y, final int w, final int h, final boolean selected) {
        try {
            AbstractClientPlayer.getDownloadImageSkin(AbstractClientPlayer.getLocationSkin(name), name).loadTexture(Minecraft.getMinecraft().getResourceManager());
            Minecraft.getMinecraft().getTextureManager().bindTexture(AbstractClientPlayer.getLocationSkin(name));
            GL11.glEnable(3042);
            if (selected) {
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            } else {
                GL11.glColor4f(0.9f, 0.9f, 0.9f, 1.0f);
            }
            float fw = 192.0f;
            float fh = 192.0f;
            float u = 24.0f;
            float v = 24.0f;
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            fw = 192.0f;
            fh = 192.0f;
            u = 120.0f;
            v = 24.0f;
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            GL11.glDisable(3042);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void drawAltBody(final String name, int x, int y, final int width, final int height) {
        try {
            AbstractClientPlayer.getDownloadImageSkin(AbstractClientPlayer.getLocationSkin(name), name).loadTexture(Minecraft.getMinecraft().getResourceManager());
            Minecraft.getMinecraft().getTextureManager().bindTexture(AbstractClientPlayer.getLocationSkin(name));
            final boolean slim = DefaultPlayerSkin.func_177332_b(EntityPlayer.func_175147_b(name)).equals("slim");
            GL11.glEnable(3042);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            x += width / 4;
            y += 0;
            int w = width / 2;
            int h = height / 4;
            final float fw = height * 2;
            final float fh = height * 2;
            float u = height / 4;
            float v = height / 4;
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            x += 0;
            y += 0;
            w = width / 2;
            h = height / 4;
            u = height / 4 * 5;
            v = height / 4;
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            x += 0;
            y += height / 4;
            w = width / 2;
            h = height / 8 * 3;
            u = height / 4 * 2.5f;
            v = height / 4 * 2.5f;
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            x += 0;
            y += 0;
            w = width / 2;
            h = height / 8 * 3;
            u = height / 4 * 2.5f;
            v = height / 4 * 4.5f;
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            x -= width / 16 * (slim ? 3 : 4);
            y += (slim ? (height / 32) : 0);
            w = width / 16 * (slim ? 3 : 4);
            h = height / 8 * 3;
            u = height / 4 * 5.5f;
            v = height / 4 * 2.5f;
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            x += 0;
            y += 0;
            w = width / 16 * (slim ? 3 : 4);
            h = height / 8 * 3;
            u = height / 4 * 5.5f;
            v = height / 4 * 4.5f;
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            x += width / 16 * (slim ? 11 : 12);
            y += 0;
            w = width / 16 * (slim ? 3 : 4);
            h = height / 8 * 3;
            u = height / 4 * 5.5f;
            v = height / 4 * 2.5f;
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            x += 0;
            y += 0;
            w = width / 16 * (slim ? 3 : 4);
            h = height / 8 * 3;
            u = height / 4 * 5.5f;
            v = height / 4 * 4.5f;
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            x -= width / 2;
            y += height / 32 * (slim ? 11 : 12);
            w = width / 4;
            h = height / 8 * 3;
            u = height / 4 * 0.5f;
            v = height / 4 * 2.5f;
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            x += 0;
            y += 0;
            w = width / 4;
            h = height / 8 * 3;
            u = height / 4 * 0.5f;
            v = height / 4 * 4.5f;
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            x += width / 4;
            y += 0;
            w = width / 4;
            h = height / 8 * 3;
            u = height / 4 * 0.5f;
            v = height / 4 * 2.5f;
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            x += 0;
            y += 0;
            w = width / 4;
            h = height / 8 * 3;
            u = height / 4 * 0.5f;
            v = height / 4 * 4.5f;
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            GL11.glDisable(3042);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void drawAltBack(final String name, int x, int y, final int width, final int height) {
        try {
            AbstractClientPlayer.getDownloadImageSkin(AbstractClientPlayer.getLocationSkin(name), name).loadTexture(Minecraft.getMinecraft().getResourceManager());
            Minecraft.getMinecraft().getTextureManager().bindTexture(AbstractClientPlayer.getLocationSkin(name));
            final boolean slim = DefaultPlayerSkin.func_177334_a(EntityPlayer.func_175147_b(name)).equals("slim");
            GL11.glEnable(3042);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            x += width / 4;
            y += 0;
            int w = width / 2;
            int h = height / 4;
            final float fw = height * 2;
            final float fh = height * 2;
            float u = height / 4 * 3;
            float v = height / 4;
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            x += 0;
            y += 0;
            w = width / 2;
            h = height / 4;
            u = height / 4 * 7;
            v = height / 4;
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            x += 0;
            y += height / 4;
            w = width / 2;
            h = height / 8 * 3;
            u = height / 4 * 4;
            v = height / 4 * 2.5f;
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            x += 0;
            y += 0;
            w = width / 2;
            h = height / 8 * 3;
            u = height / 4 * 4;
            v = height / 4 * 4.5f;
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            x -= width / 16 * (slim ? 3 : 4);
            y += (slim ? (height / 32) : 0);
            w = width / 16 * (slim ? 3 : 4);
            h = height / 8 * 3;
            u = height / 4 * (slim ? 6.375f : 6.5f);
            v = height / 4 * 2.5f;
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            x += 0;
            y += 0;
            w = width / 16 * (slim ? 3 : 4);
            h = height / 8 * 3;
            u = height / 4 * (slim ? 6.375f : 6.5f);
            v = height / 4 * 4.5f;
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            x += width / 16 * (slim ? 11 : 12);
            y += 0;
            w = width / 16 * (slim ? 3 : 4);
            h = height / 8 * 3;
            u = height / 4 * (slim ? 6.375f : 6.5f);
            v = height / 4 * 2.5f;
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            x += 0;
            y += 0;
            w = width / 16 * (slim ? 3 : 4);
            h = height / 8 * 3;
            u = height / 4 * (slim ? 6.375f : 6.5f);
            v = height / 4 * 4.5f;
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            x -= width / 2;
            y += height / 32 * (slim ? 11 : 12);
            w = width / 4;
            h = height / 8 * 3;
            u = height / 4 * 1.5f;
            v = height / 4 * 2.5f;
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            x += 0;
            y += 0;
            w = width / 4;
            h = height / 8 * 3;
            u = height / 4 * 1.5f;
            v = height / 4 * 4.5f;
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            x += width / 4;
            y += 0;
            w = width / 4;
            h = height / 8 * 3;
            u = height / 4 * 1.5f;
            v = height / 4 * 2.5f;
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            x += 0;
            y += 0;
            w = width / 4;
            h = height / 8 * 3;
            u = height / 4 * 1.5f;
            v = height / 4 * 4.5f;
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            GL11.glDisable(3042);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

//     RenderMethods.drawGradientBorderedRect(scaledResolution.getScaledWidth() / 2 - 91, scaledResolution.getScaledHeight() - 39, scaledResolution.getScaledWidth() / 2 - 10, scaledResolution.getScaledHeight() - 30, 1F, 0xDD000000, 0xFF444444, 0xFF222222);
//                              textFont.drawCenteredString((stack.getMaxDamage() - stack.getItemDamage()) + "",  scaledResolution.getScaledWidth() /2.0115F + x * 1/.8F, scaledResolution.getScaledHeight() - 60, 0xf4a442);
/*
GlStateManager.pushMatrix();
RenderMethods.enableGL3D();
GlStateManager.enableLighting();
GL11.glBegin(GL11.GL_LINE_LOOP);
for(int i =0; i <= 600; i++){
	double angle = 2 * Math.PI * i / 300;
	double x = Math.cos(angle);
	double y = Math.sin(angle);
	GL11.glVertex2d(x,y);
	}

GL11.glEnd();
GlStateManager.popMatrix();
RenderMethods.disableGL3D();
GlStateManager.disableLighting();
*/
	