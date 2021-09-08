package me.comu.client.module.impl.toggle.movement;

import me.comu.api.event.Listener;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.events.MovePlayerEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.NumberProperty;
import me.comu.client.properties.Property;
import me.comu.client.utils.ClientUtils;

/**
 * Created by comu on 08/05/19
 */
public class VanillaFly extends ToggleableModule {

    private final Property<Boolean> damage = new Property<>(false, "Damage", "dmg", "d");
    private final NumberProperty<Double> speed = new NumberProperty<>(2D, 1D, 10D, 1D,"Speed", "s");

    public VanillaFly() {
        super("VanillaFly", new String[]{"VanillaFly", "vfly"}, 0xFF4D7ED1, ModuleType.MOVEMENT);
        this.offerProperties(damage,speed);
        this.listeners.add(new Listener<MotionUpdateEvent>("high_jump_motion_update_listener") {
            @Override
            public void call(MotionUpdateEvent event) {
                if (MotionUpdateEvent.Time.BEFORE == event.getTime()) {
                    if (ClientUtils.movementInput().jump) {
                        ClientUtils.player().motionY = speed.getValue();
                    }
                    else if (ClientUtils.movementInput().sneak) {
                        ClientUtils.player().motionY = -speed.getValue();
                    }
                    else {
                        ClientUtils.player().motionY = 0.0;
                    }
                }
            }
        });

        this.listeners.add(new Listener<MovePlayerEvent>("high_jump_motion_update_listener") {
            @Override
            public void call(MovePlayerEvent event) {
                ClientUtils.setMoveSpeed(event, speed.getValue());
            }
        });
    }
}
