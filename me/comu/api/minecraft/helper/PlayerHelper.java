package me.comu.api.minecraft.helper;

import me.comu.client.utils.Helper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

import java.util.Arrays;
import java.util.List;


public final class PlayerHelper
{
    private static Minecraft minecraft = Minecraft.getMinecraft();
    private static List<Block> blacklistedBlocks;

    public static Block getBlockBelowPlayer(double height)
    {
        return WorldHelper.getBlock(minecraft.thePlayer.posX, minecraft.thePlayer.posY - height, minecraft.thePlayer.posZ);
    }

    public static Block getBlockAbovePlayer(double height)
    {
        return WorldHelper.getBlock(minecraft.thePlayer.posX, minecraft.thePlayer.posY + height, minecraft.thePlayer.posZ);
    }
    public static Block getBlock(final int x, final int y, final int z) {
        return Helper.world().getBlockState(new BlockPos(x, y, z)).getBlock();
    }
    public static boolean isOnGround(double height) {
        if (!minecraft.theWorld.getCollidingBoundingBoxes(minecraft.thePlayer, minecraft.thePlayer.getEntityBoundingBox().offset(0.0D, -height, 0.0D)).isEmpty()) {
            return true;
        } else {
            return false;
        }
    }
    public static int getSpeedEffect() {
        if (minecraft.thePlayer.isPotionActive(Potion.moveSpeed))
            return minecraft.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1;
        else
            return 0;
    }
    public static boolean isInLiquid()
    {
        if (minecraft.thePlayer == null)
        {
            return false;
        }

        boolean inLiquid = false;
        int y = (int) minecraft.thePlayer.getEntityBoundingBox().minY;

        for (int x = MathHelper.floor_double(minecraft.thePlayer.getEntityBoundingBox().minX); x < MathHelper.floor_double(minecraft.thePlayer.getEntityBoundingBox().maxX) + 1; x++)
        {
            for (int z = MathHelper.floor_double(minecraft.thePlayer.getEntityBoundingBox().minZ); z < MathHelper.floor_double(minecraft.thePlayer.getEntityBoundingBox().maxZ) + 1; z++)
            {
                Block block = minecraft.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();

                if (block != null && !(block instanceof BlockAir))
                {
                    if (!(block instanceof BlockLiquid))
                    {
                        return false;
                    }

                    if ((block instanceof BlockLiquid))
                    {
                        return true;
                    }

                    inLiquid = true;
                }
            }
        }

        return inLiquid;
    }

