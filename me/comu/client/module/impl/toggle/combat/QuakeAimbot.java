package me.comu.client.module.impl.toggle.combat;

import me.comu.api.event.Listener;
import me.comu.api.minecraft.helper.EntityHelper;
import me.comu.api.minecraft.helper.PlayerHelper;
import me.comu.client.core.Gun;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.NumberProperty;
import me.comu.client.properties.Property;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemHoe;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;

public final class QuakeAimbot extends ToggleableModule
{
    private final Property<Boolean> silent = new Property<>(true, "Silent", "s", "lock");
    private final NumberProperty<Integer> fov = new NumberProperty<>(100, 30, 180, 20, "Fov", "f");
    private final NumberProperty<Float> reach = new NumberProperty<>(60F, 1F, 120F, 10F, "Reach", "range", "r", "distance");

    private EntityLivingBase target;

    public QuakeAimbot()
    {
        super("QuakeAimbot", new String[] {"quakeaimbot", "qa", "quakeaim"}, 0xFF96D490, ModuleType.COMBAT);
        this.offerProperties(fov, silent, reach);
        this.listeners.add(new Listener<MotionUpdateEvent>("quake_aimbot_motion_update_listener")
        {
            @Override
            public void call(MotionUpdateEvent event)
            {
                switch (event.getTime())
                {
                    case BEFORE:
                        if (target == null)
                        {
                            target = getClosestEntity();
                        }

                        if (isValidEntity(target))
                        {
                            event.setLockview(!silent.getValue());
                            float[] rotations = EntityHelper.getRotations(target);

                            if (silent.getValue())
                            {
                                event.setRotationYaw(PlayerHelper.wrapAngleTo180(rotations[0]));
                                event.setRotationPitch(PlayerHelper.wrapAngleTo180(rotations[1]));
                            }
                            else
                            {
                                minecraft.thePlayer.rotationYaw = PlayerHelper.wrapAngleTo180(rotations[0]);
                                minecraft.thePlayer.rotationPitch = PlayerHelper.wrapAngleTo180(rotations[1]);
                            }
                        }
                        else
                        {
                            target = null;
                        }

                        break;

                    case AFTER:
                        if (isValidEntity(target))
                        {
                            if (minecraft.thePlayer.inventory.getCurrentItem() != null)
                            {
                                if (minecraft.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemHoe && minecraft.thePlayer.experienceLevel == 0)
                                {
                                    minecraft.func_175102_a().addToSendQueue(new C08PacketPlayerBlockPlacement(minecraft.thePlayer.inventory.getCurrentItem()));
                                }
                            }
                        }
                        else
                        {
                            target = null;
                        }

                        break;
                }
            }
        });
    }

    private boolean isValidEntity(EntityLivingBase entity)
    {
        return entity != null && entity instanceof EntityPlayer && !(entity.equals(minecraft.thePlayer) || minecraft.thePlayer.getDistanceToEntity(entity) > reach.getValue()) && !Gun.getInstance().getFriendManager().isFriend(entity.getName());
    }

    private EntityLivingBase getClosestEntity()
    {
        double range = reach.getValue();
        EntityLivingBase closest = null;

        for (Object object : minecraft.theWorld.loadedEntityList)
        {
            if (object instanceof EntityLivingBase)
            {
                EntityLivingBase entity = (EntityLivingBase) object;
                float distance = minecraft.thePlayer.getDistanceToEntity(entity);

                if (distance < range)
                {
                    if (isValidEntity(entity) && PlayerHelper.isAiming(EntityHelper.getRotations(entity)[0], EntityHelper.getRotations(entity)[1], fov.getValue()))
                    {
                        closest = entity;
                        range = distance;
                    }
                }
            }
        }

        return closest;
    }
}
