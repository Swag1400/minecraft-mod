package me.comu.client.events;

import me.comu.api.event.Event;
import net.minecraft.client.Minecraft;

public class MovePlayerEvent extends Event
{
    public double motionX, motionY, motionZ;
    private boolean safe;

    public MovePlayerEvent(double motionX, double motionY, double motionZ)
    {
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;

        if (Minecraft.getMinecraft().thePlayer != null)
        {
            safe = Minecraft.getMinecraft().thePlayer.isSneaking();
        }
    }

    public double getMotionY()
    {
        return motionY;
    }

    public void setMotionY(double motionY)
    {
        this.motionY = motionY;
    }

    public double getMotionX()
    {
        return motionX;
    }

    public void setMotionX(double motionX)
    {
        this.motionX = motionX;
    }

    public double getMotionZ()
    {
        return motionZ;
    }

    public void setMotionZ(double motionZ)
    {
        this.motionZ = motionZ;
    }

    public boolean isSafe()
    {
        return safe;
    }

    public void setSafe(boolean safe)
    {
        this.safe = safe;
    }
}
