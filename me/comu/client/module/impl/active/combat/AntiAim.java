package me.comu.client.module.impl.active.combat;

import me.comu.client.module.Module;

public final class AntiAim extends Module
{
    public AntiAim()
    {
        super("Anti Aim", new String[] {"antiaim", "aa"});
/*        Gun.getInstance().getEventManager().register(new Listener<PacketEvent>("anti_aim_packet_listener")
        {
                if (event.getPacket() instanceof S08PacketPlayerPosLook)
                {
                    S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook) event.getPacket();

                    if (minecraft.thePlayer.rotationYaw != -180 && minecraft.thePlayer.rotationPitch != 0)
                    {
                        packet.setYaw(minecraft.thePlayer.rotationYaw);
                        packet.setPitch(minecraft.thePlayer.rotationPitch);
                    }
                }
            }
        });*/
    }
}
