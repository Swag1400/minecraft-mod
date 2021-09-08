package me.comu.client.module.impl.toggle.render;


import me.comu.api.event.Listener;
import me.comu.api.minecraft.render.RenderMethods;
import me.comu.client.events.EventTarget;
import me.comu.client.events.RenderEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.Property;
import me.comu.client.utils.ClientUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSign;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.util.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;

import java.util.ArrayList;
import java.util.List;

public class Trajectories extends ToggleableModule
{
    public Trajectories() {
        super("Trajectories", new String[] { "Trajectories", "trajec", "traj" }, -7285564, ModuleType.RENDER);
        this.offerProperties(new Property[0]);
        this.listeners.add(new Listener<RenderEvent>("trajectories_render_listener") {
            @EventTarget(4)
            @Override
            public void call(final RenderEvent event) {
                final double renderPosX = ClientUtils.player().lastTickPosX + (ClientUtils.player().posX - ClientUtils.player().lastTickPosX) * event.getPartialTicks();
                final double renderPosY = ClientUtils.player().lastTickPosY + (ClientUtils.player().posY - ClientUtils.player().lastTickPosY) * event.getPartialTicks();
                final double renderPosZ = ClientUtils.player().lastTickPosZ + (ClientUtils.player().posZ - ClientUtils.player().lastTickPosZ) * event.getPartialTicks();
                if (ClientUtils.player().getHeldItem() == null || ClientUtils.mc().gameSettings.thirdPersonView != 0) {
                    return;
                }
              if (!(ClientUtils.player().getHeldItem().getItem() instanceof ItemBow || ClientUtils.player().getHeldItem().getItem() instanceof ItemEnderPearl || ClientUtils.player().getHeldItem().getItem() instanceof ItemFishingRod || ClientUtils.player().getHeldItem().getItem() instanceof ItemExpBottle || ClientUtils.player().getHeldItem().getItem() instanceof ItemPotion)) {
                    return;
                }
                final Item item = ClientUtils.player().getHeldItem().getItem();
              if (item instanceof ItemPotion && !((ItemPotion) item).isSplash(ClientUtils.player().getHeldItem().getItemDamage()))
                return;
                double posX = renderPosX - MathHelper.cos(ClientUtils.player().rotationYaw / 180.0f * 3.1415927f) * 0.16f;
                double posY = renderPosY + ClientUtils.player().getEyeHeight() - 0.1000000014901161;
                double posZ = renderPosZ - MathHelper.sin(ClientUtils.player().rotationYaw / 180.0f * 3.1415927f) * 0.16f;
                double motionX = -MathHelper.sin(ClientUtils.player().rotationYaw / 180.0f * 3.1415927f) * MathHelper.cos(ClientUtils.player().rotationPitch / 180.0f * 3.1415927f) * ((item instanceof ItemBow) ? 1.0 : 0.4);
                double motionY = -MathHelper.sin(ClientUtils.player().rotationPitch / 180.0f * 3.1415927f) * ((item instanceof ItemBow) ? 1.0 : 0.4);
                double motionZ = MathHelper.cos(ClientUtils.player().rotationYaw / 180.0f * 3.1415927f) * MathHelper.cos(ClientUtils.player().rotationPitch / 180.0f * 3.1415927f) * ((item instanceof ItemBow) ? 1.0 : 0.4);
                final int var6 = 72000 - ClientUtils.player().getItemInUseCount();
                float power = var6 / 20.0f;
                power = (power * power + power * 2.0f) / 3.0f;
                if (power < 0.1) {
                    return;
                }
                if (power > 1.0f) {
                    power = 1.0f;
                }
                final float distance = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
                motionX /= distance;
                motionY /= distance;
                motionZ /= distance;
                final float pow = (item instanceof ItemBow) ? (power * 2.0f) : ((item instanceof ItemFishingRod) ? 1.25f : ((ClientUtils.player().getHeldItem().getItem() == Items.experience_bottle || ClientUtils.player().getHeldItem().getItem() == Items.potionitem) ? 0.9f : 1.0f));
                motionX *= pow * ((item instanceof ItemFishingRod) ? 0.75f : ((ClientUtils.player().getHeldItem().getItem() == Items.experience_bottle || ClientUtils.player().getHeldItem().getItem() == Items.potionitem) ? 0.75f : 1.5f));
                motionY *= pow * ((item instanceof ItemFishingRod) ? 0.75f : ((ClientUtils.player().getHeldItem().getItem() == Items.experience_bottle || ClientUtils.player().getHeldItem().getItem() == Items.potionitem) ? 0.75f : 1.5f));
                motionZ *= pow * ((item instanceof ItemFishingRod) ? 0.75f : ((ClientUtils.player().getHeldItem().getItem() == Items.experience_bottle || ClientUtils.player().getHeldItem().getItem() == Items.potionitem) ? 0.75f : 1.5f));
                RenderMethods.enableGL3D(1f);
                GlStateManager.pushMatrix();
                if (power > 0.6f) {
                    GlStateManager.color(0.0f, 1.0f, 0.0f, 1.0f);
                }
                else {
                    GlStateManager.color(0.8f, 0.5f, 0.0f, 1.0f);
                }
                final Tessellator tessellator = Tessellator.getInstance();
                final WorldRenderer renderer = tessellator.getWorldRenderer();
                renderer.startDrawing(3);
                renderer.addVertex(posX - renderPosX, posY - renderPosY, posZ - renderPosZ);
                final List<double[]> tm = new ArrayList<double[]>();
                final float size = (float)((item instanceof ItemBow) ? 0.3 : 0.25);
                boolean hasLanded = false;
                Entity landingOnEntity = null;
                MovingObjectPosition landingPosition = null;
                while (!hasLanded && posY > 0.0) {
                    final Vec3 present = new Vec3(posX, posY, posZ);
                    final Vec3 future = new Vec3(posX + motionX, posY + motionY, posZ + motionZ);
                    final MovingObjectPosition possibleLandingStrip = ClientUtils.mc().theWorld.rayTraceBlocks(present, future, false, true, false);
                    if (possibleLandingStrip != null && possibleLandingStrip.typeOfHit != MovingObjectPosition.MovingObjectType.MISS) {
                        landingPosition = possibleLandingStrip;
                        hasLanded = true;
                    }
                    final AxisAlignedBB arrowBox = new AxisAlignedBB(posX - size, posY - size, posZ - size, posX + size, posY + size, posZ + size);
                    final List entities = this.getEntitiesWithinAABB(arrowBox.addCoord(motionX, motionY, motionZ).expand(1.0, 1.0, 1.0));
                    for (final Object entity : entities) {
                        final Entity boundingBox = (Entity)entity;
                        if (boundingBox.canBeCollidedWith() && boundingBox != ClientUtils.player()) {
                            final float var7 = 0.3f;
                            final AxisAlignedBB var8 = boundingBox.getEntityBoundingBox().expand(0.30000001192092896, 0.30000001192092896, 0.30000001192092896);
                            final MovingObjectPosition possibleEntityLanding = var8.calculateIntercept(present, future);
                            if (possibleEntityLanding == null) {
                                continue;
                            }
                            hasLanded = true;
                            landingOnEntity = boundingBox;
                            landingPosition = possibleEntityLanding;
                        }
                    }
                    if (landingOnEntity != null) {
                        GlStateManager.color(1.0f, 0.0f, 0.0f, 1.0f);
                    }
                    posX += motionX;
                    posY += motionY;
                    posZ += motionZ;
                    final float motionAdjustment = 0.99f;
                    motionX *= 0.9900000095367432;
                    motionY *= 0.9900000095367432;
                    motionZ *= 0.9900000095367432;
                    motionY -= ((item instanceof ItemBow) ? 0.05 : 0.03);
                    renderer.addVertex(posX - renderPosX, posY - renderPosY, posZ - renderPosZ);

                }
                tessellator.draw();
                if (landingPosition != null && landingPosition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    GlStateManager.translate(posX - renderPosX, posY - renderPosY, posZ - renderPosZ);
                    final int side = landingPosition.field_178784_b.getIndex();
                    if (!(item instanceof ItemEnderPearl)) {
                        if (side == 2) {
                            GlStateManager.rotate(90.0f, 1.0f, 0.0f, 0.0f);
                        } else if (side == 3) {
                            GlStateManager.rotate(90.0f, 1.0f, 0.0f, 0.0f);
                        } else if (side == 4) {
                            GlStateManager.rotate(90.0f, 0.0f, 0.0f, 1.0f);
                        } else if (side == 5) {
                            GlStateManager.rotate(90.0f, 0.0f, 0.0f, 1.0f);
                        }
                        final Cylinder c = new Cylinder();
                        GlStateManager.rotate(-90.0f, 1.0f, 0.0f, 0.0f);
                        c.setDrawStyle(100011);
                        if (landingOnEntity != null) {
                            GlStateManager.color(0.0f, 0.0f, 0.0f, 1.0f);
                            GL11.glLineWidth(2.5f);
                            c.draw(0.6f, 0.3f, 0.0f, 4, 1);
                            GL11.glLineWidth(0.1f);
                            GlStateManager.color(1.0f, 0.0f, 0.0f, 1.0f);
                        }
                        c.draw(0.6f, 0.3f, 0.0f, 4, 1);
                    } else {
                        final int x = landingPosition.getPos().getX();
                        final int y = landingPosition.getPos().getY();
                        final int z = landingPosition.getPos().getZ();
                        final Block block1 = getBlock(x, y, z);
                        final Block block2 = getBlock(x, y + 1, z);
                        final Block block3 = getBlock(x, y + 2, z);
                        final boolean blockBelow = !(block1 instanceof BlockSign) && block1.getMaterial().isSolid();
                        final boolean blockLevel = !(block2 instanceof BlockSign) && block1.getMaterial().isSolid();
                        final boolean blockAbove = !(block3 instanceof BlockSign) && block1.getMaterial().isSolid();
                        if (getBlock(landingPosition.getPos()).getMaterial() != Material.air && blockBelow && blockLevel && blockAbove) {
                            GL11.glColor4f(0.2f, 1.0f, 0.2f, 0.75f);
                            ClientUtils.drawBoundingBox((AxisAlignedBB.fromBounds(x - RenderManager.renderPosX, y - RenderManager.renderPosY, z - RenderManager.renderPosZ, x - RenderManager.renderPosX + 1.0, y + 1 - RenderManager.renderPosY, z - RenderManager.renderPosZ + 1.0)));
                        } // change colors based on players distance
                    }

                }
                GlStateManager.popMatrix();
                RenderMethods.disableGL3D();
            }

            private List getEntitiesWithinAABB(final AxisAlignedBB bb) {
                final ArrayList list = new ArrayList();
                final int chunkMinX = MathHelper.floor_double((bb.minX - 2.0) / 16.0);
                final int chunkMaxX = MathHelper.floor_double((bb.maxX + 2.0) / 16.0);
                final int chunkMinZ = MathHelper.floor_double((bb.minZ - 2.0) / 16.0);
                final int chunkMaxZ = MathHelper.floor_double((bb.maxZ + 2.0) / 16.0);
                for (int x = chunkMinX; x <= chunkMaxX; ++x) {
                    for (int z = chunkMinZ; z <= chunkMaxZ; ++z) {
                        if (ClientUtils.mc().theWorld.getChunkProvider().chunkExists(x, z)) {
                            ClientUtils.mc().theWorld.getChunkFromChunkCoords(x, z).func_177414_a(ClientUtils.player(), bb, list, null);
                        }
                    }
                }
                return list;
            }
        });

    }
    public MovingObjectPosition getBlinkBlock() {
        final Vec3 var4 = minecraft.thePlayer.func_174824_e(minecraft.timer.renderPartialTicks);
        final Vec3 var5 = minecraft.thePlayer.getLook(minecraft.timer.renderPartialTicks);
        final Vec3 var6 = var4.addVector(var5.xCoord * 70.0, var5.yCoord * 70.0, var5.zCoord * 70.0);
        return minecraft.thePlayer.worldObj.rayTraceBlocks(var4, var6, false, false, true);
    }

    private Block getBlock(final int x, final int y, final int z) {
        return minecraft.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
    }

    private Block getBlock(final BlockPos pos) {
        return minecraft.theWorld.getBlockState(pos).getBlock();
    }

}
