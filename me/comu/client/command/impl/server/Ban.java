package me.comu.client.command.impl.server;

import me.comu.api.stopwatch.Stopwatch;
import me.comu.client.command.Argument;
import me.comu.client.command.Command;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.StringUtils;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Ban extends Command
{
    private Queue<String> players = new ConcurrentLinkedQueue<>();
    private final Stopwatch stopwatch = new Stopwatch();

    public Ban()
    {
        super(new String[] {"ban"}, new Argument("get|list|clear|all"));
    }

    @Override
    public String dispatch()
    {
        switch (getArgument("get|list|clear|all").getValue())
        {
            case "get":
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

                return "List collected.";

            case "all":
                String player = players.iterator().next();

                if (stopwatch.hasCompleted(600L))
                {
                    for (int index = 0; index < 2; index++)
                    {
                        minecraft.thePlayer.sendChatMessage("/ban " + player);
                        players.remove(player);
                    }
                }

                return "Banned " + players.size() + " players!";

            case "clear":
                players.clear();
                return "Ban list cleared!";

            case "list":
                return String.valueOf((players));
        }

        return null;
    }
}
