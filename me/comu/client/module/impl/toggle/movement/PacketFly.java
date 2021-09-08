package me.comu.client.module.impl.toggle.movement;

import me.comu.api.event.Listener;
import me.comu.client.events.AirBobbingEvent;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.utils.Helper;
import net.minecraft.network.play.client.C03PacketPlayer;

/**
 * Created by comu on 5/26/2018
 */
public class PacketFly extends ToggleableModule {

    public PacketFly() {
        super("PacketFly", new String[] {"PacketFly", "pfly", "packetflight"}, 0xFF4D7ED1, ModuleType.MOVEMENT);
        this.listeners.add(new Listener<MotionUpdateEvent>("flight_motion_update_listener")
        {
            @Override
            public void call(MotionUpdateEvent event) {

                if (event.getTime() == MotionUpdateEvent.Time.BEFORE) {
                    double speed = 11;
                    boolean jumpKeyPressed = minecraft.thePlayer.movementInput.jump;
                    boolean sneakKeyPressed = minecraft.thePlayer.movementInput.sneak;
                    double yDown = sneakKeyPressed ? -0.05 : 0;
                    double yUp = jumpKeyPressed ? 0.05 : yDown;
                    double y = minecraft.thePlayer.posY;
                    double xFast = minecraft.thePlayer.posX + minecraft.thePlayer.motionX * speed;
                    double yFast = y + yUp;
                    double zFast = minecraft.thePlayer.posZ + minecraft.thePlayer.motionZ * speed;

                    Helper.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(xFast, yFast, zFast, true));
                    Helper.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(xFast, minecraft.theWorld.getHeight(), zFast, true));
                    minecraft.thePlayer.motionY = 0;
                }
            }


        });



        this.listeners.add(new Listener<AirBobbingEvent>("flight_air_bobbing_listener")
        {
            @Override
            public void call(AirBobbingEvent event)
            {
                event.setCanceled(true);
            }
        });
        }
}
