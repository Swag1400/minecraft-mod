package me.comu.client.module.impl.toggle.world;

import me.comu.api.event.Listener;
import me.comu.client.events.BlockBoundingBoxEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.Property;
import net.minecraft.block.BlockCactus;
import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;

public final class Avoid extends ToggleableModule
{
    private final Property<Boolean> fire = new Property<>(true, "Fire", "f"), cactus = new Property<>(true, "Cactus", "c");

    public Avoid()
    {
        super("Avoid", new String[] {"avoid"}, 0xFF5ED7FF, ModuleType.WORLD);
        this.offerProperties(fire, cactus);
        this.listeners.add(new Listener<BlockBoundingBoxEvent>("avoid_block_bounding_box_listener")
        {
            @Override
            public void call(BlockBoundingBoxEvent event)
            {
                if (!minecraft.gameSettings.keyBindJump.getIsKeyPressed() && minecraft.thePlayer.onGround)
                {
                    if ((event.getBlock().getMaterial().equals(Material.fire) && fire.getValue()) || (event.getBlock() instanceof BlockCactus && cactus.getValue()))
                    {
                        event.setBoundingBox(AxisAlignedBB.fromBounds(event.getBlockPos().getX(), event.getBlockPos().getY(), event.getBlockPos().getZ(), event.getBlockPos().getX() + 1D, event.getBlockPos().getY() + 1D, event.getBlockPos().getZ() + 1D));
                    }
                }
            }
        });
    }
}
