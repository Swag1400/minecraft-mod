package me.comu.client.module.impl.toggle.miscellaneous;

import me.comu.api.event.Listener;
import me.comu.api.stopwatch.Stopwatch;
import me.comu.client.events.EventTarget;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.events.MotionUpdateEvent.Time;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import net.minecraft.client.entity.EntityPlayerSP;

public final class AntiAFK extends ToggleableModule
{
    private final Stopwatch stopwatch = new Stopwatch();

    public AntiAFK()
    {
        super("AntiAFK", new String[] {"aafk", "afk", "antiafk"}, 0xFFFA8D61, ModuleType.MISCELLANEOUS);
        this.listeners.add(new Listener<MotionUpdateEvent>("auto_eat_update_listener")
        {
            @EventTarget
            public void call(MotionUpdateEvent event) {

                if (event.getTime() == Time.BEFORE && stopwatch.hasCompleted(1)) {
                    if (minecraft.currentScreen != null)
                        return;

                
                final EntityPlayerSP thePlayer = minecraft.thePlayer;
                thePlayer.rotationYaw += 15.0f;
                minecraft.thePlayer.rotationYawHead = minecraft.thePlayer.rotationYaw;
                if (minecraft.thePlayer.onGround) {
                    final EntityPlayerSP thePlayer2 = minecraft.thePlayer;
                    thePlayer2.moveForward += 24.0f;
                    minecraft.thePlayer.jump();
                }
                stopwatch.reset();
                }
            }

        });
    }
}
// TODO: fix anti afk && look at infinite dura  10/30/17