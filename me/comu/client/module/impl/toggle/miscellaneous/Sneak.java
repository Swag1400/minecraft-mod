package me.comu.client.module.impl.toggle.miscellaneous;

import me.comu.api.event.Listener;
import me.comu.api.minecraft.helper.PlayerHelper;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.events.PacketEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.EnumProperty;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0BPacketEntityAction;

public final class Sneak extends ToggleableModule
{
    private final EnumProperty<Mode> mode = new EnumProperty<>(Mode.SILENT, "Mode", "m");

    public Sneak()
    {
        super("Sneak", new String[] {"sneak", "shift"}, 0xFF87D962, ModuleType.MISCELLANEOUS);
        this.offerProperties(mode);
        this.listeners.add(new Listener<MotionUpdateEvent>("sneak_motion_update_listener")
        {
            @Override
            public void call(MotionUpdateEvent event)
            {
                switch (mode.getValue())
                {
                    case SILENT:
                        switch (event.getTime())
                        {
                            case BEFORE:
                                if (!minecraft.thePlayer.isSneaking())
                                {
                                    if (PlayerHelper.isMoving())
                                    {
                                        minecraft.func_175102_a().addToSendQueue(new C0BPacketEntityAction(minecraft.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
                                        minecraft.func_175102_a().addToSendQueue(new C0BPacketEntityAction(minecraft.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
                                    }
                                    else
                                    {
                                        minecraft.func_175102_a().addToSendQueue(new C0BPacketEntityAction(minecraft.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
                                    }
                                }

                                break;

                            case AFTER:
                                if (PlayerHelper.isMoving())
                                {
                                    minecraft.func_175102_a().addToSendQueue(new C0BPacketEntityAction(minecraft.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
                                }

                                break;
                        }

                        break;

                    case VANILLA:
                        minecraft.func_175102_a().addToSendQueue(new C0BPacketEntityAction(minecraft.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
                        break;

                    case KEY:
                        minecraft.gameSettings.keyBindSneak.pressed = true;
                        break;
                }
            }
        });
        this.listeners.add(new Listener<PacketEvent>("sneak_packet_listener")
        {
            @Override
            public void call(PacketEvent event)
            {
                if (event.getPacket() instanceof C08PacketPlayerBlockPlacement)
                {
                    minecraft.func_175102_a().addToSendQueue(new C0BPacketEntityAction(minecraft.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
                }
            }
        });
    }

    @Override
    protected void onDisable()
    {
        super.onDisable();

        switch (mode.getValue())
        {
            case SILENT:
            case VANILLA:
                if (!minecraft.gameSettings.keyBindSneak.getIsKeyPressed())
                {
                    minecraft.func_175102_a().addToSendQueue(new C0BPacketEntityAction(minecraft.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
                }

                break;

            case KEY:
                if (minecraft.gameSettings.keyBindSneak.getIsKeyPressed())
                {
                    minecraft.gameSettings.keyBindSneak.pressed = false;
                }

                break;
        }
    }

    public enum Mode
    {
        SILENT, KEY, VANILLA
    }
}
