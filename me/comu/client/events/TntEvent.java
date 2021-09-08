package me.comu.client.events;

import me.comu.api.event.Event;

public class TntEvent extends Event {

    private double x, y, z;
    private double prevX, prevY, prevZ;

    public TntEvent(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }


    public double getPrevX() {
        return prevX;
    }

    public void setPrevX(double prevX) {
        this.prevX = prevX;
    }

    public double getPrevY() {
        return prevY;
    }

    public void setPrevY(double prevY) {
        this.prevY = prevY;
    }

    public double getPrevZ() {
        return prevZ;
    }

    public void setPrevZ(double prevZ) {
        this.prevZ = prevZ;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

}
