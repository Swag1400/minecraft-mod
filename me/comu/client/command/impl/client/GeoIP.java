package me.comu.client.command.impl.client;


import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import me.comu.client.command.Argument;
import me.comu.client.command.Command;

/*
 * Made by Comu
 * 8/30/19
 */

public final class GeoIP extends Command
{
    public GeoIP()
    {
        super(new String[] {"geoip","geo-ip","ip","checkip","geo"}, new Argument("IP"));
    }
    private final char[] abc = new char[] {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','y','x','z'};
    private final char[] nums = new char[] {'1','2','3','4','5','6','7','8','9','0'};

    @Override
    public String dispatch()
    {
        String arg = getArgument("IP").getValue();
        if (getArgument("IP").getValue().length() > 15 || getArgument("IP").getValue().length() < 9|| getArgument("IP").getValue().matches("[a-zA-Z]+") || !(getArgument("IP").getValue().contains(".")) || getArgument("IP").getValue().contains(",") || arg.contains("!") || arg.contains("@") || arg.contains("-")|| arg.contains("_")|| arg.contains("+") || arg.contains("=") || arg.contains("'")|| arg.contains("\"\"")|| arg.contains(":")|| arg.contains(";")|| arg.contains("\\")|| arg.contains("|")|| arg.contains("[")|| arg.contains("{")|| arg.contains("]")|| arg.contains("}")|| arg.contains("@")|| arg.contains("#") || arg.contains("$")|| arg.contains("%")|| arg.contains("^")|| arg.contains("&")|| arg.contains("*")|| arg.contains("(")|| arg.contains(")")|| arg.contains("<")|| arg.contains(">")|| arg.contains("?")|| arg.contains("`")|| arg.contains("~")) {
            return "§7IP invalid!";
        }
        try {
            boolean found = false;
            final URL url = new URL("https://geoiptool.com/en/?ip=" + (getArgument("IP").getValue()));
            //Logger.getLogger().printToChat(url.toString());
            Desktop.getDesktop().browse(url.toURI());
            found = true;
            if (found)
                return "Opened GeoIP Link.";
//            final Document document = Jsoup.connect(url.toString()).get();
//            for (Element info : document.select("div.data-item")) {
//            Logger.getLogger().printToChat(info.text());
//
//            }

        }

        catch (IOException e) {
            return "Caught IOException.";
        } catch (URISyntaxException e) {
            return "Caught URISyntaxEception.";
        }
        return "§e" + getArgument("IP").getValue() +"§7 couldn't be resolved.";
    }

    private static void verify() {

    }
}




