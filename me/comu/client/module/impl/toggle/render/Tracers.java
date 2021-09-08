package me.comu.client.module.impl.toggle.render;

import me.comu.api.event.Listener;
import me.comu.api.minecraft.render.RenderMethods;
import me.comu.client.core.Gun;
import me.comu.client.events.RenderEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.EnumProperty;
import me.comu.client.properties.NumberProperty;
import me.comu.client.properties.Property;
import me.comu.client.utils.ClientUtils;
import me.comu.client.utils.GuiUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;

import javax.vecmath.Vector3f;
import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

public final class Tracers extends ToggleableModule
{
    private final Property<Boolean> box = new Property<>(true, "Box", "b"), itemName = new Property<>(true, "Item-Names","itemn","itemnames","itemname","item-name","in"), spine = new Property<>(false, "Spine"), items = new Property<>(false, "Items", "item", "i"), lines = new Property<>(true, "Lines", "line", "l"), players = new Property<>(true, "Players", "player", "p"), healthBar = new Property<>(false, "HealthBar", "health-bar", "hb", "health"), enderpearls = new Property<>(true, "Enderpearls","epearl","pearls", "epearls", "enderpearl", "pearl"), invisibles = new Property<>(true, "Invisibles", "invis", "inv", "invisible"), monsters = new Property<>(false, "Monsters","mon", "monster", "mob", "m"), animals = new Property<>(false, "Animals", "animal", "ani", "a"), self = new Property<>(false, "Self", "s", "body", "myself");
    public static Property<Boolean> esp = new Property<>(false, "ESP", "e");
    private final Property<Boolean> purple = new Property<>(false, "Purple", "purpleprison");
    private final EnumProperty<ESPColor> espColor = new EnumProperty<>(ESPColor.HEALTH, "ESP-Color", "ecolor", "espcolor","color");
    private final EnumProperty<Mode> mode = new EnumProperty<>(Mode.OUTLINE, "Mode", "modes", "m");
    private final EnumProperty<Type> type = new EnumProperty<>(Type.NORMAL, "Type", "types", "t");
    private final NumberProperty<Float> width = new NumberProperty<>(1.8F, 1F, 5F, 0.1F, "Width", "w");


