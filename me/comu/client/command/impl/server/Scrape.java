package me.comu.client.command.impl.server;


import me.comu.client.command.Command;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.StringUtils;


import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * Created by comu on 9/9/2018
 */

    public final class Scrape extends Command
    {
        private Queue<String> players = new ConcurrentLinkedQueue<>();

        public Scrape()
        {
            super(new String[] {"scrape"});
        }

        @Override
        public String dispatch() {

            if (minecraft.thePlayer != null)
            {
                System.out.println(players);

                for (Object o : minecraft.func_175102_a().func_175106_d())
                {
                    NetworkPlayerInfo playerInfo = (NetworkPlayerInfo) o;
                    String mcname = StringUtils.stripControlCodes(((NetworkPlayerInfo) o).func_178845_a().getName());

                    if (!mcname.equalsIgnoreCase(minecraft.thePlayer.getName()))
                    {
                        players.add(mcname);
                    }
                }
            }
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(players.toString()), null);
            return "Scraped all usernames!";
    }

}
