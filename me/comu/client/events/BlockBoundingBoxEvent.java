package me.comu.client.events;

import me.comu.api.event.Event;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;

public class BlockBoundingBoxEvent extends Event
{
    private IBlockState state;
    private Block block;
    private AxisAlignedBB boundingBox;
    private BlockPos blockPos;

    public BlockBoundingBoxEvent(Block block, AxisAlignedBB boundingBox, BlockPos blockPos, IBlockState state)
    {
        this.block = block;
        this.boundingBox = boundingBox;
        this.blockPos = blockPos;
        this.state = state;
    }

    public BlockBoundingBoxEvent(AxisAlignedBB var7, Block block, int x, int y, int z)
    {
        super();
    }

    public BlockPos getBlockPos()
    {
        return blockPos;
    }

    public void setBlockPos(BlockPos blockPos)
    {
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

    public AxisAlignedBB getBoundingBox()
    {
        return boundingBox;
    }

    public void setBoundingBox(AxisAlignedBB boundingBox)
    {
        this.boundingBox = boundingBox;
    }

    public IBlockState getState()
    {
        return state;
    }
}
