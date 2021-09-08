package me.comu.client.module.impl.toggle.combat;

import me.comu.api.stopwatch.Stopwatch;
import me.comu.client.core.Gun;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.properties.Property;
import me.comu.client.utils.ClientUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;

import me.comu.api.event.Listener;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.NumberProperty;

import java.util.Random;

public final class Triggerbot2 extends ToggleableModule
{
    private final NumberProperty<Integer> attackDelay = new NumberProperty<>(150, 0, 1000, 50, "CPS", "clicks", "click","delay","attack-delay","attackdelay","ad");
    private final NumberProperty<Float> reach = new NumberProperty<>(4F, 3F, 6F, 0.1F, "Reach", "range", "r");
    private final Property<Boolean> randomize = new Property<>(false, "Randomize","random"), players = new Property<>(true, "Players", "p","player"), monsters = new Property<>(true, "Monsters","monster","mon", "m"), animals = new Property<>(true, "Animals","animal" ,"a"), invisibles = new Property<>(true, "Invisibles","invis","i","invisible");

    private static final Stopwatch stopwatch = new Stopwatch();
    private final int[] nums = new int[] {3, 4, 5, 6, 7, 8, 9};

    private int delay;


    public Triggerbot2()
    {
        super("Triggerbot", new String[] {"triggerbot", "tb", "trigger","trigger-bot"}, 0xCC7422FA, ModuleType.COMBAT);
        this.offerProperties(attackDelay,randomize,players,monsters,animals,invisibles, reach);
        this.listeners.add(new Listener<MotionUpdateEvent>("auto_clicker_tick_listener")
        {
            @Override
            public void call(MotionUpdateEvent event)
            {
                if (stopwatch.hasCompleted(attackDelay.getValue())) {
                    attack((EntityLivingBase) ClientUtils.mc().objectMouseOver.entityHit);
                    stopwatch.reset();
                }

                if (ClientUtils.mc().objectMouseOver.entityHit != null && isEntityValid(ClientUtils.mc().objectMouseOver.entityHit)) {
                    ++delay;
                    if (delay >= (int)(randomize.getValue() ? getRandom(nums) : attackDelay)) {
                        attack((EntityLivingBase)ClientUtils.mc().objectMouseOver.entityHit);
                        delay = 0;
                    }
                }
            }

        });

    }

    public boolean isEntityValid(final Entity entity) {
        if (entity instanceof EntityLivingBase) {
            final EntityLivingBase entityLiving = (EntityLivingBase)entity;
            if (!ClientUtils.player().isEntityAlive() || !entityLiving.isEntityAlive() || entityLiving.getDistanceToEntity(ClientUtils.player()) > (ClientUtils.player().canEntityBeSeen(entityLiving) ? reach.getValue() : 3.0)) {
                return false;
            }
            if (players.getValue() && entityLiving instanceof EntityPlayer) {
                final EntityPlayer entityPlayer = (EntityPlayer)entityLiving;
                return !Gun.getInstance().getFriendManager().isFriend(entityPlayer.getName());
            }
            if (monsters.getValue() && (entityLiving instanceof EntityMob || entityLiving instanceof EntityGhast || entityLiving instanceof EntityDragon || entityLiving instanceof EntityWither || entityLiving instanceof EntitySlime || (entityLiving instanceof EntityWolf && ((EntityWolf)entityLiving).getOwner() != ClientUtils.player()))) {
                return true;
            }
            if (animals.getValue() && (entityLiving instanceof EntityAnimal || entityLiving instanceof EntitySquid)) {
                return true;
            }
        }
        return false;
    }

    private void attack(final EntityLivingBase ent) {
        ClientUtils.player().swingItem();
        ClientUtils.player().sendQueue.addToSendQueue(new C02PacketUseEntity(ent, C02PacketUseEntity.Action.ATTACK));

    }

    public int getRandom(final int[] array) {
        final int rnd = new Random().nextInt(array.length);
        return array[rnd];
    }

}
