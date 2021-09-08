package me.comu.client.utils;

import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import java.awt.*;
import java.util.Random;

public class ColorHelper
{
  private static final Random random = new Random();
  
  public static int changeAlpha(int color, int alpha)
  {
    color &= 0xFFFFFF;
    return alpha << 24 | color;
  }

  public static int getColor(final Color color) {
    return getColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
  }

  public static int getColor(final int brightness) {
    return getColor(brightness, brightness, brightness, 255);
  }

  public static int getColor(final int brightness, final int alpha) {
    return getColor(brightness, brightness, brightness, alpha);
  }

  public static int getColor(final int red, final int green, final int blue) {
    return getColor(red, green, blue, 255);
  }

  public static int getColor(final int red, final int green, final int blue, final int alpha) {
    int color = 0;
    color |= alpha << 24;
    color |= red << 16;
    color |= green << 8;
    color |= blue;
    return color;
  }

  public static float[] getRGBA(int color)
  {
    float a = (color >> 24 & 0xFF) / 255.0F;
    float r = (color >> 16 & 0xFF) / 255.0F;
    float g = (color >> 8 & 0xFF) / 255.0F;
    float b = (color & 0xFF) / 255.0F;
    return new float[] { r, g, b, a };
  }
  
  public static int toRGBAHex(Color color)
  {
    String alpha = pad(Integer.toHexString(color.getAlpha()));
    String red = pad(Integer.toHexString(color.getRed()));
    String green = pad(Integer.toHexString(color.getGreen()));
    String blue = pad(Integer.toHexString(color.getBlue()));
    String hex = "0x" + alpha + red + green + blue;
    return Integer.parseInt(hex, 16);
  }
  
  public static int toRGBAHex(float r, float g, float b, float a)
  {
    return ((int)(a * 255.0F) & 0xFF) << 24 | ((int)(r * 255.0F) & 0xFF) << 16 | ((int)(g * 255.0F) & 0xFF) << 8 | (int)(b * 255.0F) & 0xFF;
  }
  
  public static int generateColor()
  {
    float h = random.nextFloat();
    float s = random.nextInt(5000) / 10000.0F + 0.5F;
    float b = random.nextInt(5000) / 10000.0F + 0.5F;
    return Color.HSBtoRGB(h, s, b);
  }
  
  private static String pad(String s)
  {
    return s.length() == 1 ? "0" + s : s;
  }
  
  public static int getArmorColor(ItemStack stack)
  {
    if (!(stack.getItem() instanceof ItemArmor)) {
      return -16777216;
    }
    switch (((ItemArmor)stack.getItem()).getArmorMaterial())
    {
    case LEATHER: 
      return -7053765;
    case CHAIN: 
      return -6908266;
    case IRON: 
      return -3750202;
    case GOLD: 
      return -1380777;
    case DIAMOND: 
      return -13374517;
    }
    return -16777216;
  }
}
