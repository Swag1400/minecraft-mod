package me.comu.client.events;

import me.comu.api.event.Event;
import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;

public class BlockRendererEvent extends Event
{
    private Block block;
    private BlockPos blockPos;

    public BlockRendererEvent(Block block, BlockPos blockPos)
    {
        this.block = block;
        this.blockPos = blockPos;
    }

    public Block getBlock()
    {
        return block;
    }

    public void setBlock(Block block)
    {
        this.block = block;
    }

    public BlockPos getBlockPos()
    {
        return blockPos;
    }

    public void setBlockPos(BlockPos blockPos)
    {
        this.blockPos = blockPos;
    }
}
