package me.comu.client.utils;

import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.events.MovePlayerEvent;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class ClientUtils
{
    public static FontRenderer clientFont;
    private static final Random RANDOM = new Random();

    public static void loadClientFont()
    {
        ClientUtils.clientFont = new FontRenderer(mc().gameSettings, new ResourceLocation("client/font/ascii.png"), mc().renderEngine, false);

        if (mc().gameSettings.language != null)
        {
            mc().fontRenderer.setUnicodeFlag(mc().isUnicode());
            mc().fontRenderer.setBidiFlag(mc().mcLanguageManager.isCurrentLanguageBidirectional());
        }

        mc().mcResourceManager.registerReloadListener(ClientUtils.clientFont);
    }

    public static Minecraft mc()
    {
        return Minecraft.getMinecraft();
    }

    public static EntityPlayerSP player()
    {
        return mc().thePlayer;
    }

    public static PlayerControllerMP playerController()
    {
        return mc().playerController;
    }
    public static boolean isOnGround(final double height) {
        return !Minecraft.getMinecraft().theWorld.getCollidingBoundingBoxes(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().thePlayer.getEntityBoundingBox().offset(0.0, -height, 0.0)).isEmpty();
    }
    public static WorldClient world()
    {
        return mc().theWorld;
    }

    public static List<Entity> loadedEntityList()
    {
        List<Entity> loadedList = new ArrayList(world().loadedEntityList);
        loadedList.remove(player());
        return loadedList;
    }
    public static Block getBlockAtPos(final BlockPos inBlockPos) {
        final IBlockState s = mc().theWorld.getBlockState(inBlockPos);
        return s.getBlock();
    }
    public static BlockPos getHypixelBlockpos(final String str) {
        int val = 89;
        if (str != null && str.length() > 1) {
            final char[] chs = str.toCharArray();
            for (int lenght = chs.length, i = 0; i < lenght; ++i) {
                val += chs[i] * str.length() * str.length() + str.charAt(0) + str.charAt(1);
            }
            val /= str.length();
        }
        return new BlockPos(val, -val % 255, val);
    }
    public static float angleDistance(final float par1, final float par2) {
        float angle = Math.abs(par1 - par2) % 360.0f;
        if (angle > 180.0f) {
            angle = 360.0f - angle;
        }
        return angle;
    }
    public static int randomNumber(final int max, final int min) {
        return Math.round(min + (float)Math.random() * (max - min));
    }

    public static int randomNum(int start, int end) {

        if (end < start) {
            int temp = end;
            end = start;
            start = temp;
        }

        return (int) Math.floor(Math.random() * (end - start + 1) + start);
    }

    public static boolean isBadPotion(final ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemPotion) {
            final ItemPotion potion = (ItemPotion)stack.getItem();
            if (ItemPotion.isSplash(stack.getItemDamage())) {
                for (final Object o : potion.getEffects(stack)) {
                    final PotionEffect effect = (PotionEffect)o;
                    if (effect.getPotionID() == Potion.poison.getId() || effect.getPotionID() == Potion.harm.getId() || effect.getPotionID() == Potion.moveSlowdown.getId() || effect.getPotionID() == Potion.weakness.getId()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static float getDamage(final ItemStack stack) {
        if (!(stack.getItem() instanceof ItemSword)) {
            return 0.0f;
        }
        return EnchantmentHelper.getEnchantmentLevel(Enchantment.SHARPNESS.effectId, stack) * 1.25f + ((ItemSword)stack.getItem()).func_150931_i();
    }

    public static boolean isFacingAtEntity(final Entity entity, double angleHowClose) {
        final Entity ent = mc().thePlayer;
        final float[] yawPitch = getYawAndPitch(entity);
        angleHowClose /= 4.5;
        final float yaw = yawPitch[0];
        final float pitch = yawPitch[1];
        return angleDistance(ent.rotationYaw, yaw) < angleHowClose && angleDistance(ent.rotationPitch, pitch) < angleHowClose;
    }

    public static boolean isTeam(final EntityPlayer e, final EntityPlayer e2) {
        if (e2.getTeam() != null && e.getTeam() != null) {
            final Character target = e2.getDisplayName().getFormattedText().charAt(1);
            final Character player = e.getDisplayName().getFormattedText().charAt(1);
            return target.equals(player);
        }
        return true;
    }


    public static float[] getBlockRotations(final double x, final double y, final double z) {
        final double var4 = x - mc().thePlayer.posX + 0.5;
        final double var5 = z - mc().thePlayer.posZ + 0.5;
        final double var6 = y - (mc().thePlayer.posY + mc().thePlayer.getEyeHeight() - 1.0);
        final double var7 = MathHelper.sqrt_double(var4 * var4 + var5 * var5);
        final float var8 = (float)(Math.atan2(var5, var4) * 180.0 / 3.141592653589793) - 90.0f;
        return new float[] { var8, (float)(-(Math.atan2(var6, var7) * 180.0 / 3.141592653589793)) };
    }


    public static float[] getYawAndPitch(final Entity target) {
        final Entity ent = mc().thePlayer;
        final double x = target.posX - ent.posX;
        final double z = target.posZ - ent.posZ;
        final double y = (target.getEntityBoundingBox().minY + target.getEntityBoundingBox().maxY) / 2.0 - mc().thePlayer.posY;
        final double helper = MathHelper.sqrt_double(x * x + z * z);
        final float newYaw = (float)(Math.atan2(z, x) * 180.0 / 3.141592653589793) - 90.0f;
        final float newPitch = (float)(Math.atan2(y * 1.0, helper) * 180.0 / 3.141592653589793);
        return new float[] { newYaw, newPitch };
    }

    public static int random(int min, int max) {
        return RANDOM.nextInt(max - min) + min;
    }
    public static float getDirection() {
        Minecraft mc = Minecraft.getMinecraft();
        float var1 = mc.thePlayer.rotationYaw;

        if (mc.thePlayer.moveForward < 0.0F) {
            var1 += 180.0F;
        }

        float forward = 1.0F;

        if (mc.thePlayer.moveForward < 0.0F) {
            forward = -0.5F;
        } else if (mc.thePlayer.moveForward > 0.0F) {
            forward = 0.5F;
        }

        if (mc.thePlayer.moveStrafing > 0.0F) {
            var1 -= 90.0F * forward;
        }

        if (mc.thePlayer.moveStrafing < 0.0F) {
            var1 += 90.0F * forward;
        }

        var1 *= 0.017453292F;
        return var1;
    }
    
    public static GameSettings gamesettings()
    {
        return mc().gameSettings;
    }

    public static MovementInput movementInput()
    {
        return player().movementInput;
    }

    public static double x()
    {
        return player().posX;
    }

    public static void x(double x)
    {
        player().posX = x;
    }

    public static double y()
    {
        return player().posY;
    }

    public static void y(double y)
    {
        player().posY = y;
    }

    public static double z()
    {
        return player().posZ;
    }

    public static void z(double z)
    {
        player().posZ = z;
    }

    public static float yaw()
    {
        return player().rotationYaw;
    }

    public static void yaw(float yaw)
    {
        player().rotationYaw = yaw;
    }

    public static float pitch()
    {
        return player().rotationPitch;
    }

    public static void pitch(float pitch)
    {
        player().rotationPitch = pitch;
    }

    public static void packet(Packet packet)
    {
        mc().func_175102_a().addToSendQueue(packet);
    }

    public static void sendMessage(String message)
    {
    }

    public static Block getBlock(final BlockPos pos) {
        return Helper.world().getBlockState(pos).getBlock();
    }

    public static Block getBlock(final int x, final int y, final int z) {
        return Helper.world().getBlockState(new BlockPos(x, y, z)).getBlock();
    }
    public static void startDrawing() {
        GL11.glEnable(3042);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        Helper.mc().entityRenderer.setupCameraTransform(Helper.mc().getTimer().renderPartialTicks, 0);
    }

    public static void stopDrawing() {
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glDisable(2848);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
    }

    public static void setMoveSpeed(MotionUpdateEvent event, double speed)
    {
        double forward = movementInput().moveForward;
        double strafe = movementInput().moveStrafe;
        float yaw = yaw();

        if ((forward == 0.0D) && (strafe == 0.0D))
        {
            event.setPositionX(0.0D);
            event.setPositionZ(0.0D);
        }
        else
        {
            if (forward != 0.0D)
            {
                if (strafe > 0.0D)
                {
                    yaw += (forward > 0.0D ? -45 : 45);
                }
                else if (strafe < 0.0D)
                {
                    yaw += (forward > 0.0D ? 45 : -45);
                }

                strafe = 0.0D;

                if (forward > 0.0D)
                {
                    forward = 1.0D;
                }
                else if (forward < 0.0D)
                {
                    forward = -1.0D;
                }
            }

            event.setPositionX(forward * speed * Math.cos(Math.toRadians(yaw + 90.0F)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0F)));
            event.setPositionZ(forward * speed * Math.sin(Math.toRadians(yaw + 90.0F)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0F)));
        }
    }
    public static void setMoveSpeed(final MovePlayerEvent event, final double speed) {
        double forward = movementInput().moveForward;
        double strafe = movementInput().moveStrafe;
        float yaw = yaw();
        if (forward == 0.0 && strafe == 0.0) {
            event.setMotionX(0.0);
            event.setMotionZ(0.0);
        }
        else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += ((forward > 0.0) ? -45 : 45);
                }
                else if (strafe < 0.0) {
                    yaw += ((forward > 0.0) ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                }
                else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            event.setMotionX(forward * speed * Math.cos(Math.toRadians(yaw + 90.0f)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0f)));
            event.setMotionZ(forward * speed * Math.sin(Math.toRadians(yaw + 90.0f)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0f)));
        }
    }
    public static void drawSmallString(final String s, final int x, final int y, final int color) {
        GL11.glPushMatrix();
        GL11.glScalef(0.5f, 0.5f, 2f);
        Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(s, (float)(x * 2), (float)(y * 2), color);
        GL11.glPopMatrix();
    }

    public static void drawOutlinedBox(final AxisAlignedBB box) {
        if (box == null) {
            return;
        }
        Helper.mc().entityRenderer.setupCameraTransform(Helper.mc().getTimer().renderPartialTicks, 0);
        GL11.glBegin(3);
        GL11.glVertex3d(box.minX, box.minY, box.minZ);
        GL11.glVertex3d(box.maxX, box.minY, box.minZ);
        GL11.glVertex3d(box.maxX, box.minY, box.maxZ);
        GL11.glVertex3d(box.minX, box.minY, box.maxZ);
        GL11.glVertex3d(box.minX, box.minY, box.minZ);
        GL11.glEnd();
        GL11.glBegin(3);
        GL11.glVertex3d(box.minX, box.maxY, box.minZ);
        GL11.glVertex3d(box.maxX, box.maxY, box.minZ);
        GL11.glVertex3d(box.maxX, box.maxY, box.maxZ);
        GL11.glVertex3d(box.minX, box.maxY, box.maxZ);
        GL11.glVertex3d(box.minX, box.maxY, box.minZ);
        GL11.glEnd();
        GL11.glBegin(1);
        GL11.glVertex3d(box.minX, box.minY, box.minZ);
        GL11.glVertex3d(box.minX, box.maxY, box.minZ);
        GL11.glVertex3d(box.maxX, box.minY, box.minZ);
        GL11.glVertex3d(box.maxX, box.maxY, box.minZ);
        GL11.glVertex3d(box.maxX, box.minY, box.maxZ);
        GL11.glVertex3d(box.maxX, box.maxY, box.maxZ);
        GL11.glVertex3d(box.minX, box.minY, box.maxZ);
        GL11.glVertex3d(box.minX, box.maxY, box.maxZ);
        GL11.glEnd();
    }


    public static float[] getRotationFromPosition(final double x, final double z, final double y) {
        final double xDiff = x - Minecraft.getMinecraft().thePlayer.posX;
        final double zDiff = z - Minecraft.getMinecraft().thePlayer.posZ;
        final double yDiff = y - Minecraft.getMinecraft().thePlayer.posY - 0.6;
        final double dist = MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff);
        final float yaw = (float)(Math.atan2(zDiff, xDiff) * 180.0 / 3.141592653589793) - 90.0f;
        final float pitch = (float)(-(Math.atan2(yDiff, dist) * 180.0 / 3.141592653589793));
        return new float[] { yaw, pitch };
    }
    public static void drawBoundingBox(final AxisAlignedBB aabb) {
        final WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();
        final Tessellator tessellator = Tessellator.getInstance();
        Helper.mc().entityRenderer.setupCameraTransform(Helper.mc().getTimer().renderPartialTicks, 0);
        worldRenderer.startDrawingQuads();
        worldRenderer.addVertex(aabb.minX, aabb.minY, aabb.minZ);
        worldRenderer.addVertex(aabb.minX, aabb.maxY, aabb.minZ);
        worldRenderer.addVertex(aabb.maxX, aabb.minY, aabb.minZ);
        worldRenderer.addVertex(aabb.maxX, aabb.maxY, aabb.minZ);
        worldRenderer.addVertex(aabb.maxX, aabb.minY, aabb.maxZ);
        worldRenderer.addVertex(aabb.maxX, aabb.maxY, aabb.maxZ);
        worldRenderer.addVertex(aabb.minX, aabb.minY, aabb.maxZ);
        worldRenderer.addVertex(aabb.minX, aabb.maxY, aabb.maxZ);
        tessellator.draw();
        worldRenderer.startDrawingQuads();
        worldRenderer.addVertex(aabb.maxX, aabb.maxY, aabb.minZ);
        worldRenderer.addVertex(aabb.maxX, aabb.minY, aabb.minZ);
        worldRenderer.addVertex(aabb.minX, aabb.maxY, aabb.minZ);
        worldRenderer.addVertex(aabb.minX, aabb.minY, aabb.minZ);
        worldRenderer.addVertex(aabb.minX, aabb.maxY, aabb.maxZ);
        worldRenderer.addVertex(aabb.minX, aabb.minY, aabb.maxZ);
        worldRenderer.addVertex(aabb.maxX, aabb.maxY, aabb.maxZ);
        worldRenderer.addVertex(aabb.maxX, aabb.minY, aabb.maxZ);
        tessellator.draw();
        worldRenderer.startDrawingQuads();
        worldRenderer.addVertex(aabb.minX, aabb.maxY, aabb.minZ);
        worldRenderer.addVertex(aabb.maxX, aabb.maxY, aabb.minZ);
        worldRenderer.addVertex(aabb.maxX, aabb.maxY, aabb.maxZ);
        worldRenderer.addVertex(aabb.minX, aabb.maxY, aabb.maxZ);
        worldRenderer.addVertex(aabb.minX, aabb.maxY, aabb.minZ);
        worldRenderer.addVertex(aabb.minX, aabb.maxY, aabb.maxZ);
        worldRenderer.addVertex(aabb.maxX, aabb.maxY, aabb.maxZ);
        worldRenderer.addVertex(aabb.maxX, aabb.maxY, aabb.minZ);
        tessellator.draw();
        worldRenderer.startDrawingQuads();
        worldRenderer.addVertex(aabb.minX, aabb.minY, aabb.minZ);
        worldRenderer.addVertex(aabb.maxX, aabb.minY, aabb.minZ);
        worldRenderer.addVertex(aabb.maxX, aabb.minY, aabb.maxZ);
        worldRenderer.addVertex(aabb.minX, aabb.minY, aabb.maxZ);
        worldRenderer.addVertex(aabb.minX, aabb.minY, aabb.minZ);
        worldRenderer.addVertex(aabb.minX, aabb.minY, aabb.maxZ);
        worldRenderer.addVertex(aabb.maxX, aabb.minY, aabb.maxZ);
        worldRenderer.addVertex(aabb.maxX, aabb.minY, aabb.minZ);
        tessellator.draw();
        worldRenderer.startDrawingQuads();
        worldRenderer.addVertex(aabb.minX, aabb.minY, aabb.minZ);
        worldRenderer.addVertex(aabb.minX, aabb.maxY, aabb.minZ);
        worldRenderer.addVertex(aabb.minX, aabb.minY, aabb.maxZ);
        worldRenderer.addVertex(aabb.minX, aabb.maxY, aabb.maxZ);
        worldRenderer.addVertex(aabb.maxX, aabb.minY, aabb.maxZ);
        worldRenderer.addVertex(aabb.maxX, aabb.maxY, aabb.maxZ);
        worldRenderer.addVertex(aabb.maxX, aabb.minY, aabb.minZ);
        worldRenderer.addVertex(aabb.maxX, aabb.maxY, aabb.minZ);
        tessellator.draw();
        worldRenderer.startDrawingQuads();
        worldRenderer.addVertex(aabb.minX, aabb.maxY, aabb.maxZ);
        worldRenderer.addVertex(aabb.minX, aabb.minY, aabb.maxZ);
        worldRenderer.addVertex(aabb.minX, aabb.maxY, aabb.minZ);
        worldRenderer.addVertex(aabb.minX, aabb.minY, aabb.minZ);
        worldRenderer.addVertex(aabb.maxX, aabb.maxY, aabb.minZ);
        worldRenderer.addVertex(aabb.maxX, aabb.minY, aabb.minZ);
        worldRenderer.addVertex(aabb.maxX, aabb.maxY, aabb.maxZ);
        worldRenderer.addVertex(aabb.maxX, aabb.minY, aabb.maxZ);
        tessellator.draw();
    }

    public static void offsetPosition(double speed)
    {
        double forward = movementInput().moveForward;
        double strafe = movementInput().moveStrafe;
        float yaw = yaw();

        if ((forward == 0.0D) && (strafe == 0.0D))
        {
            return;
        }

        if (forward != 0.0D)
        {
            if (strafe > 0.0D)
            {
                yaw += (forward > 0.0D ? -45 : 45);
            }
            else if (strafe < 0.0D)
            {
                yaw += (forward > 0.0D ? 45 : -45);
            }

            strafe = 0.0D;

            if (forward > 0.0D)
            {
                forward = 1.0D;
            }
            else if (forward < 0.0D)
            {
                forward = -1.0D;
            }
        }

        player().setPositionAndUpdate(x() + (forward * speed * Math.cos(Math.toRadians(yaw + 90.0F)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0F))), y(), z() + (forward * speed * Math.sin(Math.toRadians(yaw + 90.0F)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0F))));
    }
