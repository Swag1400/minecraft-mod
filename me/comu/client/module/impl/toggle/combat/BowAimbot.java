package me.comu.client.module.impl.toggle.combat;

import me.comu.api.event.Listener;
import me.comu.api.minecraft.helper.EntityHelper;
import me.comu.api.minecraft.helper.PlayerHelper;
import me.comu.client.core.Gun;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.EnumProperty;
import me.comu.client.properties.NumberProperty;
import me.comu.client.properties.Property;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBow;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class BowAimbot extends ToggleableModule
{
    private final NumberProperty<Integer> ticks = new NumberProperty<>(51, 0, 100, 5, "Ticks-Existed", "te", "ticks", "existed"), fov = new NumberProperty<>(60, 30, 360, 30, "Fov", "f");
    private final NumberProperty<Float> reach = new NumberProperty<>(50F, 6F, 80F, 8F, "Reach", "range", "r", "distance", "dist");
    private final Property<Boolean> players = new Property<>(true, "Players", "player", "p", "player"), animals = new Property<>(false, "Animals", "ani", "animal"), invisibles = new Property<>(true, "Invisibles", "invis", "inv", "invisible"), monsters = new Property<>(false, "Monsters", "monster", "mon", "m", "monst"), silent = new Property<>(true, "Silent", "s", "lock");

    private final List<EntityLivingBase> validTargets = new CopyOnWriteArrayList<>();

    private EntityLivingBase target = null;

    public BowAimbot()
    {
        super("BowAimbot", new String[] {"bowaimbot", "ba", "bowaim", "bow"}, 0xFFCCBF99, ModuleType.COMBAT);
        this.offerProperties(ticks, reach, players, animals, invisibles, monsters, silent, fov);
        this.listeners.add(new Listener<MotionUpdateEvent>("bow_aimbot_motion_update_listener")
        {
            @Override
            public void call(MotionUpdateEvent event)
            {
                switch (event.getTime())
                {
                    case BEFORE:
                        if (validTargets.isEmpty())
                        {
                            for (Object object : minecraft.theWorld.loadedEntityList)
                            {
                                Entity entity = (Entity) object;

                                if (entity instanceof EntityLivingBase)
                                {
                                    EntityLivingBase entityLivingBase = (EntityLivingBase) entity;

                                    if (isValidEntity(entityLivingBase) && validTargets.size() < 5)
                                    {
                                        validTargets.add(entityLivingBase);
                                    }
                                }
                            }
                        }

                        validTargets.forEach(entityLivingBase ->
                        {
                            if (!isValidEntity(entityLivingBase))
                            {
                                validTargets.remove(entityLivingBase);
                            }
                        });
                        target = getClosestEntity();

                        if (isValidEntity(target))
                        {
                            AutoHeal autoHeal = (AutoHeal) Gun.getInstance().getModuleManager().getModuleByAlias("autoheal");
                            EnumProperty<AutoHeal.Mode> mode = (EnumProperty<AutoHeal.Mode>) autoHeal.getPropertyByAlias("Mode");

                            if (autoHeal != null && autoHeal.isRunning() && mode.getValue() == AutoHeal.Mode.POTION && autoHeal.isPotting())
                            {
                                return;
                            }

                            float[] rotations = EntityHelper.getRotations(target);

                            if (minecraft.thePlayer.getCurrentEquippedItem() == null)
                            {
                                return;
                            }

                            if (!(minecraft.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBow))
                            {
                                return;
                            }

                            event.setLockview(!silent.getValue());

                            if (minecraft.thePlayer.isUsingItem())
                                if (silent.getValue())
                                {
                                    event.setRotationYaw(PlayerHelper.wrapAngleTo180(rotations[0]));
                                    event.setRotationPitch(PlayerHelper.wrapAngleTo180(rotations[1] + (minecraft.thePlayer.getDistanceToEntity(target) * -0.15F)));
                                }
                                else
                                {
                                    minecraft.thePlayer.rotationYaw = PlayerHelper.wrapAngleTo180(rotations[0]);
                                    minecraft.thePlayer.rotationPitch = PlayerHelper.wrapAngleTo180(rotations[1] + (minecraft.thePlayer.getDistanceToEntity(target) * -0.15F));
                                }
                        }
                        else
                        {
                            validTargets.remove(target);
                            target = null;
                        }

                        break;
                }
            }
        });
    }

    @Override
    protected void onDisable()
    {
        super.onDisable();
        target = null;
        validTargets.clear();
    }

    private boolean isValidEntity(EntityLivingBase entity)
    {
        if (entity == null || entity.isDead || !entity.isEntityAlive() || minecraft.thePlayer.getDistanceToEntity(entity) > reach.getValue() || entity.ticksExisted < ticks.getValue() || !minecraft.thePlayer.canEntityBeSeen(entity))
        {
            return false;
        }

        if (entity instanceof IMob)
        {
            return monsters.getValue();
        }

        if (entity instanceof IAnimals)
        {
            return animals.getValue();
        }

        if (entity instanceof EntityPlayer)
        {
            if (!players.getValue())
            {
                return false;
            }

            EntityPlayer entityPlayer = (EntityPlayer) entity;

            if (entityPlayer.equals(minecraft.thePlayer) || entityPlayer.capabilities.isCreativeMode)
            {
                return false;
            }

            if (entityPlayer.isInvisible())
            {
                return invisibles.getValue();
            }

            return !Gun.getInstance().getFriendManager().isFriend(entityPlayer.getName());
        }

        return true;
    }

    private EntityLivingBase getClosestEntity()
    {
        double range = reach.getValue();
        EntityLivingBase closest = null;

        for (EntityLivingBase entity : validTargets)
        {
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

        return closest;
    }
}
