package me.comu.client.module.impl.toggle.movement;

import me.comu.api.event.Listener;
import me.comu.api.minecraft.helper.PlayerHelper;
import me.comu.client.core.Gun;
import me.comu.client.events.BlockSoulSandSlowdownEvent;
import me.comu.client.events.ItemInUseEvent;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.module.impl.toggle.combat.AutoHeal;
import me.comu.client.properties.EnumProperty;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public final class NoSlow extends ToggleableModule
{
    public NoSlow()
    {
        super("NoSlow", new String[] {"noslow", "noslowdown", "ns"}, ModuleType.MOVEMENT);
        this.listeners.add(new Listener<MotionUpdateEvent>("no_slow_motion_update_listener")
        {
            @Override
            public void call(MotionUpdateEvent event)
            {
                AutoHeal autoHeal = (AutoHeal) Gun.getInstance().getModuleManager().getModuleByAlias("autoheal");
                EnumProperty<AutoHeal.Mode> mode = (EnumProperty<AutoHeal.Mode>) autoHeal.getPropertyByAlias("Mode");
                boolean isPotting = autoHeal.isPotting();

                if (minecraft.thePlayer.isBlocking() && !isPotting)
                {
                    if (PlayerHelper.isMoving())
                    {
                        switch (event.getTime())
                        {
                            case BEFORE:
                                minecraft.func_175102_a().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                                break;

                            case AFTER:
                                minecraft.func_175102_a().addToSendQueue(new C08PacketPlayerBlockPlacement(minecraft.thePlayer.getCurrentEquippedItem()));
                                break;
                        }
                    }
                    else
                    {
                        minecraft.func_175102_a().addToSendQueue(new C08PacketPlayerBlockPlacement(minecraft.thePlayer.getCurrentEquippedItem()));
                    }
                }
            }
        }
                          );
        this.listeners.add(new Listener<ItemInUseEvent>("no_slow_item_in_use_listener")
        {
            @Override
            public void call(ItemInUseEvent event)
            {
                if (!minecraft.thePlayer.isSneaking())
                {
                    event.setSpeed(1.0F);
                }
            }
        }
                          );
        this.listeners.add(new Listener<BlockSoulSandSlowdownEvent>("no_slow_block_soul_sand_slowdown_listener")
        {
            @Override
            public void call(BlockSoulSandSlowdownEvent event)
            {
                event.setCanceled(true);
            }
        }
                          );
        setRunning(true);
    }
}
