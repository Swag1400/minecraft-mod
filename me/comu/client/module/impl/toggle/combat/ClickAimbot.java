package me.comu.client.module.impl.toggle.combat;

import me.comu.api.event.Listener;
import me.comu.api.minecraft.helper.EntityHelper;
import me.comu.api.minecraft.helper.PlayerHelper;
import me.comu.client.core.Gun;
import me.comu.client.events.InputEvent;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.EnumProperty;
import me.comu.client.properties.NumberProperty;
import me.comu.client.properties.Property;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;

public final class ClickAimbot extends ToggleableModule
{
    private final NumberProperty<Float> reach = new NumberProperty<>(3.9F, 3F, 5F, 0.05F,"Reach", "range", "r");
    private final NumberProperty<Integer> fov = new NumberProperty<>(60, 30, 360, 30, "Fov", "view");
    private final EnumProperty<InputEvent.Type> button = new EnumProperty<>(InputEvent.Type.MOUSE_LEFT_CLICK, "Button", "b");
    private final Property<Boolean> rayTrace = new Property<>(false, "Ray-Trace", "raytrace", "rt", "trace", "ray"), players = new Property<>(true, "Players", "player", "p", "play"), animals = new Property<>(false, "Animals", "ani", "a", "animal"), monsters = new Property<>(false, "Monsters", "monster", "m", "mon"), invisibles = new Property<>(false, "Invisibles", "invisible", "invis", "i", "inv");

    private boolean attackedTarget = false;

    private Entity target = null;

    public ClickAimbot()
    {
        super("ClickAimbot", new String[] {"clickaimbot", "ca", "clickaim"}, 0xFFF57F99, ModuleType.COMBAT);
        this.offerProperties(reach, rayTrace, fov, button, players, monsters, animals, invisibles);
        this.listeners.add(new Listener<InputEvent>("click_aimbot_input_listener")
        {
            @Override
            public void call(InputEvent event)
            {
                if (event.getType() == button.getValue())
                {
                    if (attackedTarget)
                    {
                        attackedTarget = false;
                    }
                }
            }
        });
        this.listeners.add(new Listener<MotionUpdateEvent>("click_aimbot_motion_update_listener")
        {
            @Override
            public void call(MotionUpdateEvent event)
            {
                switch (event.getTime())
                {
                    case BEFORE:
                        if (!isValidEntity(target))
                        {
                            target = null;
                        }

                        if (target == null)
                        {
                            target = getClosestEntity();
                        }

                        if (isValidEntity(target))
                        {
                            float[] rotations = EntityHelper.getRotations(target);
                            event.setRotationYaw(rotations[0]);
                            event.setRotationPitch(rotations[1]);
                        }
                        else
                        {
                            target = null;
                        }

                        break;

                    case AFTER:
                        if (!attackedTarget)
                        {
                            if (isValidEntity(target))
                            {
                                minecraft.thePlayer.swingItem();
                                minecraft.playerController.attackEntity(minecraft.thePlayer, target);
                            }

                            target = null;
                            attackedTarget = true;
                        }

                        break;
                }
            }
        });
    }

    @Override
    protected void onEnable()
    {
        super.onEnable();
        attackedTarget = false;
        target = null;
    }

    private boolean isValidEntity(Entity entity)
    {
        if (entity == null || entity.equals(minecraft.thePlayer) || minecraft.thePlayer.getDistanceToEntity(entity) > reach.getValue() || !PlayerHelper.isAiming(EntityHelper.getRotations(entity)[0], EntityHelper.getRotations(entity)[1], fov.getValue()))
        {
            return false;
        }

        if (entity instanceof EntityPlayer)
        {
            if (entity.isInvisible() && !invisibles.getValue())
            {
                return false;
            }

            if (!minecraft.thePlayer.canEntityBeSeen(entity) && !rayTrace.getValue())
            {
                return false;
            }

            return players.getValue() && !Gun.getInstance().getFriendManager().isFriend(entity.getName());
        }

        return (entity instanceof EntityMob && monsters.getValue()) || (entity instanceof EntityAnimal && animals.getValue());
    }

    private Entity getClosestEntity()
    {
        double range = reach.getValue();
        Entity closest = null;

        for (Object object : minecraft.theWorld.loadedEntityList)
        {
            Entity entity = (Entity) object;

            if (entity instanceof EntityLivingBase)
            {
                float distance = minecraft.thePlayer.getDistanceToEntity(entity);

                if (distance < range)
                {
                    if (isValidEntity(entity))
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