    public Tracers()
    {
        super("Tracers", new String[] {"tracers", "entityesp", "eesp", "esp"}, 0xFFDBE300, ModuleType.RENDER);
        this.offerProperties(box, itemName, players, spine, enderpearls, width, items, monsters, purple, animals, lines, invisibles, mode, type, esp, self, espColor);
        this.listeners.add(new Listener<RenderEvent>("tracers_render_listener")
        {
            @Override
            public void call(RenderEvent event)
            {
                GlStateManager.pushMatrix();
                RenderMethods.enableGL3D();
                double x, y, z;

                for (Entity entity : (List<Entity>) minecraft.theWorld.loadedEntityList)
                {
                    if (!entity.isEntityAlive())
                    {
                        continue;
                    }

                    if (isValidEntity(entity))
                    {
                        switch(type.getValue()) {
                            case SPECIFIC:
                                    if (!(Gun.getInstance().getFriendManager().isFriend(entity.getName()) || Gun.getInstance().getEnemyManager().isEnemy(entity.getName()) || Gun.getInstance().getStaffManager().isStaff(entity.getName()))) {
                                        continue;
                                }
                                break;
                            case NON_SPECIFIC:
                                    if (Gun.getInstance().getFriendManager().isFriend(entity.getName()) || Gun.getInstance().getEnemyManager().isEnemy(entity.getName()) || Gun.getInstance().getStaffManager().isStaff(entity.getName())) {
                                        continue;
                                    }
                                break;

                        }
                        x = interpolate(entity.lastTickPosX, entity.posX, event.getPartialTicks(), minecraft.getRenderManager().renderPosX);
                        y = interpolate(entity.lastTickPosY, entity.posY, event.getPartialTicks(), minecraft.getRenderManager().renderPosY);
                        z = interpolate(entity.lastTickPosZ, entity.posZ, event.getPartialTicks(), minecraft.getRenderManager().renderPosZ);
                        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(x - 0.4D, y, z - 0.4D, x + 0.4D, y + 2D, z + 0.4D);

                        if (!(entity instanceof EntityPlayer))
                        {
                            axisAlignedBB = new AxisAlignedBB(x - 0.4D, y, z - 0.4D, x + 0.4D, y + entity.getEyeHeight() + 0.35D, z + 0.4D);
                        }

                        if (entity instanceof EntityItem) {
                            me.comu.client.module.impl.active.render.Render renderModule = (me.comu.client.module.impl.active.render.Render) Gun.getInstance().getModuleManager().getModuleByAlias("Render");
                            Property itemPhysics = renderModule.getPropertyByAlias("itemphysics");
                            if ((boolean)itemPhysics.getValue()) {
                                axisAlignedBB = new AxisAlignedBB(x - 0.16D, y , z - 0.16D, x + 0.16D, y + entity.getEyeHeight() , z + 0.16D);
                            } else
                            axisAlignedBB = new AxisAlignedBB(x - 0.16D, y + 0.13D, z - 0.16D, x + 0.16D, y + entity.getEyeHeight() + 0.25D, z + 0.16D);

                            if (itemName.getValue()) {
                                EntityItem entityItem = (EntityItem) entity;
                                minecraft.fontRenderer.drawStringWithShadow(entityItem.getName(), (float)x-0.16F, (float)y+0.13F, 0xFFFFFFFF);
                            }
                        }
                        else if (entity instanceof EntityEnderPearl)
                        {
                            axisAlignedBB = new AxisAlignedBB(x - 0.16D, y - 0.2D, z - 0.16D, x + 0.16D, y + entity.getEyeHeight() - 0.1D, z + 0.16D);
                        }

                        if (Gun.getInstance().getFriendManager().isFriend(entity.getName()))
                        {
                            GlStateManager.color(0.4007F, 400.40F, 0.22F, 0.65F);
                        }
                        else
                        {
                            float distance = minecraft.thePlayer.getDistanceToEntity(entity);

                            if (distance <= 32)
                            {
                                GlStateManager.color(2F, distance / 32F, 0F, 0.45F);
                            }
                            else
                            {
                                GlStateManager.color(0F, 0.9F, 0F, 0.45F);
                            }
                        }
                        if (Gun.getInstance().getEnemyManager().isEnemy(entity.getName()))
                        {
                            GlStateManager.color(0.87F, 0.70F, 0.92F, 0.65F);
                        } else if (Gun.getInstance().getStaffManager().isStaff(entity.getName())) {
                            GlStateManager.color(0.67F, 0.50F, 0.1002F, 0.65F);
                        } else if (Gun.getInstance().getFriendManager().isFriend(entity.getName()))
                        {
                            GlStateManager.color(0.17F, 0.70F, 0.92F, 0.65F);
                        }
                        else
                        {
                            float distance = minecraft.thePlayer.getDistanceToEntity(entity);

                            if (distance <= 32)
                            {
                                GlStateManager.color(1F, distance / 32F, 0F, 0.45F);
                            }
                            else
                            {
                                GlStateManager.color(0F, 0.9F, 0F, 0.45F);
                            }
                        }

                        boolean bobbing = minecraft.gameSettings.viewBobbing;


                        if (lines.getValue())
                        {
                            GlStateManager.pushMatrix();
                            GL11.glLineWidth(width.getValue());
                            GL11.glLoadIdentity();
                            minecraft.gameSettings.viewBobbing = false;
                            minecraft.entityRenderer.orientCamera(event.getPartialTicks());
                            GL11.glBegin(GL11.GL_LINES);
                            GL11.glVertex3d(0, minecraft.thePlayer.getEyeHeight(), 0);

                            if (mode.getValue() == Mode.OUTLINE && esp.getValue())
                            {
                                GL11.glVertex3d(x, y + 1, z);
                            }
                            else
                            {
                                GL11.glVertex3d(x, y, z);
                            }

                            if (spine.getValue())
                            {
                                if (mode.getValue() == Mode.OUTLINE && esp.getValue())
                                {
                                    GL11.glVertex3d(x, y + 1, z);
                                }
                                else
                                {
                                    GL11.glVertex3d(x, y, z);
                                }

                                if (entity instanceof EntityPlayer)
                                {
                                    GL11.glVertex3d(x, y + 1.35, z); //entity.getEyeHeight(), z);
                                }
                                else
                                {
                                    GL11.glVertex3d(x, y + entity.getEyeHeight(), z);
                                }
                            }

                            GL11.glEnd();
                            GlStateManager.popMatrix();
                        }

                        if (box.getValue())
                        {
                            GlStateManager.pushMatrix();
                            GlStateManager.translate(x, y, z);
                            GlStateManager.rotate(-entity.rotationYaw, 0F, entity.height, 0F);
                            GlStateManager.translate(-x, -y, -z);

                            if (entity instanceof EntityItem || entity instanceof EntityEnderPearl)
                            {
                                float distance = minecraft.thePlayer.getDistanceToEntity(entity);

                                if (distance <= 32)
                                {
                                    GlStateManager.color(1F, distance / 32F, 0F, 0.25F);
                                }
                                else
                                {
                                    GlStateManager.color(0F, 0.9F, 0F, 0.25F);
                                }

                                RenderMethods.drawBox(axisAlignedBB);
                            }

                            RenderMethods.drawOutlinedBox(axisAlignedBB);
                            GlStateManager.popMatrix();
                        }


                        if (esp.getValue())
                        {
                            if (mode.getValue() == Mode.BOXFILL)
                            {
                                GlStateManager.pushMatrix();
                                GlStateManager.translate(x, y, z);
                                GlStateManager.rotate(-entity.rotationYaw, 0F, entity.height, 0F);
                                GlStateManager.translate(-x, -y, -z);

                                if (Gun.getInstance().getFriendManager().isFriend(entity.getName()))
                                {
                                    GlStateManager.color(0.27F, 0.70F, 0.92F, 0.15F);
                                } else if (Gun.getInstance().getEnemyManager().isEnemy(entity.getName())) {
                                    GlStateManager.color(0.87F, 0.70F, 0.92F, 0.2F);
                                } else if (Gun.getInstance().getStaffManager().isStaff(entity.getName())) {
                                    GlStateManager.color(0.67F, 0.50F, 0.1002F, 0.2F);
                                }
                                else
                                {
                                    float distance = minecraft.thePlayer.getDistanceToEntity(entity);

                                    if (distance <= 32)
                                    {
                                        GlStateManager.color(1F, distance / 32F, 0F, 0.15F);
                                    }
                                    else
                                    {
                                        GlStateManager.color(0F, 0.9F, 0F, 0.15F);
                                    }
                                }

                                RenderMethods.drawBox(axisAlignedBB);
                                RenderMethods.drawOutlinedBox(axisAlignedBB);
                                GlStateManager.popMatrix();
                            }
                            else if (mode.getValue() == Mode.CROSS)
                            {
                                GlStateManager.pushMatrix();
                                GlStateManager.translate(x, y, z);
                                GlStateManager.rotate(-entity.rotationYaw, 0F, entity.height, 0F);
                                GlStateManager.translate(-x, -y, -z);
                                RenderMethods.drawOutlinedBox(axisAlignedBB);
                                RenderMethods.renderCrosses(axisAlignedBB);
                                GlStateManager.popMatrix();
                                // TODO: fix all this shit
                                } else if (mode.getValue() == Mode.SHADERS) {

                            }

                            else if (mode.getValue() == Mode.RECT) {
                                GL11.glPushMatrix();
                                for (final Object o : ClientUtils.world().loadedEntityList) {
                                    if (o instanceof EntityLivingBase && o != ClientUtils.mc().thePlayer) {
                                        final EntityLivingBase entity2 = (EntityLivingBase)o;
                                        final float distance2 = ClientUtils.player().getDistanceToEntity(entity);
                                        final float health2 = entity2.getHealth();
                                        final float posX2 = (float)((float)entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * ClientUtils.mc().timer.renderPartialTicks);
                                        final float posY2 = (float)((float)entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * ClientUtils.mc().timer.renderPartialTicks);
                                        final float posZ2 = (float)((float)entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * ClientUtils.mc().timer.renderPartialTicks);
                                        if (!Gun.getInstance().getFriendManager().isFriend(entity.getName())) {
                                            final float percent = health2 / 2.0f;
                                            if (percent >= 6.0f) {
                                                GuiUtils.draw2D(entity, posX2 - RenderManager.renderPosX, posY2 - RenderManager.renderPosY, posZ2 - RenderManager.renderPosZ, 0.1f, 1.0f, 0.1f, 255.0f);
                                            }
                                            if (percent < 6.0f) {
                                                GuiUtils.draw2D(entity, posX2 - RenderManager.renderPosX, posY2 - RenderManager.renderPosY, posZ2 - RenderManager.renderPosZ, 1.0f, 0.5f, 0.0f, 255.0f);
                                            }
                                            if (percent >= 3.0f) {
                                                continue;
                                            }
                                            GuiUtils.draw2D(entity, posX2 - RenderManager.renderPosX, posY2 - RenderManager.renderPosY, posZ2 - RenderManager.renderPosZ, 0.0f, 0.0f, 0.0f, 255.0f);
                                        }
                                        else {
                                            GuiUtils.draw2D(entity, posX2 - RenderManager.renderPosX, posY2 - RenderManager.renderPosY, posZ2 - RenderManager.renderPosZ, 0.0f, 0.7f, 0.8f, 255.0f);
                                        }
                                    }

                                }
                                GL11.glPopMatrix();
                            } else if (mode.getValue() == Mode.RECT2D) {
                                if ((entity instanceof EntityPlayer || entity instanceof EntityEnderPearl) && entity != minecraft.thePlayer) {
                                    if (enderpearls.getValue()) {
                                        if (entity instanceof EntityEnderPearl) {
                                            GL11.glPushMatrix();
                                            final float posX2 = (float) ((float) entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * ClientUtils.mc().timer.renderPartialTicks);
                                            final float posY2 = (float) ((float) entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * ClientUtils.mc().timer.renderPartialTicks);
                                            final float posZ2 = (float) ((float) entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * ClientUtils.mc().timer.renderPartialTicks);
                                            {
                                                GuiUtils.draw2D(entity, posX2 - RenderManager.renderPosX, posY2 - RenderManager.renderPosY, posZ2 - RenderManager.renderPosZ, 0.0f, 0.7f, 0.8f, 255.0f);
                                            }
                                            GL11.glPopMatrix();
                                        }
                                    }
                                    if (purple.getValue()) {
                                        if (hasOmega((EntityPlayer) entity)) {
                                            try {
                                                if (entity.isInvisible() || !entity.isEntityAlive()) {
                                                    continue;
                                                }
                                                y += (float) (0.4 + entity.getEyeHeight() - (entity.isSneaking() ? 0.2 : 0.0));
                                                final int scale = minecraft.gameSettings.guiScale;
                                                minecraft.gameSettings.guiScale = 2;
                                                minecraft.entityRenderer.setupCameraTransform(minecraft.timer.renderPartialTicks, 0);
                                                minecraft.gameSettings.guiScale = scale;
                                                if (project2D((float) x, (float) y, (float) z) == null || project2D((float) x, (float) y - 0.5f - entity.getEyeHeight(), (float) z) == null) {
                                                    continue;
                                                }
                                                final Vector3f posTop = project2D((float) x, (float) y, (float) z);
                                                final Vector3f posBottom = project2D((float) x, (float) y - 0.6f - entity.getEyeHeight() + (entity.isSneaking() ? 0.2f : 0.0f), (float) z);
                                                if (posTop.getZ() < 0.0f || posTop.getZ() > 1.0f || posBottom.getZ() < 0.0f) {
                                                    continue;
                                                }
                                                if (posBottom.getZ() > 1.0f) {
                                                    continue;
                                                }
                                                minecraft.gameSettings.guiScale = 2;
                                                minecraft.entityRenderer.setupOverlayRendering();
                                                minecraft.gameSettings.guiScale = scale;
                                                posTop.setY(Display.getHeight() / 2 - posTop.getY());
                                                posBottom.setY(Display.getHeight() / 2 - posBottom.getY());
                                                final double[] corners = getCorners(entity, posTop, posBottom);
                                                final float width = (float) corners[2] - (float) corners[0];
                                                final double height = corners[3] - corners[1];
                                                final double inc = height / 20.0;
                                                final double bottomleft = corners[1] + height;
                                                final double topright = corners[0] + width;
                                                final float health = ((EntityPlayer) entity).getHealth();

                                                GL11.glPushMatrix();
                                                Gui.drawRect(corners[0] - 0.5, corners[1] - 0.5, corners[0] + width / 4.0f + 0.5, corners[1] + 1.0, -16777216);
                                                Gui.drawRect(corners[0] - 0.5, corners[1] - 0.5, corners[0] + 1.0, corners[1] + height / 4.0 + 0.5, -16777216);
                                                Gui.drawRect(corners[0], corners[1], corners[0] + width / 4.0f, corners[1] + 0.5, new Color(50, 150, 255).getRGB());
                                                Gui.drawRect(corners[0], corners[1], corners[0] + 0.5, corners[1] + height / 4.0, new Color(50, 150, 255).getRGB());
                                                Gui.drawRect(topright + 0.5, corners[1] - 0.5, topright - width / 4.0f - 0.5, corners[1] + 1.0, -16777216);
                                                Gui.drawRect(topright - 0.5, corners[1] - 0.5, topright + 1.0, corners[1] + height / 4.0 + 0.5, -16777216);
                                                Gui.drawRect(topright, corners[1], topright - width / 4.0f, corners[1] + 0.5, new Color(50, 150, 255).getRGB());
                                                Gui.drawRect(topright, corners[1], topright + 0.5, corners[1] + height / 4.0, new Color(50, 150, 255).getRGB());
                                                Gui.drawRect(corners[0] - 0.5, bottomleft - 0.5, corners[0] + width / 4.0f + 0.5, bottomleft + 1.0, -16777216);
                                                Gui.drawRect(corners[0] - 0.5, bottomleft - height / 4.0 - 0.5, corners[0] + 1.0, bottomleft + 0.5, -16777216);
                                                Gui.drawRect(corners[0], bottomleft, corners[0] + width / 4.0f, bottomleft + 0.5, new Color(50, 150, 255).getRGB());
                                                Gui.drawRect(corners[0], bottomleft - height / 4.0, corners[0] + 0.5, bottomleft, new Color(50, 150, 255).getRGB());
                                                Gui.drawRect(corners[2] - width / 4.0f, corners[3] - 0.5, corners[2] + 1.0, corners[3] + 1.0, -16777216);
                                                Gui.drawRect(corners[2] - 0.5, corners[3] - height / 4.0 - 0.5, corners[2] + 1.0, corners[3] + 1.0, -16777216);
                                                Gui.drawRect(corners[2] - width / 4.0f + 0.5, corners[3], corners[2] + 0.5, corners[3] + 0.5, new Color(50, 150, 255).getRGB());
                                                Gui.drawRect(corners[2], corners[3] - height / 4.0, corners[2] + 0.5, corners[3], new Color(50, 150, 255).getRGB());
                                                drawBorderedRect(corners[0] - 2.5, corners[1] - 0.5, corners[0] - 1.0, corners[3] + 1.0, 1.5, 0, -16777216);
                                                drawBorderedRect(corners[0] - 2.0, corners[3] - inc * 20.0, corners[0] - 1.5, corners[3] + 0.5, 1.5, 0, -13421773);
                                                drawBorderedRect(corners[0] - 2.0, corners[3] - inc * health, corners[0] - 1.5, corners[3] + 0.5, 1.0, 0, health > 18 ? Color.green.getRGB() : health > 16 ? -16733696 : health > 12 ? Color.yellow.getRGB() : health > 8 ? Color.ORANGE.getRGB() : Color.red.getRGB());
                                                GL11.glPopMatrix();
                                                GlStateManager.enableBlend();
                                                minecraft.entityRenderer.setupOverlayRendering();
                                                GlStateManager.disableBlend();
                                            } catch (Exception ex) {
                                            }
                                        }
                                    } else {

                                        try {
                                            if (entity.isInvisible() || !entity.isEntityAlive()) {
                                                continue;
                                            }
                                            y += (float) (0.4 + entity.getEyeHeight() - (entity.isSneaking() ? 0.2 : 0.0));
                                            final int scale = minecraft.gameSettings.guiScale;
                                            minecraft.gameSettings.guiScale = 2;
                                            minecraft.entityRenderer.setupCameraTransform(minecraft.timer.renderPartialTicks, 0);
                                            minecraft.gameSettings.guiScale = scale;
                                            if (project2D((float) x, (float) y, (float) z) == null || project2D((float) x, (float) y - 0.5f - entity.getEyeHeight(), (float) z) == null) {
                                                continue;
                                            }
                                            final Vector3f posTop = project2D((float) x, (float) y, (float) z);
                                            final Vector3f posBottom = project2D((float) x, (float) y - 0.6f - entity.getEyeHeight() + (entity.isSneaking() ? 0.2f : 0.0f), (float) z);
                                            if (posTop.getZ() < 0.0f || posTop.getZ() > 1.0f || posBottom.getZ() < 0.0f) {
                                                continue;
                                            }
                                            if (posBottom.getZ() > 1.0f) {
                                                continue;
                                            }
                                            minecraft.gameSettings.guiScale = 2;
                                            minecraft.entityRenderer.setupOverlayRendering();
                                            minecraft.gameSettings.guiScale = scale;
                                            posTop.setY(Display.getHeight() / 2 - posTop.getY());
                                            posBottom.setY(Display.getHeight() / 2 - posBottom.getY());
                                            final double[] corners = getCorners(entity, posTop, posBottom);
                                            final float width = (float) corners[2] - (float) corners[0];
                                            final double height = corners[3] - corners[1];
                                            final double inc = height / 20.0;
                                            final double bottomleft = corners[1] + height;
                                            final double topright = corners[0] + width;
                                            final float health = ((EntityPlayer) entity).getHealth();

                                            GL11.glPushMatrix();
                                            Gui.drawRect(corners[0] - 0.5, corners[1] - 0.5, corners[0] + width / 4.0f + 0.5, corners[1] + 1.0, -16777216);
                                            Gui.drawRect(corners[0] - 0.5, corners[1] - 0.5, corners[0] + 1.0, corners[1] + height / 4.0 + 0.5, -16777216);
                                            Gui.drawRect(corners[0], corners[1], corners[0] + width / 4.0f, corners[1] + 0.5, new Color(50, 150, 255).getRGB());
                                            Gui.drawRect(corners[0], corners[1], corners[0] + 0.5, corners[1] + height / 4.0, new Color(50, 150, 255).getRGB());
                                            Gui.drawRect(topright + 0.5, corners[1] - 0.5, topright - width / 4.0f - 0.5, corners[1] + 1.0, -16777216);
                                            Gui.drawRect(topright - 0.5, corners[1] - 0.5, topright + 1.0, corners[1] + height / 4.0 + 0.5, -16777216);
                                            Gui.drawRect(topright, corners[1], topright - width / 4.0f, corners[1] + 0.5, new Color(50, 150, 255).getRGB());
                                            Gui.drawRect(topright, corners[1], topright + 0.5, corners[1] + height / 4.0, new Color(50, 150, 255).getRGB());
                                            Gui.drawRect(corners[0] - 0.5, bottomleft - 0.5, corners[0] + width / 4.0f + 0.5, bottomleft + 1.0, -16777216);
                                            Gui.drawRect(corners[0] - 0.5, bottomleft - height / 4.0 - 0.5, corners[0] + 1.0, bottomleft + 0.5, -16777216);
                                            Gui.drawRect(corners[0], bottomleft, corners[0] + width / 4.0f, bottomleft + 0.5, new Color(50, 150, 255).getRGB());
                                            Gui.drawRect(corners[0], bottomleft - height / 4.0, corners[0] + 0.5, bottomleft, new Color(50, 150, 255).getRGB());
                                            Gui.drawRect(corners[2] - width / 4.0f, corners[3] - 0.5, corners[2] + 1.0, corners[3] + 1.0, -16777216);
                                            Gui.drawRect(corners[2] - 0.5, corners[3] - height / 4.0 - 0.5, corners[2] + 1.0, corners[3] + 1.0, -16777216);
                                            Gui.drawRect(corners[2] - width / 4.0f + 0.5, corners[3], corners[2] + 0.5, corners[3] + 0.5, new Color(50, 150, 255).getRGB());
                                            Gui.drawRect(corners[2], corners[3] - height / 4.0, corners[2] + 0.5, corners[3], new Color(50, 150, 255).getRGB());
                                            drawBorderedRect(corners[0] - 2.5, corners[1] - 0.5, corners[0] - 1.0, corners[3] + 1.0, 1.5, 0, -16777216);
                                            drawBorderedRect(corners[0] - 2.0, corners[3] - inc * 20.0, corners[0] - 1.5, corners[3] + 0.5, 1.5, 0, -13421773);
                                            drawBorderedRect(corners[0] - 2.0, corners[3] - inc * health, corners[0] - 1.5, corners[3] + 0.5, 1.0, 0, health > 18 ? Color.green.getRGB() : health > 16 ? -16733696 : health > 12 ? Color.yellow.getRGB() : health > 8 ? Color.ORANGE.getRGB() : Color.red.getRGB());
                                            GL11.glPopMatrix();
                                            GlStateManager.enableBlend();
                                            minecraft.entityRenderer.setupOverlayRendering();
                                            GlStateManager.disableBlend();
                                        } catch (Exception ex) {
                                        }
                                    }
                            }
                            }
                            else if (mode.getValue() == Mode.FILL) {
                                if (enderpearls.getValue()) {
                                    GL11.glPushMatrix();
                                    for (final Object o : ClientUtils.world().loadedEntityList) {
                                        if (o instanceof EntityEnderPearl) {
                                            final float distance2 = ClientUtils.player().getDistanceToEntity(entity);
                                            final float posX2 = (float)((float)entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * ClientUtils.mc().timer.renderPartialTicks);
                                            final float posY2 = (float)((float)entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * ClientUtils.mc().timer.renderPartialTicks);
                                            final float posZ2 = (float)((float)entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * ClientUtils.mc().timer.renderPartialTicks);{
                                                GuiUtils.draw2D(entity, posX2 - RenderManager.renderPosX, posY2 - RenderManager.renderPosY, posZ2 - RenderManager.renderPosZ, 0.0f, 0.7f, 0.8f, 255.0f);
                                            }
                                        }

                                    }
                                    GL11.glPopMatrix();

                                }
                            }
                            else if (mode.getValue() == Mode.RECTFILL) {
                                if ((entity instanceof EntityPlayer || entity instanceof EntityEnderPearl) && entity != minecraft.thePlayer) {
                                    if (enderpearls.getValue()) {
                                        if (entity instanceof EntityEnderPearl) {
                                            GL11.glPushMatrix();
                                            final float posX2 = (float) ((float) entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * ClientUtils.mc().timer.renderPartialTicks);
                                            final float posY2 = (float) ((float) entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * ClientUtils.mc().timer.renderPartialTicks);
                                            final float posZ2 = (float) ((float) entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * ClientUtils.mc().timer.renderPartialTicks);
                                            {
                                                GuiUtils.draw2D(entity, posX2 - RenderManager.renderPosX, posY2 - RenderManager.renderPosY, posZ2 - RenderManager.renderPosZ, 0.0f, 0.7f, 0.8f, 255.0f);
                                            }
                                            GL11.glPopMatrix();
                                        }
                                    }
                                    try {
                                        if (entity.isInvisible() || !entity.isEntityAlive()) {
                                            continue;
                                        }
                                        y += (float)(0.4 + entity.getEyeHeight() - (entity.isSneaking() ? 0.2 : 0.0));
                                        final int scale = minecraft.gameSettings.guiScale;
                                        minecraft.gameSettings.guiScale = 2;
                                        minecraft.entityRenderer.setupCameraTransform(minecraft.timer.renderPartialTicks, 0);
                                        minecraft.gameSettings.guiScale = scale;
                                        if (project2D((float) x, (float) y,(float) z) == null || project2D((float)x,(float) y - 0.5f - entity.getEyeHeight(), (float) z) == null) {
                                            continue;
                                        }
                                        final Vector3f posTop = project2D((float)x, (float)y,(float) z);
                                        final Vector3f posBottom = project2D((float)x, (float)y - 0.6f - entity.getEyeHeight() + (entity.isSneaking() ? 0.2f : 0.0f), (float)z);
                                        if (posTop.getZ() < 0.0f || posTop.getZ() > 1.0f || posBottom.getZ() < 0.0f) {
                                            continue;
                                        }
                                        if (posBottom.getZ() > 1.0f) {
                                            continue;
                                        }
                                        minecraft.gameSettings.guiScale = 2;
                                        minecraft.entityRenderer.setupOverlayRendering();
                                        minecraft.gameSettings.guiScale = scale;
                                        posTop.setY(Display.getHeight() / 2 - posTop.getY());
                                        posBottom.setY(Display.getHeight() / 2 - posBottom.getY());
                                        final double[] corners = getCorners(entity, posTop, posBottom);
                                        final float width = (float)corners[2] - (float)corners[0];
                                        final double height = corners[3] - corners[1];
                                        final double inc = height / 20.0;
                                        final double bottomleft = corners[1] + height;
                                        final double topright = corners[0] + width;
                                        final float health =  ((EntityPlayer) entity).getHealth();

                                        GL11.glPushMatrix();
                                        Gui.drawRect(corners[0] - 0.5, corners[1] - 0.5, corners[0] + width / 4.0f + 0.5, corners[1] + 1.0, -16777216);
                                        Gui.drawRect(corners[0] - 0.5, corners[1] - 0.5, corners[0] + 1.0, corners[1] + height / 4.0 + 0.5, -16777216);
                                        Gui.drawRect(corners[0], corners[1], corners[0] + width / 4.0f, corners[1] + 0.5, new Color(50, 150, 255).getRGB());
                                        Gui.drawRect(corners[0], corners[1], corners[0] + 0.5, corners[1] + height / 4.0, new Color(50, 150, 255).getRGB());
                                        Gui.drawRect(topright + 0.5, corners[1] - 0.5, topright - width / 4.0f - 0.5, corners[1] + 1.0, -16777216);
                                        Gui.drawRect(topright - 0.5, corners[1] - 0.5, topright + 1.0, corners[1] + height / 4.0 + 0.5, -16777216);
                                        Gui.drawRect(topright, corners[1], topright - width / 4.0f, corners[1] + 0.5, new Color(50, 150, 255).getRGB());
                                        Gui.drawRect(topright, corners[1], topright + 0.5, corners[1] + height / 4.0, new Color(50, 150, 255).getRGB());
                                        Gui.drawRect(corners[0] - 0.5, bottomleft - 0.5, corners[0] + width / 4.0f + 0.5, bottomleft + 1.0, -16777216);
                                        Gui.drawRect(corners[0] - 0.5, bottomleft - height / 4.0 - 0.5, corners[0] + 1.0, bottomleft + 0.5, -16777216);
                                        Gui.drawRect(corners[0], bottomleft, corners[0] + width / 4.0f, bottomleft + 0.5, new Color(50, 150, 255).getRGB());
                                        Gui.drawRect(corners[0], bottomleft - height / 4.0, corners[0] + 0.5, bottomleft, new Color(50, 150, 255).getRGB());
                                        Gui.drawRect(corners[2] - width / 4.0f, corners[3] - 0.5, corners[2] + 1.0, corners[3] + 1.0, -16777216);
                                        Gui.drawRect(corners[2] - 0.5, corners[3] - height / 4.0 - 0.5, corners[2] + 1.0, corners[3] + 1.0, -16777216);
                                        Gui.drawRect(corners[2] - width / 4.0f + 0.5, corners[3], corners[2] + 0.5, corners[3] + 0.5, new Color(50, 150, 255).getRGB());
                                        Gui.drawRect(corners[2], corners[3] - height / 4.0, corners[2] + 0.5, corners[3], new Color(50, 150, 255).getRGB());
                                        drawBorderedRect(corners[0] - 2.5, corners[1] - 0.5, corners[0] - 1.0, corners[3] + 1.0, 1.5, 0, -16777216);
                                        drawBorderedRect(corners[0] - 2.0, corners[3] - inc * 20.0, corners[0] - 1.5, corners[3] + 0.5, 1.5, 0, -13421773);
                                        drawBorderedRect(corners[0] - 2.0, corners[3] - inc * health, corners[0] - 1.5, corners[3] + 0.5, 1.0, 0, health > 18 ? Color.green.getRGB() : health > 16 ? -16733696 : health > 12 ? Color.yellow.getRGB() : health > 8 ? Color.ORANGE.getRGB() : Color.red.getRGB());
                                        GL11.glPopMatrix();
                                        GlStateManager.enableBlend();
                                        minecraft.entityRenderer.setupOverlayRendering();
                                        GlStateManager.disableBlend();

                                    } catch (Exception ex) {}
                                }
                            }

                                else if (mode.getValue() == Mode.NONE)
                                {
                                	
                                }
                            else if (mode.getValue() == Mode.PENIS)
                            {
                                GlStateManager.pushMatrix();
                                GlStateManager.translate(x, y, z);
                                GlStateManager.rotate(-entity.rotationYaw, 0.0F, entity.height, 0.0F);
                                GlStateManager.translate(-x, -y, -z);
                                GlStateManager.translate(x, y + entity.height / 2.0F - 0.225F, z);
                                GlStateManager.rotate(entity.rotationPitch, 1.0F, 0.0F, 0.0F);
                                GlStateManager.color(1.0F, 1.0F, 0.0F, 1.0F);
                                int lines = 20;
                                GlStateManager.translate(0.0D, 0.0D, 0.075D);
                                Cylinder shaft = new Cylinder();
                                shaft.setDrawStyle(100013);
                                shaft.draw(0.1F, 0.09F, 1.0F, 25, lines * 2);
                                GlStateManager.translate(0.0D, 0.0D, -0.075D);
                                GlStateManager.translate(-0.05D, 0.0D, 0.0D);
                                Sphere right = new Sphere();
                                right.setDrawStyle(100013);
                                right.draw(0.1F, 25, lines);
                                GlStateManager.translate(0.1D, 0.0D, 0.0D);
                                Sphere left = new Sphere();
                                left.setDrawStyle(100013);
                                left.draw(0.1F, 25, lines);
                                GlStateManager.color(1.0F, 0.2F, 1.0F, 1.0F);
                                GlStateManager.translate(-0.05D, 0.0D, 1.1D);
                                Sphere tip = new Sphere();
                                tip.setDrawStyle(100013);
                                tip.draw(0.09F, 25, lines);
                                GlStateManager.popMatrix();
                            }
                        }

                        minecraft.gameSettings.viewBobbing = bobbing;
                    }
                }

                RenderMethods.disableGL3D();
                GlStateManager.popMatrix();
            }
        });
    }

