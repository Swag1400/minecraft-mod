package me.comu.client.module.impl.toggle.movement;

import me.comu.api.event.Listener;
import me.comu.api.minecraft.helper.PlayerHelper;
import me.comu.client.events.AirBobbingEvent;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.NumberProperty;
import me.comu.client.properties.Property;

public final class Glide extends ToggleableModule
{
    private final Property<Boolean> damage = new Property<>(false, "Damage", "dmg", "d");
    private final NumberProperty<Float> speed = new NumberProperty<>(0.01F, 0.0001F, 1F, 0.0001F, "Speed", "s");

    public Glide()
    {
        super("Glide", new String[] {"glide", "slowfall"}, 0xFFC1ADCC, ModuleType.MOVEMENT);
        this.offerProperties(damage, speed);
        this.listeners.add(new Listener<MotionUpdateEvent>("glide_motion_update_listener")
        {
            @Override
            public void call(MotionUpdateEvent event)
            {
                minecraft.thePlayer.motionY = -speed.getValue();

                if (minecraft.gameSettings.keyBindSneak.getIsKeyPressed())
                {
                    minecraft.thePlayer.motionY = -0.4F;
                }
            }
        });
        this.listeners.add(new Listener<AirBobbingEvent>("glide_air_bobbing_listener")
        {
            @Override
            public void call(AirBobbingEvent event)
            {
                event.setCanceled(true);
            }
        });
    }

    @Override
    protected void onEnable()
    {
        super.onEnable();

        if (minecraft.thePlayer == null)
        {
            return;
        }

        if (damage.getValue())
        {
            PlayerHelper.damagePlayer();
        }
    }
}
