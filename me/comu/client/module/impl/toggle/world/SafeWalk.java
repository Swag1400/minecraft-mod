package me.comu.client.module.impl.toggle.world;

import me.comu.api.event.Listener;
import me.comu.client.events.MovePlayerEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;

public final class SafeWalk extends ToggleableModule
{
    public SafeWalk()
    {
        super("SafeWalk", new String[] {"safewalk", "sw", "walk", "safe"}, 0xFFD14D7E, ModuleType.WORLD);
        this.listeners.add(new Listener<MovePlayerEvent>("safe_walk_move_player_listener")
        {
            @Override
            public void call(MovePlayerEvent event)
            {
                event.setSafe(true);
            }
        });
    }
}
