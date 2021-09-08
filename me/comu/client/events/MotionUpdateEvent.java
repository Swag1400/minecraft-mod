package me.comu.client.events;

import me.comu.api.event.Event;

public class MotionUpdateEvent extends Event
{
    private double positionX, oldPositionY,  positionY, positionZ;
    private boolean sprinting, onGround, sneaking, lockview = false;
    private float oldRotationYaw, rotationYaw, oldRotationPitch, rotationPitch;

    private Time time;

    public MotionUpdateEvent(Time time, double positionX, double positionY, double positionZ, boolean sprinting, boolean onGround, boolean sneaking, float rotationYaw, float rotationPitch)
    {
        this.time = time;
        this.positionX = positionX;
        this.positionY = oldPositionY = positionY;
        this.positionZ = positionZ;
        this.sprinting = sprinting;
        this.onGround = onGround;
        this.sneaking = sneaking;
        this.rotationYaw = oldRotationYaw = rotationYaw;
        this.rotationPitch = oldRotationPitch = rotationPitch;
    }

    public MotionUpdateEvent(Time time)
    {
        this.time = time;
    }

    public double getPositionX()
    {
        return positionX;
    }

    public void setPositionX(double positionX)
    {
        this.positionX = positionX;
    }

    public double getPositionY()
    {
        return positionY;
    }

    public double getOldPositionY()
    {
        return oldPositionY;
    }

    public void setPositionY(double positionY)
    {
        this.positionY = positionY;
    }

    public double getPositionZ()
    {
        return positionZ;
    }

    public void setPositionZ(double positionZ)
    {
        this.positionZ = positionZ;
    }

    public boolean isSprinting()
    {
        return sprinting;
    }

    public void setSprinting(boolean sprinting)
    {
        this.sprinting = sprinting;
    }

    public boolean isOnGround()
    {
        return onGround;
    }

    public void setOnGround(boolean onGround)
    {
        this.onGround = onGround;
    }

    public boolean isSneaking()
    {
        return sneaking;
    }

    public void setSneaking(boolean sneaking)
    {
        this.sneaking = sneaking;
    }

    public float getRotationYaw()
    {
        return rotationYaw;
    }

    public void setRotationYaw(float rotationYaw)
    {
        this.rotationYaw = rotationYaw;
    }

    public float getOldRotationPitch()
    {
        return oldRotationPitch;
    }

    public float getRotationPitch()
    {
        return rotationPitch;
    }

    public float getOldRotationYaw()
    {
        return oldRotationYaw;
    }

    public void setRotationPitch(float rotationPitch)
    {
        this.rotationPitch = rotationPitch;
    }

    public boolean isLockview()
    {
        return lockview;
    }

    public void setLockview(boolean lockview)
    {
        this.lockview = lockview;
    }

    public Time getTime()
    {
        return time;
    }

    public enum Time
    {
        BEFORE, AFTER
    }
}
