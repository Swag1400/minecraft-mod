package me.comu.client.module.impl.toggle.movement;

import me.comu.api.event.Listener;
import me.comu.api.minecraft.helper.PlayerHelper;
import me.comu.client.events.BlockBoundingBoxEvent;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.events.PacketEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import net.minecraft.block.BlockLiquid;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;

public final class Jesus extends ToggleableModule
{
    private boolean nextTick = false;
    private float offset = 0.02F;

    public Jesus()
    {
        super("Jesus", new String[] {"jesus", "watermark"}, 0xFF88DDEB, ModuleType.MOVEMENT);
        this.listeners.add(new Listener<MotionUpdateEvent>("jesus_motion_update_listener")
        {
            @Override
            public void call(MotionUpdateEvent event)
            {
                if (event.getTime() == MotionUpdateEvent.Time.BEFORE)
                {
                    if (PlayerHelper.isInLiquid() && !event.isSneaking() && !minecraft.thePlayer.isCollidedVertically)
                    {
                        minecraft.thePlayer.motionY = 0.085D;
                    }
                }
            }
        });
        this.listeners.add(new Listener<BlockBoundingBoxEvent>("jesus_block_bounding_box_listener")
        {
            @Override
            public void call(BlockBoundingBoxEvent event)
            {
                if (event.getState() != null && event.getState().getBlock() instanceof BlockLiquid)
                {
                    if (!minecraft.thePlayer.isSneaking() && minecraft.thePlayer.fallDistance <= 3.0F && event.getBlockPos().getY() < minecraft.thePlayer.posY - offset)
                    {
                        event.setBoundingBox(new AxisAlignedBB(event.getBlockPos().getX(), event.getBlockPos().getY(), event.getBlockPos().getZ(), event.getBlockPos().getX() + 1.0F - offset, event.getBlockPos().getY() + 1.0F, event.getBlockPos().getZ() + 1.0F));
                    }
                }
            }
        });
        this.listeners.add(new Listener<PacketEvent>("jesus_packet_listener")
        {
            @Override
            public void call(PacketEvent event)
            {
                if (event.getPacket() instanceof C03PacketPlayer)
                {
                    C03PacketPlayer player = (C03PacketPlayer) event.getPacket();

                    if (PlayerHelper.isOnLiquid() && !PlayerHelper.isInLiquid())
                    {
                        if (minecraft.thePlayer.ticksExisted % 2 == 0)
                        {
                            player.setPositionY(player.getPositionY() - 0.01F);
                        }
                    }
                }
            }
        });
    }


    @Override
    protected void onDisable()
    {
        super.onDisable();
        nextTick = false;
    }
}