    /*   private void drawHealthBar(final Vector4f vector, final EntityLivingBase entityLivingBase) {
          final int dmg = (int)Math.round(255.0 - entityLivingBase.getHealth() * 255.0 / entityLivingBase.getMaxHealth());
          final int dmgint = 255 - dmg << 8 | dmg << 16;
          final float hpHeight = entityLivingBase.getHealth() * (vector.h - vector.y) / entityLivingBase.getMaxHealth();
          RenderHelper.drawRect(vector.x - 3.0f, vector.y - 0.5f, vector.x - 1.0f, vector.h + 0.5f, -1726934767);
          RenderHelper.drawRect(vector.x - 2.5f, vector.h - hpHeight, vector.x - 1.5f, vector.h, ColorHelper.changeAlpha(dmgint, 255));
      }
    */
    public boolean isValidEntity(Entity entity)
    {
        if (entity == null)
        {
            return false;
        }

        if (entity instanceof IMob)
        {
            return monsters.getValue();
        }

        if (entity instanceof IAnimals)
        {
            return animals.getValue();
        }

        if (entity instanceof IMob)
        {
            return monsters.getValue();
        }

        if (entity instanceof EntityItem)
        {
            return items.getValue();
        }

        if (entity instanceof EntityEnderPearl)
        {
            return enderpearls.getValue();
        }

        if (entity instanceof EntityPlayer)
        {
            EntityPlayer entityPlayer = (EntityPlayer) entity;

            if ((entityPlayer.equals(minecraft.thePlayer) && !self.getValue()) || (entityPlayer.isInvisible() && !invisibles.getValue()))
            {
                return false;
            }

            return players.getValue();
        }

        return false;
    }

