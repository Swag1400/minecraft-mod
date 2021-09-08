package me.comu.client.waypoints;

import me.comu.api.interfaces.Labeled;

public class Point implements Labeled
{
    private final String label;
    private final int x, y, z;
    private float[] color;

    public Point(String label, int x, int y, int z)
    {
        this.label = label;
        this.x = x;
        this.y = y;
        this.z = z;
        this.color = new float[]{(float) (Math.random()), (float) (Math.random()), (float) (Math.random())};
    }

    public Point(String label, int x, int y, int z, float r, float g, float b)
    {
        this.label = label;
        this.x = x;
        this.y = y;
        this.z = z;
        this.color = new float[]{r, g, b};
    }



    @Override
    public String getLabel()
    {
        return label;
    }

    public int getZ()
    {
        return z;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public float[] getColor() { return color; }


}