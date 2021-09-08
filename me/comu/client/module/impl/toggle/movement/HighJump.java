package me.comu.client.module.impl.toggle.movement;

import me.comu.api.event.Listener;
import me.comu.client.events.MovePlayerEvent;
import me.comu.client.events.PacketEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.NumberProperty;
import me.comu.client.properties.Property;
import net.minecraft.network.play.client.C03PacketPlayer;

public final class HighJump extends ToggleableModule
{ // GUARDIAN
	  private final NumberProperty<Double> height = new NumberProperty<>(2.5D, 0.51D, 5.0D, 0.1D, "Height", "jump","high");
	  private final Property<Boolean> nofall = new Property<>(true, "No-Fall","nofall","nf","n");

    public HighJump()
    {
        super("HighJump", new String[] {"HighJump", "high-jumo", "high","hj"}, 0xFF4D7ED1, ModuleType.MOVEMENT);
        this.offerProperties(height, nofall);
        this.listeners.add(new Listener<MovePlayerEvent>("high_jump_motion_update_listener")
        {
            @Override
            public void call(MovePlayerEvent event)
            {
            	if (!minecraft.thePlayer.onGround) {
            		minecraft.thePlayer.motionY = 4.255;
            		HighJump.this.toggle();
            	}

                
            }


        });
            this.listeners.add(new Listener<PacketEvent>("") {
                @Override
                public void call(PacketEvent event) {

                    if (event.getPacket() instanceof C03PacketPlayer)
                    {
                        C03PacketPlayer packet = (C03PacketPlayer) event.getPacket();

                        if (minecraft.thePlayer.fallDistance > 3F)
                        {
                            packet.setOnGround(true);
                        }
                    }
                }
            });
    }

 
}
