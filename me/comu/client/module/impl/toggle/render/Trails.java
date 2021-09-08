package me.comu.client.module.impl.toggle.render;

import me.comu.api.event.Listener;
import me.comu.api.stopwatch.Stopwatch;
import me.comu.client.events.TickEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.EnumProperty;
import me.comu.client.properties.NumberProperty;
import net.minecraft.util.EnumParticleTypes;

public final class Trails extends ToggleableModule
{
    private final EnumProperty<ParticleType> particleType = new EnumProperty<>(ParticleType.BARRIER, "Particle");
    private final NumberProperty<Long> delay = new NumberProperty<>(250L, 1L, 10000L, 250L, "Delay", "d");
    private final NumberProperty<Double> xOffset = new NumberProperty<>(0d, -10d, 10d, 0.5d, "X Offset", "xoffset", "xo");
    private final NumberProperty<Double> yOffset = new NumberProperty<>(2.7d, -10d, 10d, 0.5d, "Y Offset", "yoffset", "yo");
    private final NumberProperty<Double> zOffset = new NumberProperty<>(0d, -10d, 10d, 0.5d, "Z Offset", "zoffset", "zo");

    private final Stopwatch stopwatch = new Stopwatch();

    public Trails()
    {
        super("Trails", new String[] {"trails"}, ModuleType.RENDER);
        this.offerProperties(delay, particleType, xOffset, yOffset, zOffset);
        this.listeners.add(new Listener<TickEvent>("trails_tick_listener")
        {
            @Override
            public void call(TickEvent event)
            {
                if (stopwatch.hasCompleted(delay.getValue()))
                {
                    minecraft.theWorld.spawnParticle(particleType.getValue().particleType, minecraft.thePlayer.posX + xOffset.getValue(), minecraft.thePlayer.posY + yOffset.getValue(), minecraft.thePlayer.posZ + zOffset.getValue(), 0D, 0D, 0D, new int[0]);
                    stopwatch.reset();
                }
            }
        });
    }

    public enum ParticleType
    {
        HEART(EnumParticleTypes.HEART), MOB_APPEARANCE(EnumParticleTypes.MOB_APPEARANCE), WATER_DROP(EnumParticleTypes.WATER_DROP), SLIME(EnumParticleTypes.SLIME), SNOW_SHOVEL(EnumParticleTypes.SNOW_SHOVEL), SNOWBALL(EnumParticleTypes.SNOWBALL), REDSTONE(EnumParticleTypes.REDSTONE), FOOTSTEP(EnumParticleTypes.FOOTSTEP), LAVA(EnumParticleTypes.LAVA), FLAME(EnumParticleTypes.FLAME), ENCHANTMENT_TABLE(EnumParticleTypes.ENCHANTMENT_TABLE), PORTAL(EnumParticleTypes.PORTAL), NOTE(EnumParticleTypes.NOTE),	TOWN_AURA(EnumParticleTypes.TOWN_AURA), VILLAGER_HAPPY(EnumParticleTypes.VILLAGER_HAPPY), VILLAGER_ANGRY(EnumParticleTypes.VILLAGER_ANGRY), SPELL(EnumParticleTypes.SPELL), SPELL_INSTANT(EnumParticleTypes.SPELL_INSTANT), SPELL_MOB(EnumParticleTypes.SPELL_MOB), SPELL_MOB_AMBIENT(EnumParticleTypes.SPELL_MOB_AMBIENT), SPELL_WITCH(EnumParticleTypes.SPELL_WITCH),	SMOKE_LARGE(EnumParticleTypes.SMOKE_LARGE), SMOKE_NORMAL(EnumParticleTypes.SMOKE_NORMAL), CRIT_MAGIC(EnumParticleTypes.CRIT_MAGIC), SUSPENDED_DEPTH(EnumParticleTypes.SUSPENDED_DEPTH),	WATER_WAKE(EnumParticleTypes.WATER_WAKE), WATER_SPLASH(EnumParticleTypes.WATER_SPLASH), FIREWORKS_SPARK(EnumParticleTypes.FIREWORKS_SPARK), BARRIER(EnumParticleTypes.BARRIER), CLOUD(EnumParticleTypes.CLOUD), CRIT(EnumParticleTypes.CRIT), EXPLOSION_NORMAL(EnumParticleTypes.EXPLOSION_NORMAL), EXPLOSION_LARGE(EnumParticleTypes.EXPLOSION_LARGE), EXPLOSION_HUGE(EnumParticleTypes.EXPLOSION_HUGE), DRIP_LAVA(EnumParticleTypes.DRIP_LAVA), DRIP_WATER(EnumParticleTypes.DRIP_WATER);

        public EnumParticleTypes particleType;

        ParticleType(EnumParticleTypes particleType)
        {
            this.particleType = particleType;
        }
    }
}
