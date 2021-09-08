package me.comu.client.module.impl.toggle.movement;

import com.mojang.authlib.GameProfile;

import me.comu.api.event.Listener;
import me.comu.api.minecraft.render.RenderMethods;
import me.comu.client.core.Gun;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.events.PacketEvent;
import me.comu.client.events.RenderEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.module.impl.active.render.TextGUI;
import me.comu.client.properties.Property;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public final class Blink extends ToggleableModule
{
    private final Property<Boolean> breadcrumbs = new Property<>(true, "Breadcrumbs", "b", "crumbs","bc","breadcrumb");

    private final List<Packet> reservedPackets = new ArrayList<>();
    private final List<Crumb> crumbs = new ArrayList<>();
    
    int var1 = 0;

    public Blink()
    {
        super("Blink", new String[] {"blink", "fakelag"}, 0xFF7861FA, ModuleType.MOVEMENT);
        this.offerProperties(breadcrumbs);
        this.listeners.add(new Listener<MotionUpdateEvent>("blink_motion_update_listener")
        {
            @Override
            public void call(MotionUpdateEvent event)
            {
            	TextGUI textGUI = (TextGUI) Gun.getInstance().getModuleManager().getModuleByAlias("textgui");
                Property<Boolean> sf = textGUI.getPropertyByAlias("Suffix");
            	if (sf.getValue()) {
                setTag(String.format("Blink \2477" + getMS()));
            	}
                if (breadcrumbs.getValue() && !isRecorded(minecraft.thePlayer.posX, minecraft.thePlayer.posY, minecraft.thePlayer.posZ))
                {
                    crumbs.add(new Crumb(minecraft.thePlayer.posX, minecraft.thePlayer.posY, minecraft.thePlayer.posZ));
                }
            }
        });
        this.listeners.add(new Listener<PacketEvent>("blink_packet_listener")
        {
            @Override
            public void call(PacketEvent event)
            {
                if (event.getPacket() instanceof C03PacketPlayer
                        || event.getPacket() instanceof C08PacketPlayerBlockPlacement
                        || event.getPacket() instanceof C07PacketPlayerDigging)
                {
                    reservedPackets.add(event.getPacket());
                    event.setCanceled(true);
                }
            }
        });
        this.listeners.add(new Listener<RenderEvent>("blink_render_listener")
        {
            @Override
            public void call(RenderEvent event)
            {
                if (!breadcrumbs.getValue())
                {
                    return;
                }

                GlStateManager.pushMatrix();
                RenderMethods.enableGL3D();
                GL11.glColor4f(0.27F, 0.70F, 0.27F, 1F);
                GL11.glBegin(GL11.GL_LINE_STRIP);
                crumbs.forEach(crumb ->
                {
                    double x = crumb.getX() - minecraft.getRenderManager().renderPosX;
                    double y = crumb.getY() - minecraft.thePlayer.height + 3 - minecraft.getRenderManager().renderPosY;
                    double z = crumb.getZ() - minecraft.getRenderManager().renderPosZ;
                    GL11.glVertex3d(x, y, z);
                });
                GL11.glEnd();
                RenderMethods.disableGL3D();
                GlStateManager.popMatrix();
            }
        });
    }

    @Override
    protected void onEnable()
    {
        super.onEnable();
        EntityOtherPlayerMP entity = new EntityOtherPlayerMP(minecraft.theWorld,
                new GameProfile(minecraft.thePlayer.getUniqueID(), minecraft.thePlayer.getCommandSenderEntity().getName()));
        minecraft.theWorld.addEntityToWorld(-1337, entity);
        entity.setPositionAndRotation(minecraft.thePlayer.posX, minecraft.thePlayer.getEntityBoundingBox().minY, minecraft.thePlayer.posZ,
                                      minecraft.thePlayer.rotationYaw, minecraft.thePlayer.rotationPitch);
        entity.onLivingUpdate();
        this.crumbs.clear();
    }

    @Override
    protected void onDisable()
    {
        super.onDisable();
        reservedPackets.forEach(packet -> minecraft.func_175102_a().addToSendQueue(packet));
        reservedPackets.clear();
        crumbs.clear();
        minecraft.theWorld.removeEntityFromWorld(-1337);
        var1 = 0;
    }

    private boolean isRecorded(double x, double y, double z)
    {
        for (Crumb crumb : crumbs)
        {
            return crumb.getX() == x && crumb.getY() == y && crumb.getZ() == z;
        }

        return false;
    }

    private class Crumb
    {
        private final double x, y, z;

        public Crumb(double x, double y, double z)
        {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public double getX()
        {
            return x;
        }

        public double getY()
        {
            return y;
        }

        public double getZ()
        {
            return z;
        }
    }
    public int getMS() {
		return var1++;
		
     }
    
    
}
