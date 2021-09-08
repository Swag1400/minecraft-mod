package me.comu.client.module.impl.toggle.movement;

import me.comu.api.event.Listener;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.events.PacketEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.EnumProperty;
import net.minecraft.network.play.client.C03PacketPlayer;

public final class NoFall extends ToggleableModule
{
    private final EnumProperty<Mode> mode = new EnumProperty<>(Mode.DAMAGE, "Mode", "m");

    public NoFall()
    {
        super("NoFall", new String[] {"nofall", "0fall", "nf"}, 0xFF3DCC4E, ModuleType.MOVEMENT);
        this.offerProperties(mode);
        this.listeners.add(new Listener<PacketEvent>("no_fall_packet_listener")
        {
            @Override
            public void call(PacketEvent event)
            {
                if (mode.getValue() == Mode.DAMAGE)
                {
                    if (event.getPacket() instanceof C03PacketPlayer)
                    {
                        C03PacketPlayer packet = (C03PacketPlayer) event.getPacket();

                        if (minecraft.thePlayer.fallDistance > 3F)
                        {
                            packet.setOnGround(true);
                        }
                    }
                }
            }
        });
        this.listeners.add(new Listener<MotionUpdateEvent>("no_fall_motion_update_listener")
        {
            @Override
            public void call(MotionUpdateEvent event)
            {
                switch (mode.getValue())
                {
                    case INFINITE:
                        setTag("InfiniteJump");
                        minecraft.thePlayer.onGround = true;
                        break;
                        
                    case VANILLA:
                    	if(minecraft.thePlayer.fallDistance <= 3) {
                    	event.setOnGround(true);
                    	}
                    case DAMAGE:
                        setTag("NoFall");
                        break;
            /*        case GUARDIAN:
                    	if (minecraft.thePlayer.fallDistance >= 3) {
                    		event.setOnGround(true);
                    	}
                    	break;
*/
               /*     case RECONNECT:
                        setTag("ReconnectFall");

                        if (!(PlayerHelper.getBlockBelowPlayer(4D) instanceof BlockAir))
                        {
                            ServerData serverData = minecraft.getCurrentServerData();
                            minecraft.theWorld.sendQuittingDisconnectingPacket();
                            minecraft.displayGuiScreen(new GuiConnecting(null, minecraft, serverData));
                            setRunning(false);
                        }

                        break;
*/
                   /* case DISCONNECT:
                        setTag("DisconnectFall");

                        if (!(PlayerHelper.getBlockBelowPlayer(4D) instanceof BlockAir))
                        {
                            minecraft.theWorld.sendQuittingDisconnectingPacket();
                            setRunning(false);
                        }
							break;
                     */   
                }
            }
        });
    }

    public enum Mode
    {
        DAMAGE, INFINITE, VANILLA
    }
}