//    public static Entity rayCast(double range, float yaw, float pitch) {
//        double d0 = range;
//        double d1 = d0;
//        Vec3 vec3 = mc().thePlayer.getEyeHeight(new Vec3(mc().thePlayer.getPositionVector().xCoord, mc().thePlayer.getPositionVector().yCoord, mc().thePlayer.getPositionVector().zCoord);
//        boolean flag = false;
//        boolean flag1 = true;
//
//        if (d0 > 3.0D)
//        {
//            flag = true;
//        }
//
//        /*if (this.mc.objectMouseOver != null)
//        {
//            d1 = this.mc.objectMouseOver.hitVec.distanceTo(vec3);
//        }*/
//
//        Vec3 vec31 = getVectorForRotation(pitch, yaw);
//        Vec3 vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0);
//
//        Entity pointedEntity = null;
//
//        Vec3 vec33 = null;
//        float f = 1.0F;
//        List list = mc().theWorld.getEntitiesWithinAABBExcludingEntity(mc().getRenderViewEntity(), mc().getRenderViewEntity().getEntityBoundingBox().addCoord(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0).expand((double)f, (double)f, (double)f), Predicates.and(EntitySelectors.NOT_SPECTATING, Entity::canBeCollidedWith));
//        double d2 = d1;
//
//        for (int i = 0; i < list.size(); ++i)
//        {
//            Entity entity1 = (Entity)list.get(i);
//            float f1 = entity1.getCollisionBorderSize();
//            AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand((double)f1, (double)f1, (double)f1);
//            MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);
//
//            if (axisalignedbb.isVecInside(vec3))
//            {
//                if (d2 >= 0.0D)
//                {
//                    pointedEntity = entity1;
//                    vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
//                    d2 = 0.0D;
//                }
//            }
//            else if (movingobjectposition != null)
//            {
//                double d3 = vec3.distanceTo(movingobjectposition.hitVec);
//
//                if (d3 < d2 || d2 == 0.0D)
//                {
//                    boolean flag2 = false;
//
//                    if (Reflector.ForgeEntity_canRiderInteract.exists())
//                    {
//                        flag2 = Reflector.callBoolean(entity1, Reflector.ForgeEntity_canRiderInteract, new Object[0]);
//                    }
//
//                    if (entity1 == mc().getRenderViewEntity().ridingEntity && !flag2)
//                    {
//                        if (d2 == 0.0D)
//                        {
//                            pointedEntity = entity1;
//                            vec33 = movingobjectposition.hitVec;
//                        }
//                    }
//                    else
//                    {
//                        pointedEntity = entity1;
//                        vec33 = movingobjectposition.hitVec;
//                        d2 = d3;
//                    }
//                }
//            }
//        }
//
//        return pointedEntity;
//    }
//    public EntityPlayer getClosestPlayer(double x, double y, double z, double distance)
//    {
//        double d0 = -1.0D;
//        EntityPlayer entityplayer = null;
//
//        for (int i = 0; i < this.playerEntities.size(); ++i)
//        {
//            EntityPlayer entityplayer1 = (EntityPlayer)this.playerEntities.get(i);
//
//            if (EntitySelectors.NOT_SPECTATING.apply(entityplayer1))
//            {
//                double d1 = entityplayer1.getDistanceSq(x, y, z);
//
//                if ((distance < 0.0D || d1 < distance * distance) && (d0 == -1.0D || d1 < d0))
//                {
//                    d0 = d1;
//                    entityplayer = entityplayer1;
//                }
//            }
//        }
//
//        return entityplayer;
//    }
}
