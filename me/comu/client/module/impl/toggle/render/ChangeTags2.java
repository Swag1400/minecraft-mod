package me.comu.client.module.impl.toggle.render;

import me.comu.api.event.Listener;
import me.comu.client.core.Gun;
import me.comu.client.events.PassSpecialRenderEvent;
import me.comu.client.events.RenderEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;

public final class ChangeTags2 extends ToggleableModule {

    public ChangeTags2() {
        super("ChangeTags", new String[]{"changetags"}, 0x54FF33, ModuleType.RENDER);
        this.listeners.add(new Listener<RenderEvent>("name_tags_render_listener") {
            @Override
            public void call(RenderEvent event) {
                for (Object o : minecraft.theWorld.playerEntities) {
                    Entity entity = (Entity) o;
                    EntityPlayer playerSelf = minecraft.thePlayer;
                    if (entity instanceof EntityPlayer) {
                        if (entity.isEntityAlive()) { // TODO: renders nametag on self
                            double x = interpolate(entity.lastTickPosX, entity.posX, event.getPartialTicks())
                                    - minecraft.getRenderManager().renderPosX;
                            double y = interpolate(entity.lastTickPosY, entity.posY, event.getPartialTicks())
                                    - minecraft.getRenderManager().renderPosY;
                            double z = interpolate(entity.lastTickPosZ, entity.posZ, event.getPartialTicks())
                                    - minecraft.getRenderManager().renderPosZ;
                            renderNameTag((EntityPlayer) entity, x, y, z, event.getPartialTicks());


                        }
                    }
                }
            }

        });
        this.listeners.add(new Listener<PassSpecialRenderEvent>("name_tags_pass_special_render_listener") {
            @Override
            public void call(PassSpecialRenderEvent event) {
                event.setCanceled(true);
            }
        });
    }


    private void renderNameTag(EntityPlayer player, double x, double y, double z, float delta) {
        double tempY = y;
        tempY += (player.isSneaking() ? 0.5D : 0.7D);
        Entity camera = minecraft.getRenderViewEntity();
        double originalPositionX = camera.posX;
        double originalPositionY = camera.posY;
        double originalPositionZ = camera.posZ;
        camera.posX = interpolate(camera.prevPosX, camera.posX, delta);
        camera.posY = interpolate(camera.prevPosY, camera.posY, delta);
        camera.posZ = interpolate(camera.prevPosZ, camera.posZ, delta);
        double distance = camera.getDistance(x + minecraft.getRenderManager().viewerPosX, y + minecraft.getRenderManager().viewerPosY, z + minecraft.getRenderManager().viewerPosZ);
        int width = minecraft.fontRenderer.getStringWidth(this.getDisplayName(player)) / 2;
        double scale = 0.0018 + 0.003 * distance;

        if (distance <= 8) {
            scale = 0.0245D;
        }


        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset(1, -1500000);
        GlStateManager.disableLighting();
        GlStateManager.translate((float) x, (float) tempY + 1.4F, (float) z);
        GlStateManager.rotate(-minecraft.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(minecraft.getRenderManager().playerViewX, minecraft.gameSettings.thirdPersonView == 2 ? -1.0F : 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-scale, -scale, scale);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        //RenderMethods.drawRect(-width - 2, -(minecraft.fontRenderer.FONT_HEIGHT + 1), width + 2F, 1.5F, 1.4F, 102,22,42);
        //RenderMethods.drawBorderedRectReliant(-width - 2, -(minecraft.fontRenderer.FONT_HEIGHT + 1), width + 2F, 1.5F, 1.4F, 0x77000000, 0x55000000);
        GlStateManager.enableAlpha();
        minecraft.fontRenderer.drawString(this.getDisplayName(player), -width, -(minecraft.fontRenderer.FONT_HEIGHT - 1), this.getDisplayColour(player));


        camera.posX = originalPositionX;
        camera.posY = originalPositionY;
        camera.posZ = originalPositionZ;
        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.disablePolygonOffset();
        GlStateManager.doPolygonOffset(1, 1500000);
        GlStateManager.popMatrix();
    }


    private String getDisplayName(EntityPlayer player) {
        String name = player.getDisplayName().getFormattedText();

        if (Gun.getInstance().getFriendManager().isFriend(player.getName())) {
            name = Gun.getInstance().getFriendManager().getFriendByAliasOrLabel(player.getName()).getAlias();
        }

        if (Gun.getInstance().getEnemyManager().isEnemy(player.getName())) {
            name = Gun.getInstance().getEnemyManager().getEnemyByAliasOrLabel(player.getName()).getAlias();
        }
        if (Gun.getInstance().getStaffManager().isStaff((player.getName()))) {
            name = Gun.getInstance().getStaffManager().getStaffByAliasOrLabel(player.getName()).getAlias();
        }

//        if (name.contains(minecraft.getSession().getUsername())) {
//            name = "You";
//        }
        EnumChatFormatting healthColor;
        if (player.getHealth() >= 16)
            healthColor= EnumChatFormatting.GREEN;
        else if (player.getHealth() >= 14)
            healthColor = EnumChatFormatting.DARK_GREEN;
                    else if (player.getHealth() >= 8)
                        healthColor = EnumChatFormatting.YELLOW;
                    else if (player.getHealth() >= 5)
                        healthColor = EnumChatFormatting.RED;
                    else
                        healthColor = EnumChatFormatting.DARK_RED;
        return name + " \247b" + healthColor + MathHelper.roundToPlace(player.getHealth(), 3);
    }

    private int getDisplayColour(EntityPlayer player) {
        int color = 0xFFAAAAAA;

        if (Gun.getInstance().getFriendManager().isFriend(player.getName())) {
            return 0xFF55C0ED;
        } else if (player.isInvisible()) {
            color = 0xFFef0147;
        } else if (Gun.getInstance().getEnemyManager().isEnemy(player.getName())) {
            color = 0xf442e8;
        } else if (Gun.getInstance().getStaffManager().isStaff(player.getName())) {
            color = 0xFFDD2E;
        }

        return color;
    }

    private double interpolate(double previous, double current, float delta) {
        return (previous + (current - previous) * delta);
    }


}
