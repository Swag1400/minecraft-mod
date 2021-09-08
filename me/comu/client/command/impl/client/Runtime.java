package me.comu.client.command.impl.client;

import me.comu.client.command.Argument;
import me.comu.client.command.Command;
import me.comu.client.core.Gun;


public final class Runtime extends Command
{
    public Runtime()
    {
        super(new String[] {"runtime", "rtime"}, new Argument("format"));
    }

    @Override
    public String dispatch()
    {
        String runtime;
        long second = ((System.nanoTime() / 1000000L) - Gun.getInstance().startTime) / 1000L;
        long minute = second / 60L;
        long hour = minute / 60L;

        switch (getArgument("format").getValue())
        {
            case "second":
                runtime = String.format("%s seconds", second);
                break;

            case "minute":
                runtime = String.format("%s minutes", minute);
                break;

            case "hour":
                runtime = String.format("%s hours", hour);
                break;

            default:
                return "Invalid time format, use second, minute, hour.";
        }

        return String.format("You've been playing for &e%s&7.", runtime);
    }
}
