package me.comu.api.minecraft.helper;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;


public final class WorldHelper
{
    private static Minecraft minecraft = Minecraft.getMinecraft();

    public static BlockPos getSpawnPoint()
    {
        return minecraft.theWorld.getSpawnPoint();
    }

    public static Block getBlock(double x, double y, double z)
    {
        return minecraft.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
    }
}
