package me.comu.client.module.impl.toggle.movement;

import me.comu.api.event.Listener;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.events.SprintingAttackEvent;
import me.comu.client.logging.Logger;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.Property;

public final class Sprint extends ToggleableModule
{
    private final Property<Boolean> keepSprint = new Property<>(true, "KeepSprint", "sprint", "ks", "keep");
    private final Property<Boolean> multiDir = new Property<>(true, "Multi-Direction", "MultiDirection", "multi-dir", "multidir", "direction", "dir", "Multi");

    public Sprint()
    {
        super("Sprint", new String[] {"sprint", "autosprint", "as"}, 0xFF4D7ED1, ModuleType.MOVEMENT);
        this.offerProperties(keepSprint);
        this.listeners.add(new Listener<MotionUpdateEvent>("sprint_motion_update_listener")
        {
            @Override
            public void call(MotionUpdateEvent event)
            {
                Logger.getLogger().printToChat("Ticks Per Second: " + minecraft.timer.ticksPerSecond);
                Logger.getLogger().printToChat("Timer Speed: " + minecraft.timer.timerSpeed);
                Logger.getLogger().printToChat("Elapsed Ticks: " + minecraft.timer.elapsedTicks);
                Logger.getLogger().printToChat("RenderPartialTicks Speed: " + minecraft.timer.renderPartialTicks);

                if (canSprint())
                {
                    minecraft.thePlayer.setSprinting(true);
                }
            }
        });
        this.listeners.add(new Listener<SprintingAttackEvent>("sprint_sprinting_attack_listener")
        {
            @Override
            public void call(SprintingAttackEvent event)
            {
                if (keepSprint.getValue())
                {
                    event.setCanceled(true);
                }

                if (multiDir.getValue() && canSprint())
                {
                    minecraft.thePlayer.setSprinting(true);
                    
                }
            }
        });
    }

    @Override
    protected void onDisable()
    {
        super.onDisable();
        minecraft.thePlayer.setSprinting(false);
    }

    public boolean canSprint()
    {
        return !minecraft.thePlayer.isSneaking() && minecraft.gameSettings.keyBindForward.getIsKeyPressed() && !minecraft.thePlayer.isCollidedHorizontally && minecraft.thePlayer.moveForward > 0D && minecraft.thePlayer.getFoodStats().getFoodLevel() > 6 && (multiDir.getValue() ? (minecraft.getMinecraft().thePlayer.movementInput.moveForward != 0.0f || minecraft.getMinecraft().thePlayer.movementInput.moveStrafe != 0.0f) : (minecraft.getMinecraft().thePlayer.movementInput.moveForward > 0.0f));
    }
}
