package me.comu.client.module.impl.toggle.render;

import me.comu.api.event.Listener;
import me.comu.api.minecraft.render.RenderMethods;
import me.comu.api.stopwatch.Stopwatch;
import me.comu.client.core.Gun;
import me.comu.client.events.RenderGameOverlayEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.NumberProperty;
import me.comu.client.utils.ColorHelper;
import me.comu.client.utils.FontManager;
import me.comu.client.utils.FontRenderUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

public final class Radar extends ToggleableModule
{

    private final NumberProperty<Float> scale = new NumberProperty<>(2.0f, 0.1f, 5.0F, 0.1F, "Scaling", "scale","sc"),
            sizing = new NumberProperty<>(125f, 5F, 500F, 50F, "Size", "s"),
            x = new NumberProperty<>(100F, 1F, 1920F, 50F, "X-Axis", "x"),
            y = new NumberProperty<>(125F, 1F, 1080F, 25F, "Y-Axis", "y");
    private Stopwatch timer;
    FontManager fontManager = new FontManager();

    public Radar()
    {
        super("Radar", new String[] {"radar","compass"}, 0xFF90D4C4, ModuleType.RENDER);
        this.offerProperties(scale, sizing, x, y);
        this.listeners.add(new Listener<RenderGameOverlayEvent>("tracers_render_listener")
        {
            @Override
            public void call(RenderGameOverlayEvent event) {
                if (event.getType() == RenderGameOverlayEvent.Type.IN_GAME) {
                    final int size = Math.round(sizing.getValue());
                    final float xOffset = x.getValue();
                    final float yOffset = y.getValue();
                    final float playerOffsetX = (float) minecraft.thePlayer.posX;
                    final float playerOffSetZ = (float) minecraft.thePlayer.posZ;
                    RenderMethods.drawFilledCircle(xOffset + size / 2f, yOffset + size / 2f, size / 2f - 4, ColorHelper.getColor(50, 100), 0);
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(xOffset + size / 2f, yOffset + size / 2f, 0.0f);


                    RenderMethods.drawRect(-0.5f, -size / 2f + 4, 0.5f, size / 2f - 4, ColorHelper.getColor(255, 80));
                    RenderMethods.drawRect(-size / 2f + 4, -0.5f, size / 2f - 4, 0.5f, ColorHelper.getColor(255, 80));

                    GlStateManager.rotate(-minecraft.thePlayer.rotationYaw, 0.0f, 0.0f, 1.0f);
                    GlStateManager.popMatrix();

                    RenderMethods.drawCircle(xOffset + size / 2f, yOffset + size / 2f, (float) (size / 2 - 4), 72, ColorHelper.getColor(0, 200));
                    final FontRenderUtils normal = fontManager.getFont("SFB 8");
                    final float angle2 = -minecraft.thePlayer.rotationYaw + 90.0f;
                    float x2 = (float) ((size / 2 + 4) * Math.cos(Math.toRadians(angle2))) + xOffset + size / 2f;
                    float y2 = (float) ((size / 2 + 4) * Math.sin(Math.toRadians(angle2))) + yOffset + size / 2f;
                    normal.drawStringWithShadow("N", x2 - normal.getWidth("N") / 2.0f, y2 - 1.0f, -1);
                    x2 = (float) ((size / 2 + 4) * Math.cos(Math.toRadians(angle2 + 90.0f))) + xOffset + size / 2f;
                    y2 = (float) ((size / 2 + 4) * Math.sin(Math.toRadians(angle2 + 90.0f))) + yOffset + size / 2f;
                    normal.drawStringWithShadow("E", x2 - normal.getWidth("E") / 2.0f, y2 - 1.0f, -1);
                    x2 = (float) ((size / 2 + 4) * Math.cos(Math.toRadians(angle2 + 180.0f))) + xOffset + size / 2f;
                    y2 = (float) ((size / 2 + 4) * Math.sin(Math.toRadians(angle2 + 180.0f))) + yOffset + size / 2f;
                    normal.drawStringWithShadow("S", x2 - normal.getWidth("S") / 2.0f, y2 - 1.0f, -1);
                    x2 = (float) ((size / 2 + 4) * Math.cos(Math.toRadians(angle2 - 90.0f))) + xOffset + size / 2f;
                    y2 = (float) ((size / 2 + 4) * Math.sin(Math.toRadians(angle2 - 90.0f))) + yOffset + size / 2f;
                    normal.drawStringWithShadow("W", x2 - normal.getWidth("W") / 2.0f, y2 - 1.0f, -1);

                    for (final Object o : minecraft.theWorld.getLoadedEntityList()) {
                        if (o instanceof EntityPlayer) {
                            final EntityPlayer ent = (EntityPlayer) o;
                            if (!ent.isEntityAlive() || ent == minecraft.thePlayer || ent.isInvisible() || ent.isInvisibleToPlayer(minecraft.thePlayer)) {
                                continue;
                            }
                            final float pTicks = minecraft.timer.renderPartialTicks;
                            final float posX = (float) ((ent.posX + (ent.posX - ent.lastTickPosX) * pTicks - playerOffsetX) * scale.getValue());
                            final float posZ = (float) ((ent.posZ + (ent.posZ - ent.lastTickPosZ) * pTicks - playerOffSetZ) * scale.getValue());
                            int color;
                            if (Gun.getInstance().getFriendManager().isFriend(ent.getName())) {
                                color = (minecraft.thePlayer.canEntityBeSeen(ent) ? ColorHelper.getColor(0, 195, 255) : ColorHelper.getColor(0, 195, 255));
                            } else {
                                color = (minecraft.thePlayer.canEntityBeSeen(ent) ? ColorHelper.getColor(255, 0, 0) : ColorHelper.getColor(255, 255, 0));
                            }
                            final float cos = (float) Math.cos(minecraft.thePlayer.rotationYaw * 0.017453292519943295);
                            final float sin = (float) Math.sin(minecraft.thePlayer.rotationYaw * 0.017453292519943295);
                            final float rotY = -(posZ * cos - posX * sin);
                            final float rotX = -(posX * cos + posZ * sin);
                            final float var143 = 0.0f - rotX;
                            final float var144 = 0.0f - rotY;
                            if (MathHelper.sqrt_double(var143 * var143 + var144 * var144) > size / 2f - 4) {
                                final float angle3 = findAngle(0.0f, rotX, 0.0f, rotY);
                                final float x3 = (float) (size / 2 * Math.cos(Math.toRadians(angle3))) + xOffset + size / 2f;
                                final float y3 = (float) (size / 2 * Math.sin(Math.toRadians(angle3))) + yOffset + size / 2f;
                                GlStateManager.pushMatrix();
                                GlStateManager.translate(x3, y3, 0.0f);
                                GlStateManager.rotate(angle3, 0.0f, 0.0f, 1.0f);
                                GlStateManager.scale(1.5, 0.5, 0.5);
                                RenderMethods.drawCircle(0.0f, 0.0f, 1.5f, 3, ColorHelper.getColor(46));
                                RenderMethods.drawCircle(0.0f, 0.0f, 1.0f, 3, color);
                                GlStateManager.popMatrix();
                            } else {
                                RenderMethods.drawBorderedRect((float) (xOffset + size / 2f + rotX - 1.5),
                                        (float) (yOffset + size / 2f + rotY - 1.5),
                                        (float) (xOffset + size / 2f + rotX + 1.5), (float) (yOffset + size / 2f + rotY + 1.5),
                                        0.5f,
                                        color,
                                        ColorHelper.getColor(46));
                            }
                        }
                    }
                }
            }
        });
    }
    private float findAngle(final float x, final float x2, final float y, final float y2) {
        return (float)(Math.atan2(y2 - y, x2 - x) * 180.0 / 3.141592653589793);
    }


}
