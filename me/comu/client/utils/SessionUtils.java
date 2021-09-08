package me.comu.client.utils;

import java.net.*;
import com.mojang.authlib.yggdrasil.*;
import net.minecraft.client.*;
import net.minecraft.util.*;
import com.mojang.authlib.*;

public class SessionUtils
{
    private static String textOverlay;

    public static String getMessage()
    {
        return SessionUtils.textOverlay;
    }

    public static void login(final String name, final String password)
    {
        final YggdrasilAuthenticationService authenticationService = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
        final YggdrasilUserAuthentication authentication = (YggdrasilUserAuthentication)authenticationService.createUserAuthentication(Agent.MINECRAFT);
        authentication.setUsername(name);
        authentication.setPassword(password);

        try
        {
            authentication.logIn();
            Minecraft.getMinecraft().session = new Session(authentication.getSelectedProfile().getName(), authentication.getSelectedProfile().getId().toString(), authentication.getAuthenticatedToken(), "mojang");
        }
        catch (Exception e)
        {
            System.out.println("YggdrasilAuthenticationService Proxy Log-In Error");
        }
    }

    public static void changeCrackedName(final String newName)
    {
        Minecraft.getMinecraft().session = new Session(newName, "", "", UserType.MOJANG.getName());
    }
}
