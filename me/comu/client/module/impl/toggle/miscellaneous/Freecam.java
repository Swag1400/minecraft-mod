package me.comu.client.module.impl.toggle.miscellaneous;

import com.mojang.authlib.GameProfile;

import me.comu.api.event.Listener;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.events.MovePlayerEvent;
import me.comu.client.events.PacketEvent;
import me.comu.client.events.RenderHandEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.NumberProperty;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.network.play.client.C03PacketPlayer;

import java.util.List;

public final class Freecam extends ToggleableModule

{
    private final NumberProperty<Double> speed = new NumberProperty<>(2D, 1D, 10D, 1D, "Speed", "s");

    private double startX, startY, startZ;
    private float yaw, pitch;

    public Freecam()
    {
        super("Freecam", new String[] {"freecam", "camera"}, ModuleType.MISCELLANEOUS);
        this.offerProperties(speed);
        this.listeners.add(new Listener<MotionUpdateEvent>("freecam_motion_update_listener")
        {
            @Override
            public void call(MotionUpdateEvent event)
            {
                List boxes = minecraft.theWorld.getCollidingBoundingBoxes(minecraft.thePlayer, minecraft.thePlayer.getEntityBoundingBox().expand(0.5D, 0.5D, 0.5D));
                minecraft.thePlayer.noClip = !boxes.isEmpty();

                if (!minecraft.thePlayer.capabilities.isFlying)
                {
                    minecraft.thePlayer.capabilities.isFlying = true;
                }

                if (minecraft.inGameHasFocus)
                {
                    if (minecraft.gameSettings.keyBindJump.getIsKeyPressed())
                    {
                        minecraft.thePlayer.motionY = 0.4D;
                    }

                    if (minecraft.gameSettings.keyBindSneak.getIsKeyPressed())
                    {
                        minecraft.thePlayer.motionY = -0.4D;
                    }
                }
            }
        });
        this.listeners.add(new Listener<PacketEvent>("freecam_packet_listener")
        {
            @Override
            public void call(PacketEvent event)
            {
                if (event.getPacket() instanceof C03PacketPlayer)
                {
                    if (!event.isCancelled())
                    {
                        event.setCanceled(true);
                    }
                }
            }
        });
        this.listeners.add(new Listener<MovePlayerEvent>("freecam_move_player_listener")
        {
            @Override
            public void call(MovePlayerEvent event)
            {
                event.setMotionX(event.getMotionX() * speed.getValue());
                event.setMotionZ(event.getMotionZ() * speed.getValue());
            }
        });
        this.listeners.add(new Listener<RenderHandEvent>("freecam_render_hand_listener")
        {
            @Override
            public void call(RenderHandEvent event)
            {
                event.setCanceled(true);
            }
        });
    }

    @Override
    protected void onEnable()
    {
        super.onEnable();
        minecraft.renderGlobal.loadRenderers();
        startX = minecraft.thePlayer.posX;
        startY = minecraft.thePlayer.posY;
        startZ = minecraft.thePlayer.posZ;
        yaw = minecraft.thePlayer.rotationYaw;
        pitch = minecraft.thePlayer.rotationPitch;
        EntityOtherPlayerMP entity = new EntityOtherPlayerMP(minecraft.theWorld, new GameProfile(minecraft.thePlayer.getUniqueID(), minecraft.thePlayer.getCommandSenderEntity().getName()));
        minecraft.theWorld.addEntityToWorld(-1337, entity);
        entity.setPositionAndRotation(startX, minecraft.thePlayer.getEntityBoundingBox().minY, startZ, yaw, pitch);
        entity.setSneaking(minecraft.thePlayer.isSneaking());
    }

    @Override
    protected void onDisable()
    {
        super.onDisable();
        minecraft.renderGlobal.loadRenderers();
        minecraft.thePlayer.setPositionAndRotation(startX, startY, startZ, yaw, pitch);
        minecraft.thePlayer.noClip = false;
        minecraft.theWorld.removeEntityFromWorld(-1337);
        minecraft.thePlayer.capabilities.isFlying = false;
    }
}