    private double interpolate(double lastI, double i, float ticks, double ownI)
    {
        return (lastI + (i - lastI) * ticks) - ownI;
    }

    public void renderOne()
    {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(width.getValue() * 2F);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
        GL11.glClearStencil(0xF);
        GL11.glStencilFunc(GL11.GL_NEVER, 1, 0xF);
        GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
        GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_LINE);
    }

    public void renderTwo()
    {
        GL11.glStencilFunc(GL11.GL_NEVER, 0, 0xF);
        GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
        GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_FILL);
    }

    public void renderThree()
    {
        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
        GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_LINE);
    }

    public void renderFour(Entity renderEntity)
    {
        float[] color = {};

        if (espColor.getValue().equals(ESPColor.DISTANCE)) {
            if (renderEntity instanceof EntityLivingBase) {
                EntityLivingBase entity = (EntityLivingBase) renderEntity;

                if (Gun.getInstance().getFriendManager().isFriend(entity.getName())) {
                    color = new float[]{0.27F, 0.70F, 0.92F};
                } else if (Gun.getInstance().getEnemyManager().isEnemy(entity.getName())) {
                    color = new float[]{0.87F, 0.70F, 0.92F};
                } else if (Gun.getInstance().getStaffManager().isStaff(entity.getName())) {
                    color = new float[]{0.67F, 0.50F, 0.1002F};
                } else {
                    float distance = minecraft.thePlayer.getDistanceToEntity(entity);

                    if (distance <= 32) {
                        color = new float[]{1F, distance / 32F, 0F};
                    } else {
                        color = new float[]{0F, 0.9F, 0F};
                    }
                }
            } else {
                float distance = minecraft.thePlayer.getDistanceToEntity(renderEntity);

                if (distance <= 32) {
                    color = new float[]{1F, distance / 32F, 0F};
                } else {
                    color = new float[]{0F, 0.9F, 0F};
                }
            }
        } else {
            if (espColor.getValue().equals(ESPColor.HEALTH)) {
                if (renderEntity instanceof EntityLivingBase) {
                    EntityLivingBase entity = (EntityLivingBase) renderEntity;
                    float health = entity.getHealth();
                    if (health > 18) {
                        color = new float[]{0.5647059f, 0.93333334f, 0.5647059f};
                    } else if (health > 16) {
                        color = new float[]{0.0f, 0.39215687f, 0.0f};
                    } else if (health > 12) {
                        color = new float[]{1.0f, 1.0f, 0.0f};
                    } else if (health > 8) {
                        color = new float[]{1.0f, 0.84313726f, 0.0f};
                    } else if (health > 5) {
                        color = new float[]{1.0f, 0.0f, 0.0f};
                    } else {
                        color = new float[]{0.54509807f, 0.0f, 0.0f};
                    }
                }
            }
        }
        GlStateManager.color(color[0], color[1], color[2], 0.85F);
        renderFour();
    }

    private void renderFour()
    {
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_POLYGON_OFFSET_LINE);
        GL11.glPolygonOffset(1F, -2000000F);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
    }

    public void renderFive()
    {
        GL11.glPolygonOffset(1.0F, 2000000F);
        GL11.glDisable(GL11.GL_POLYGON_OFFSET_LINE);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_STENCIL_TEST);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_DONT_CARE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glPopAttrib();
    }
    

    public static void drawRect(int x, int y, int x1, int y1, int color)
    {
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_NICEST);
        Gui.drawRect(x, y, x1, y1, color);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_DONT_CARE);
        GL11.glHint(GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_DONT_CARE);
    }

    public static void drawBorderedRect(final double x, final double y, final double x2, final double y2, final double thickness, final int inside, final int outline) {
        double fix = 0.0;
        if (thickness < 1.0) {
            fix = 1.0;
        }
        Gui.drawRect(x + thickness, y + thickness, x2 - thickness, y2 - thickness, inside);
        Gui.drawRect(x, y + 1.0 - fix, x + thickness, y2, outline);
        Gui.drawRect(x, y, x2 - 1.0 + fix, y + thickness, outline);
        Gui.drawRect(x2 - thickness, y, x2, y2 - 1.0 + fix, outline);
        Gui.drawRect(x + 1.0 - fix, y2 - thickness, x2, y2, outline);
    }
    public double[] getCorners(final Entity e, final Vector3f posTop, final Vector3f posBottom) {
        final ScaledResolution scaledResolution = new ScaledResolution(minecraft, minecraft.displayWidth, minecraft.displayHeight);
        double x1 = 0.0;
        double x2 = 0.0;
        double y1 = 0.0;
        double y2 = 0.0;
        if (e instanceof EntityPlayer) {
            final double xDif = Math.abs(scaledResolution.getScaledWidth() / 2 - posTop.getX());
            final double fovMod = minecraft.entityRenderer.getFOVModifier(minecraft.timer.renderPartialTicks, minecraft.thePlayer.isInWater()) / 70.0f;
            final double scaleMod = Display.getWidth() / 856.0;
            final double distanceModifier = minecraft.thePlayer.getDistanceToEntity(e) / 10.0f;
            final double xModifier = xDif / minecraft.thePlayer.getDistanceToEntity(e);
            final double width = 15.0 / distanceModifier / fovMod * scaleMod + xModifier;
            final double height = posBottom.getY() - posTop.getY();
            y1 = posTop.getY();
            y2 = posBottom.getY();
            if (posBottom.getX() - width / 2.0 < posTop.getX() - width / 2.0) {
                x1 = posBottom.getX() - width / 2.0;
                x2 = posTop.getX() + width / 2.0;
            }
            if (posTop.getX() - width / 2.0 < posBottom.getX() - width / 2.0) {
                x1 = posTop.getX() - width / 2.0;
                x2 = posBottom.getX() + width / 2.0;
            }
        }
        return new double[] { x1, y1, x2, y2 };
    }

    public static Vector3f project2D(final float x, final float y, final float z) {
        final FloatBuffer screen_coords = GLAllocation.createDirectFloatBuffer(3);
        final IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
        final FloatBuffer modelview = GLAllocation.createDirectFloatBuffer(16);
        final FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
        screen_coords.clear();
        modelview.clear();
        projection.clear();
        viewport.clear();
        GL11.glGetFloat(2982, modelview);
        GL11.glGetFloat(2983, projection);
        GL11.glGetInteger(2978, viewport);
        final boolean ret = GLU.gluProject(x, y, z, modelview, projection, viewport, screen_coords);
        if (ret) {
            return new Vector3f(screen_coords.get(0) / 2.0f, screen_coords.get(1) / 2.0f, screen_coords.get(2));
        }
        return null;
    }
    private boolean hasOmega(EntityPlayer player) {
        if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof ItemAxe) {
            int sharpnessLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.SHARPNESS.effectId, player.getCurrentEquippedItem());
            return sharpnessLevel >= 80;
        }
        return false;
    }
    private double interpolate(double previous, double current, float delta) {
        return (previous + (current - previous) * delta);
    }
    private void renderNameTag(ItemStack stack, double x, double y, double z, float delta) {
        double tempY = y;
        tempY += (minecraft.thePlayer.isSneaking() ? 0.5D : 0.7D);
        Entity camera = minecraft.getRenderViewEntity();
        double originalPositionX = camera.posX;
        double originalPositionY = camera.posY;
        double originalPositionZ = camera.posZ;
        camera.posX = interpolate(camera.prevPosX, camera.posX, delta);
        camera.posY = interpolate(camera.prevPosY, camera.posY, delta);
        camera.posZ = interpolate(camera.prevPosZ, camera.posZ, delta);
        double distance = camera.getDistance(x + minecraft.getRenderManager().viewerPosX, y + minecraft.getRenderManager().viewerPosY, z + minecraft.getRenderManager().viewerPosZ);
        int width = minecraft.fontRenderer.getStringWidth(stack.getDisplayName()) / 2;
        double scale = 0.0018 + 0.003 * distance;
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
        GlStateManager.enableAlpha();
        if (distance <= 8) {
            scale = 0.0245D;
        }
        {
            minecraft.fontRenderer.drawStringWithShadow(stack.getDisplayName(), -width, -(minecraft.fontRenderer.FONT_HEIGHT - 1), 0xFFFFFF);
        }




            GlStateManager.popMatrix();


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

    public enum Mode
    {
         FILL, RECTFILL, BOXFILL, CROSS, OUTLINE, PENIS, RECT2D, RECT, SHADERS, NONE
    }

    public enum Type
    {
        SPECIFIC, NON_SPECIFIC, NORMAL
    }

    public enum ESPColor
    {
        HEALTH, DISTANCE
    }
}
