package me.comu.client.logging;

import me.comu.client.core.Gun;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

public final class Logger
{
    private static Logger logger = null;

    public void print(String message)
    {
        System.out.println(String.format("[%s] %s", Gun.TITLE, message));
    }

    public void printToChat(String message)
    {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(String.format("\247c[%s] \2477%s", Gun.TITLE, message.replace("&", "\247"))).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GRAY)));
    }

    public static Logger getLogger()
    {
        return logger == null ? logger = new Logger() : logger;
    }
}
