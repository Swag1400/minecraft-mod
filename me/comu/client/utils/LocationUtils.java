package me.comu.client.utils;

import net.minecraft.block.*;
import net.minecraft.client.*;
import net.minecraft.util.*;

public class LocationUtils
{
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;
    
    public LocationUtils(final double x, final double y, final double z, final float yaw, final float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }
    
    public LocationUtils(final double x, final double y, final double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = 0.0f;
        this.pitch = 0.0f;
    }
    
    public LocationUtils(final int x, final int y, final int z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = 0.0f;
        this.pitch = 0.0f;
    }
    
    public LocationUtils add(final int x, final int y, final int z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }
    
    public LocationUtils add(final double x, final double y, final double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }
    
    public LocationUtils subtract(final int x, final int y, final int z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }
    
    public LocationUtils subtract(final double x, final double y, final double z) {
        this.x -= x;
        this.y -= y;
        this.z -= z;
        return this;
    }
    
    public Block getBlock() {
        return Minecraft.getMinecraft().theWorld.getBlockState(this.toBlockPos()).getBlock();
    }
    
    public double getX() {
        return this.x;
    }
    
    public LocationUtils setX(final double x) {
        this.x = x;
        return this;
    }
    
    public double getY() {
        return this.y;
    }
    
    public LocationUtils setY(final double y) {
        this.y = y;
        return this;
    }
    
    public double getZ() {
        return this.z;
    }
    
    public LocationUtils setZ(final double z) {
        this.z = z;
        return this;
    }
    
    public float getYaw() {
        return this.yaw;
    }
    
    public LocationUtils setYaw(final float yaw) {
        this.yaw = yaw;
        return this;
    }
    
    public float getPitch() {
        return this.pitch;
    }
    
    public LocationUtils setPitch(final float pitch) {
        this.pitch = pitch;
        return this;
    }
    
    public static LocationUtils fromBlockPos(final BlockPos blockPos) {
        return new LocationUtils(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }
    
    public BlockPos toBlockPos() {
        return new BlockPos(this.getX(), this.getY(), this.getZ());
    }
}
