package me.comu.client.command.impl.client;

import me.comu.client.command.Argument;
import me.comu.client.command.Command;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
/*
 * Made by Comu
 * 11/25/17
 */

public final class History extends Command {
    public History() {
        super(new String[]{"history", "namemc", "names", "his"}, new Argument("IGN"));
    }


    @Override
    public String dispatch() {
        if (getArgument("IGN").getValue().length() > 16) {
            return "§e" + (getArgument("IGN").getValue()) + "§7 is too long to be a name!";
        }
        String uuid = "";
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + (getArgument("IGN").getValue()));
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;

            while ((line = br.readLine()) != null) {
                uuid = line.split("\"")[7];
            }
            br.close();
            url = new URL("https://api.mojang.com/user/profiles/" + uuid + "/names");
            br = new BufferedReader(new InputStreamReader(url.openStream()));
            String oldNames = "";
            while ((line = br.readLine()) != null) {
                final String originalName = line.split("\"")[3];
                for (int i = 0; i < line.split("\"").length; ++i) {
                    if (i != line.split("\"").length - 1 && line.split("\"")[i + 1].equals(",")) {
                        if (oldNames.equals("")) {
                            oldNames = line.split("\"")[i];
                        } else {
                            oldNames = oldNames + ", " + line.split("\"")[i];
                        }
                    }
                }
                if (oldNames.equals("")) {
                    return (getArgument("IGN").getValue()) + " hasn't changed their name!";
                } else {
                    return (getArgument("IGN").getValue()) + "'s name history: " + originalName + ", " + oldNames + ".";
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "No one has the name §e" + getArgument("IGN").getValue() + "§7.";
        }
        return "Unable to retrieve UUID of player.";
    }

}
        
    
    

