package me.comu.client.module.impl.toggle.movement;

import me.comu.api.event.Listener;
import me.comu.client.events.EventTarget;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.Property;

public final class AutoWalk extends ToggleableModule
{
	
    private final Property<Boolean> jump = new Property<>(false, "Jump","j");
    private final Property<Boolean> swim = new Property<>(false, "Swim","Swimming","s");


    public AutoWalk()
    
    {
        super("AutoWalk", new String[] {"autowalk","aw", "awalk", "automove"}, 0xFFFA8D61, ModuleType.MISCELLANEOUS);
        this.offerProperties(jump, swim);
        this.listeners.add(new Listener<MotionUpdateEvent>("auto_eat_update_listener")
        {
            @EventTarget
            public void call(MotionUpdateEvent event) {
                if (jump.getValue() && minecraft.thePlayer.onGround && minecraft.thePlayer.isCollidedVertically)
                    minecraft.thePlayer.jump();
                minecraft.gameSettings.keyBindForward.pressed = true;


            }
            
        });
    }

    @Override
    public void onDisable() {

        super.onDisable();
        minecraft.gameSettings.keyBindForward.pressed = false;
    }


}