    public static boolean isInLiquid(double offset)
    {
        return getBlockBelowPlayer(-offset) instanceof BlockLiquid;
    }
    public static boolean isOnLiquid(double profondeur)
    {
        boolean onLiquid = false;

        if(minecraft.theWorld.getBlockState(new BlockPos(minecraft.thePlayer.posX, minecraft.thePlayer.posY - profondeur, minecraft.thePlayer.posZ)).getBlock().getMaterial().isLiquid()) {
            onLiquid = true;
        }
        return onLiquid;
    }
    public static boolean isTotalOnLiquid(double profondeur)
    {
        for(double x = minecraft.thePlayer.boundingBox.minX; x < minecraft.thePlayer.boundingBox.maxX; x +=0.01f){

            for(double z = minecraft.thePlayer.boundingBox.minZ; z < minecraft.thePlayer.boundingBox.maxZ; z +=0.01f){
                Block block = minecraft.theWorld.getBlockState(new BlockPos(x, minecraft.thePlayer.posY - profondeur,z)).getBlock();
                if(!(block instanceof BlockLiquid) && !(block instanceof BlockAir)){
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isOnLiquid()
    {
        AxisAlignedBB boundingBox = minecraft.thePlayer.getEntityBoundingBox();
        boundingBox = boundingBox.contract(0.00D, 0.0D, 0.00D).offset(0.0D, -0.02D, 0.0D);
        boolean onLiquid = false;
        int y = (int) boundingBox.minY;

        for (int x = MathHelper.floor_double(boundingBox.minX); x < MathHelper.floor_double(boundingBox.maxX + 1.0D); x++)
        {
            for (int z = MathHelper.floor_double(boundingBox.minZ); z < MathHelper.floor_double(boundingBox.maxZ + 1.0D); z++)
            {
                Block block = WorldHelper.getBlock(x, y, z);

                if (block != Blocks.air)
                {
                    if (!(block instanceof BlockLiquid))
                    {
                        return false;
                    }

                    onLiquid = true;
                }
            }
        }

        return onLiquid;
    }
    
	/*public static boolean isOnLiquid() {
		boolean onLiquid = false;
		if (getBlockAtPosC(Helper.player(), 0.3F, 0.1F, 0.3F).getMaterial().isLiquid() &&
				getBlockAtPosC(Helper.player(), -0.3F, 0.1F, -0.3F).getMaterial().isLiquid()){
			onLiquid = true;
		}
		return onLiquid;
	}
	*/
    public static void blinkToPos(final double[] startPos, final BlockPos endPos, final double slack, final double[] pOffset) {
        double curX = startPos[0];
        double curY = startPos[1];
        double curZ = startPos[2];
        final double endX = endPos.getX() + 0.5;
        final double endY = endPos.getY() + 1.0;
        final double endZ = endPos.getZ() + 0.5;
        double distance = Math.abs(curX - endX) + Math.abs(curY - endY) + Math.abs(curZ - endZ);
        int count = 0;
        while (distance > slack) {
            distance = Math.abs(curX - endX) + Math.abs(curY - endY) + Math.abs(curZ - endZ);
            if (count > 120) {
                break;
            }
            final boolean next = false;
            final double diffX = curX - endX;
            final double diffY = curY - endY;
            final double diffZ = curZ - endZ;
            final double offset = ((count & 0x1) == 0x0) ? pOffset[0] : pOffset[1];
            if (diffX < 0.0) {
                if (Math.abs(diffX) > offset) {
                    curX += offset;
                }
                else {
                    curX += Math.abs(diffX);
                }
            }
            if (diffX > 0.0) {
                if (Math.abs(diffX) > offset) {
                    curX -= offset;
                }
                else {
                    curX -= Math.abs(diffX);
                }
            }
            if (diffY < 0.0) {
                if (Math.abs(diffY) > 0.25) {
                    curY += 0.25;
                }
                else {
                    curY += Math.abs(diffY);
                }
            }
            if (diffY > 0.0) {
                if (Math.abs(diffY) > 0.25) {
                    curY -= 0.25;
                }
                else {
                    curY -= Math.abs(diffY);
                }
            }
            if (diffZ < 0.0) {
                if (Math.abs(diffZ) > offset) {
                    curZ += offset;
                }
                else {
                    curZ += Math.abs(diffZ);
                }
            }
            if (diffZ > 0.0) {
                if (Math.abs(diffZ) > offset) {
                    curZ -= offset;
                }
                else {
                    curZ -= Math.abs(diffZ);
                }
            }
            Minecraft.getMinecraft().func_175102_a().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(curX, curY, curZ, true));
            ++count;
        }
    }
	public static Block getBlockAtPosC(EntityPlayer inPlayer, double x, double y, double z) {
		return getBlock(new BlockPos(inPlayer.posX - x, inPlayer.posY - y, inPlayer.posZ - z));
	}
    public static Block getBlock(BlockPos pos) {
        return Helper.world().getBlockState(pos).getBlock();
    
    }
    public static int getBestToolForBlock(final BlockPos pos) {
        final Block block = minecraft.theWorld.getBlockState(pos).getBlock();
        int slot = 0;
        float damage = 0.1f;
        for (int index = 36; index < 45; ++index) {
            final ItemStack itemStack = minecraft.thePlayer.inventoryContainer.getSlot(index).getStack();
            if (itemStack != null && block != null && itemStack.getItem().getStrVsBlock(itemStack, block) > damage) {
                slot = index - 36;
                damage = itemStack.getItem().getStrVsBlock(itemStack, block);
            }
        }
        if (damage > 0.1f) {
            return slot;
        }
        return minecraft.thePlayer.inventory.currentItem;
    }

    public static boolean isInsideBlock()
    {
        for (int x = MathHelper.floor_double(minecraft.thePlayer.getEntityBoundingBox().minX); x < MathHelper.floor_double(minecraft.thePlayer.getEntityBoundingBox().maxX) + 1; x++)
        {
            for (int y = MathHelper.floor_double(minecraft.thePlayer.getEntityBoundingBox().minY); y < MathHelper.floor_double(minecraft.thePlayer.getEntityBoundingBox().maxY) + 1; y++)
            {
                for (int z = MathHelper.floor_double(minecraft.thePlayer.getEntityBoundingBox().minZ); z < MathHelper.floor_double(minecraft.thePlayer.getEntityBoundingBox().maxZ) + 1; z++)
                {
                    Block block = WorldHelper.getBlock(x, y, z);

                    if (block == null || block instanceof BlockAir)
                    {
                        continue;
                    }

                    if (block instanceof BlockTallGrass)
                    {
                        return false;
                    }

                    AxisAlignedBB boundingBox = block.getSelectedBoundingBox(Minecraft.getMinecraft().theWorld, new BlockPos(x, y, z));

                    if (boundingBox != null && minecraft.thePlayer.getEntityBoundingBox().intersectsWith(boundingBox))
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static boolean isAiming(float yaw, float pitch, int fov)
    {
        yaw = wrapAngleTo180(yaw);
        pitch = wrapAngleTo180(pitch);
        float curYaw = wrapAngleTo180(minecraft.thePlayer.rotationYaw);
        float curPitch = wrapAngleTo180(minecraft.thePlayer.rotationPitch);
        float yawDiff = Math.abs(yaw - curYaw);
        float pitchDiff = Math.abs(pitch - curPitch);
        return yawDiff + pitchDiff <= fov;
    }
    public static boolean isUnderBlock() {
        if (Helper.player() == null) {
            return false;
        }
        final int y = (int)Helper.player().playerLocation.add(0, 2, 0).getY();
        for (int x = MathHelper.floor_double(Helper.player().boundingBox.minX); x < MathHelper.floor_double(Helper.player().boundingBox.maxX) + 1; ++x) {
            for (int z = MathHelper.floor_double(Helper.player().boundingBox.minZ); z < MathHelper.floor_double(Helper.player().boundingBox.maxZ) + 1; ++z) {
                final Block block = Helper.world().getBlockState(new BlockPos(x, y, z)).getBlock();
                if (block != null && !(block instanceof BlockAir) && block.isCollidable()) {
                    return true;
                }
            }
        }
        return false;
    }
    

    public static float getFOV(float[] rotations)
    {
        float yaw = rotations[0];
        float pitch = rotations[1];
        yaw = wrapAngleTo180(yaw);
        pitch = wrapAngleTo180(pitch);
        float curYaw = wrapAngleTo180(minecraft.thePlayer.rotationYaw);
        float curPitch = wrapAngleTo180(minecraft.thePlayer.rotationPitch);
        float yawDiff = Math.abs(yaw - curYaw);
        float pitchDiff = Math.abs(pitch - curPitch);
        return yawDiff + pitchDiff;
    }

    public static float wrapAngleTo180(float angle)
    {
        angle %= 360f;

        if (angle >= 180f)
        {
            angle -= 360f;
        }

        if (angle < -180f)
        {
            angle += 360f;
        }

        return angle;
    }

    public static boolean isMoving()
    {
        return (minecraft.thePlayer.moveForward != 0D || minecraft.thePlayer.moveStrafing != 0D);
    }

    public static boolean isPressingMoveKeybinds()
    {
        return (minecraft.gameSettings.keyBindForward.getIsKeyPressed() || minecraft.gameSettings.keyBindBack.getIsKeyPressed() || minecraft.gameSettings.keyBindLeft.getIsKeyPressed() || minecraft.gameSettings.keyBindRight.getIsKeyPressed());
    }

    public static void damagePlayer()
    {
        for (int index = 0; index < 81; index++)
        {
            minecraft.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY + 0.05D, minecraft.thePlayer.posZ, false));
            minecraft.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(minecraft.thePlayer.posX, minecraft.thePlayer.posY, minecraft.thePlayer.posZ, false));
        }
    }

    public static void drownPlayer()
    {
        for (int index = 0; index < 500; index++)
        {
            minecraft.func_175102_a().addToSendQueue(new C03PacketPlayer());
            minecraft.func_175102_a().addToSendQueue(new C03PacketPlayer());
            minecraft.func_175102_a().addToSendQueue(new C03PacketPlayer());
            minecraft.func_175102_a().addToSendQueue(new C03PacketPlayer());
            minecraft.func_175102_a().addToSendQueue(new C03PacketPlayer());
        }
    }
    public static List<Block> getBlacklistedBlocks() {
        return blacklistedBlocks;
    }

    static {
        minecraft= Minecraft.getMinecraft();
        blacklistedBlocks = Arrays.asList(Blocks.air, Blocks.water, Blocks.flowing_water, Blocks.lava, Blocks.flowing_lava, Blocks.enchanting_table, Blocks.carpet, Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.snow_layer, Blocks.ice, Blocks.packed_ice, Blocks.coal_ore, Blocks.diamond_ore, Blocks.emerald_ore, Blocks.chest, Blocks.torch, Blocks.anvil, Blocks.trapped_chest, Blocks.noteblock, Blocks.jukebox, Blocks.tnt, Blocks.gold_ore, Blocks.iron_ore, Blocks.lapis_ore, Blocks.lit_redstone_ore, Blocks.quartz_ore, Blocks.redstone_ore, Blocks.wooden_pressure_plate, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.heavy_weighted_pressure_plate, Blocks.stone_button, Blocks.wooden_button, Blocks.lever);
    }
    public static String getFacingWithProperCapitals()
    {
        String directionLabel = minecraft.getRenderViewEntity().getDirectionFacing().getName();

        switch (directionLabel)
        {
            case "north":
                directionLabel = "North";
                break;

            case "south":
                directionLabel = "South";
                break;

            case "west":
                directionLabel = "West";
                break;

            case "east":
                directionLabel = "East";
                break;
        }

        return directionLabel;
    }
    public static void setMotion(double speed) {
        double forward = minecraft.thePlayer.movementInput.moveForward;
        double strafe = minecraft.thePlayer.movementInput.moveStrafe;
        float yaw = minecraft.thePlayer.rotationYaw;
        if ((forward == 0.0D) && (strafe == 0.0D)) {
            minecraft.thePlayer.motionX = 0;
            minecraft.thePlayer.motionZ = 0;
        } else {
            if (forward != 0.0D) {
                if (strafe > 0.0D) {
                    yaw += (forward > 0.0D ? -45 : 45);
                } else if (strafe < 0.0D) {
                    yaw += (forward > 0.0D ? 45 : -45);
                }
                strafe = 0.0D;
                if (forward > 0.0D) {
                    forward = 1;
                } else if (forward < 0.0D) {
                    forward = -1;
                }
            }
            minecraft.thePlayer.motionX = forward * speed * Math.cos(Math.toRadians(yaw + 90.0F)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0F));
            minecraft.thePlayer.motionZ = forward * speed * Math.sin(Math.toRadians(yaw + 90.0F)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0F));
        }
    }
    public enum EnumHand
    {
        MAIN_HAND("MAIN_HAND", 0), 
        OFF_HAND("OFF_HAND", 1);
        
        private EnumHand(final String s, final int n) {
        }
    }

}
