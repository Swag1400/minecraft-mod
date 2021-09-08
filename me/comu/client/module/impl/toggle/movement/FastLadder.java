package me.comu.client.module.impl.toggle.movement;

import me.comu.api.event.Listener;
import me.comu.client.events.MovePlayerEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.NumberProperty;
import me.comu.client.properties.Property;

public final class FastLadder extends ToggleableModule
{
    private final NumberProperty<Double> speed = new NumberProperty<>(1.9D, 1.1D, 10D, 0.1D, "Speed", "s");
    private final Property<Boolean> stop = new Property<>(true, "Stop", "s");

    public FastLadder()
    {
        super("FastLadder", new String[] {"fastladder", "ladder", "ladders", "fl"}, 0xFFD1A74D, ModuleType.MOVEMENT);
        this.offerProperties(stop);
        this.listeners.add(new Listener<MovePlayerEvent>("fast_ladder_move_player_listener")
        {
            @Override
            public void call(MovePlayerEvent event)
            {
                if (!minecraft.thePlayer.onGround && minecraft.thePlayer.isOnLadder())
                {
                    event.setMotionY(event.getMotionY() * speed.getValue());

                    if (stop.getValue())
                    {
                        if (minecraft.currentScreen != null || minecraft.thePlayer.isSneaking())
                        {
                            event.setMotionX(0D);
                            event.setMotionY(0D);
                            event.setMotionZ(0D);
                        }
                    }
                }
            }
        });
    }
}
