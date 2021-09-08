package me.comu.client.module.impl.toggle.miscellaneous;

import net.minecraft.entity.player.EnumPlayerModelParts;

import java.util.Random;

import me.comu.api.event.Listener;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;

public final class SkinFlash extends ToggleableModule
{
    private final Random random = new Random();

    public SkinFlash()
    {
        super("SkinFlash", new String[] {"skinflash", "sf", "flash"}, ModuleType.MISCELLANEOUS);
        this.listeners.add(new Listener<MotionUpdateEvent>("skin_flash_motion_update_listener")
        {
            @Override
            public void call(MotionUpdateEvent event)
            {
                EnumPlayerModelParts[] parts = EnumPlayerModelParts.values();

                if (parts != null)
                {
                    for (EnumPlayerModelParts part : parts)
                    {
                        minecraft.gameSettings.func_178878_a(part, random.nextBoolean());
                    }
                }
            }
        });
    }

    @Override
    protected void onDisable()
    {
        super.onDisable();

        if (minecraft.thePlayer == null)
        {
            return;
        }

        EnumPlayerModelParts[] parts = EnumPlayerModelParts.values();

        if (parts != null)
        {
            for (EnumPlayerModelParts part : parts)
            {
                minecraft.gameSettings.func_178878_a(part, true);
            }
        }
    }
}
