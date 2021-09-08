package me.comu.client.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

public class NetworkHelper {
	Minecraft mc = new Minecraft(null);

	public static int getFPS() {

	    return Minecraft.debugFPS;
	}

	public static int getMS() {
		Object o = Minecraft.getMinecraft().thePlayer;
		Entity entity = (Entity)o;
		return Minecraft.getMinecraft().func_175102_a().func_175104_a(entity.getName()).getResponseTime();
	}
	
}
