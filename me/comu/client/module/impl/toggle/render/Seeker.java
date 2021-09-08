package me.comu.client.module.impl.toggle.render;

import me.comu.api.event.Listener;
import me.comu.api.minecraft.render.RenderMethods;
import me.comu.client.events.RenderEvent;
import me.comu.client.events.TntEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.Property;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

import java.util.List;

public final class Seeker extends ToggleableModule {

    public static Property<Boolean> hideNSeek = new Property<>(false, "HideNSeek", "hideandseek", "hideseek", "seek");
    public static Property<Boolean> tnt = new Property<>(true, "TnT", "explosions", "explosion");
    public static Property<Boolean> sand = new Property<>(true, "Sand", "s", "gravel", "g");
    private double x1, y2, z3;

    public Seeker() {
        super("Seeker", new String[]{"seeker", "seek", "hide"}, 0xFF90D4C4, ModuleType.RENDER);
        this.offerProperties(hideNSeek, tnt, sand);
        this.listeners.add(new Listener<TntEvent>("tracers_render_listener") {
            @Override
            public void call(TntEvent event) {
                x1 = event.getX();
                y2 = event.getY();
                z3 = event.getZ();

            }
        });

        this.listeners.add(new Listener<RenderEvent>("tracers_render_listener") {
            @Override
            public void call(RenderEvent event) {
                if (tnt.getValue())
                {

                    GlStateManager.pushMatrix();
                    RenderMethods.enableGL3D();
                    double x = x1 - RenderManager.renderPosX;
                    double y = y2 - RenderManager.renderPosY;
                    double z = z3 - RenderManager.renderPosZ;
                    GlStateManager.color(255, 255, 255, 1F);
                    boolean bobbing = minecraft.gameSettings.viewBobbing;
                    GL11.glLineWidth(3);
                    GL11.glLoadIdentity();
                    minecraft.gameSettings.viewBobbing = false;
                    minecraft.entityRenderer.orientCamera(event.getPartialTicks());
                    GL11.glBegin(GL11.GL_LINES);
                    GL11.glVertex3d(x, y, z);
                    GL11.glVertex3d(0, minecraft.thePlayer.getEyeHeight(), 0);
                    GL11.glEnd();
                    minecraft.gameSettings.viewBobbing = bobbing;
                    RenderMethods.disableGL3D();
                    GlStateManager.popMatrix();
                }
                if (hideNSeek.getValue()) {
                    GlStateManager.pushMatrix();
                    RenderMethods.enableGL3D();

                    double x, y, z;

                    for (Entity entity : (List<Entity>) minecraft.theWorld.loadedEntityList) {
                        if (!entity.isEntityAlive() || !(entity instanceof EntityFallingBlock)) {
                            continue;
                        }

                        x = interpolate(entity.lastTickPosX, entity.posX, event.getPartialTicks(), minecraft.getRenderManager().renderPosX);
                        y = interpolate(entity.lastTickPosY, entity.posY, event.getPartialTicks(), minecraft.getRenderManager().renderPosY);
                        z = interpolate(entity.lastTickPosZ, entity.posZ, event.getPartialTicks(), minecraft.getRenderManager().renderPosZ);
                        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(x - 0.5D, y, z - 0.5D, x + 0.5D, y + 1D, z + 0.5D);
                        float distance = minecraft.thePlayer.getDistanceToEntity(entity);

                        if (distance <= 32) {
                            GlStateManager.color(1F, distance / 32F, 0F, 0.35F);
                        } else {
                            GlStateManager.color(0F, 0.9F, 0F, 0.35F);
                        }

                        RenderMethods.drawBox(axisAlignedBB);
                    }

                    RenderMethods.disableGL3D();
                    GlStateManager.popMatrix();
                }
            }
        });
    }

    private double interpolate(double lastI, double i, float ticks, double ownI) {
        return (lastI + (i - lastI) * ticks) - ownI;
    }
}
