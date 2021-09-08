package me.comu.client.module.impl.toggle.movement;

import me.comu.api.event.Listener;
import me.comu.api.minecraft.helper.PlayerHelper;
import me.comu.client.events.AirBobbingEvent;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.events.MovePlayerEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.NumberProperty;
import me.comu.client.properties.Property;

public final class Flight extends ToggleableModule
{
    private final Property<Boolean> damage = new Property<>(false, "Damage", "dmg", "d");
    private final NumberProperty<Double> speed = new NumberProperty<>(2D, 1D, 10D, 1D, "Speed", "s");
    
    private boolean wasFlying;

    public Flight()
    {
        super("Flight", new String[] {"flight", "fly"}, 0xFFDEA35F, ModuleType.MOVEMENT);
        this.offerProperties(damage, speed);
        this.listeners.add(new Listener<MotionUpdateEvent>("flight_motion_update_listener")
        {
            @Override
            public void call(MotionUpdateEvent event)
            {



                if (!minecraft.thePlayer.capabilities.isFlying)
                {
                    minecraft.thePlayer.capabilities.isFlying = true;
                }

                if (minecraft.inGameHasFocus)
                {
                    if (minecraft.gameSettings.keyBindJump.getIsKeyPressed())
                    {
                        minecraft.thePlayer.motionY = 0.4D;
                    }

                    if (minecraft.gameSettings.keyBindSneak.getIsKeyPressed())
                    {
                        minecraft.thePlayer.motionY = -0.4D;
                    }
                }
                }
            });

            		
 
        this.listeners.add(new Listener<AirBobbingEvent>("flight_air_bobbing_listener")
        {
            @Override
            public void call(AirBobbingEvent event)
            {
                event.setCanceled(true);
            }
        });
        this.listeners.add(new Listener<MovePlayerEvent>("flight_move_player_listener")
        {
            @Override
            public void call(MovePlayerEvent event)
            {
                if (!PlayerHelper.isPressingMoveKeybinds())
                {
                    event.setMotionX(0D);
                    event.setMotionZ(0D);
                    return;
                }

                event.setMotionX(event.getMotionX() * speed.getValue());
                event.setMotionZ(event.getMotionZ() * speed.getValue());
            }
            	
            	
            
        });
    }

    @Override
    protected void onEnable()
    {
        if (minecraft.thePlayer != null)
        {
            wasFlying = minecraft.thePlayer.capabilities.isFlying;
        }

        super.onEnable();

        if (damage.getValue())
        {
            PlayerHelper.damagePlayer();
        }
    }

    @Override
    protected void onDisable()
    {
        super.onDisable();
        minecraft.thePlayer.capabilities.isFlying = wasFlying;
    }

}
