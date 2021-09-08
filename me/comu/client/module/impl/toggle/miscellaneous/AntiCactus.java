package me.comu.client.module.impl.toggle.miscellaneous;

import me.comu.api.event.Listener;
import me.comu.client.events.BlockBoundingBoxEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import net.minecraft.block.BlockCactus;
import net.minecraft.util.AxisAlignedBB;

public final class AntiCactus extends ToggleableModule
{
    public AntiCactus()
    {
        super("AntiCactus", new String[] {"anticactus", "anticac", "cactus"}, 0xFFC690D4, ModuleType.MISCELLANEOUS);
        this.listeners.add(new Listener<BlockBoundingBoxEvent>("anti_cactus_block_bounding_event_listener")
        {
            @Override
            public void call(BlockBoundingBoxEvent event)
            {
                if (event.getBlock() instanceof BlockCactus)
                {
                    event.setBoundingBox(new AxisAlignedBB(event.getBlockPos().getX(), event.getBlockPos().getY(), event.getBlockPos().getZ(), event.getBlockPos().getX() + 1, event.getBoundingBox().maxY, event.getBlockPos().getZ() + 1));
                }
            }
        });
    }
}
