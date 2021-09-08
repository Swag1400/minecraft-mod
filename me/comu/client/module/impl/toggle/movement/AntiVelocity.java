package me.comu.client.module.impl.toggle.movement;

import me.comu.api.event.Listener;
import me.comu.client.events.PacketEvent;
import me.comu.client.events.WaterMoveEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.NumberProperty;
import me.comu.client.properties.Property;
import net.minecraft.item.ItemBow;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

public final class AntiVelocity extends ToggleableModule
{
    private final NumberProperty<Integer> percent = new NumberProperty<>(0, 0, 100, 5, "Percent", "p", "%");
    private final Property<Boolean> water = new Property<>(true, "Water", "w");
    private final Property<Boolean> aac = new Property<>(false, "AAC");
    private final Property<Boolean> bowboost = new Property<>(false, "Bow-Boost","bowboost","bow","boost","bboost");

    public AntiVelocity()
    {
        super("AntiVelocity", new String[] {"antivelocity", "novelocity", "av", "nv","kb", "vel", "velocity", "antikb"}, 0xFF9E9E9E, ModuleType.MOVEMENT);
        this.offerProperties(percent, water, aac, bowboost);
        this.listeners.add(new Listener<PacketEvent>("anti_velocity_packet_listener")
        {
            @Override
            public void call(PacketEvent event)
            {
                if (bowboost.getValue() && minecraft.thePlayer.getCurrentEquippedItem() != null && minecraft.thePlayer.getCurrentEquippedItem().getItem() != null && minecraft.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBow)
                {
                    setTag(percent.getValue() > 0 ? String.format("Velocity \247c%s", percent.getValue().toString()) : getLabel());
                    return;
                } else {
                    setTag(percent.getValue() > 0 ? String.format("Velocity \2477%s", percent.getValue().toString()) : getLabel());
                }
                if (event.getPacket() instanceof S27PacketExplosion)
                {
                    S27PacketExplosion explosion = (S27PacketExplosion) event.getPacket();

                    switch (percent.getValue())
                    {
                        case 100:
                            break;

                        case 0:
                            event.setCanceled(true);
                            break;

                        default:
                            explosion.setMotionX((explosion.getMotionX() * percent.getValue() / 100));
                            explosion.setMotionY((explosion.getMotionY() * percent.getValue() / 100));
                            explosion.setMotionZ((explosion.getMotionZ() * percent.getValue() / 100));
                            break;
                    }
                }
                else if (event.getPacket() instanceof S12PacketEntityVelocity)
                {
                    S12PacketEntityVelocity entityVelocity = (S12PacketEntityVelocity) event.getPacket();
                    if (entityVelocity.getEntityID() == minecraft.thePlayer.getEntityId())
                    {
                        switch (percent.getValue())
                        {
                            case 100:
                                break;

                            case 0:
                                event.setCanceled(true);
                                break;

                            default:
                                entityVelocity.setMotionX((entityVelocity.getMotionX() * percent.getValue() / 100));
                                entityVelocity.setMotionY((entityVelocity.getMotionY() * percent.getValue() / 100));
                                entityVelocity.setMotionZ((entityVelocity.getMotionZ() * percent.getValue() / 100));
                                break;
                        }
                    }
                }

                if (aac.getValue())
                {
                    if (minecraft.thePlayer.hurtTime > 0)
                    {
                        if (minecraft.thePlayer.onGround)
                        {
                            minecraft.thePlayer.jump();
                        }

                        minecraft.thePlayer.motionX *= 0.333F;
                        minecraft.thePlayer.motionZ *= 0.333F;
                    }
                }
            }
        });
        this.listeners.add(new Listener<WaterMoveEvent>("anti_velocity_water_move_listener")
        {
            @Override
            public void call(WaterMoveEvent event)
            {
                if (water.getValue())
                {
                    event.setCanceled(true);
                }
            }
        });
    }
}
