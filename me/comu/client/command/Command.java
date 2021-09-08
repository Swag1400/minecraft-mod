package me.comu.client.command;

import net.minecraft.client.Minecraft;

import java.util.StringJoiner;

public abstract class Command
{
    private final String[] aliases;
    private final Argument[] arguments;

    protected Minecraft minecraft = Minecraft.getMinecraft();

    public Command(String[] aliases, Argument... arguments)
    {
        this.aliases = aliases;
        this.arguments = arguments;
    }

    public String dispatch(String[] input)
    {
        Argument[] arguments = getArguments();
        boolean valid = false;

        if (input.length < arguments.length)
        {
            return String.format("%s %s", input[0], getSyntax());
        }
        else if ((input.length - 1) > arguments.length)
        {
            return String.format("Maximum number of arguments is &e%s&7.", arguments.length);
        }

        if (arguments.length > 0)
        {
            for (int index = 0; index < arguments.length; index++)
            {
                Argument argument = arguments[index];
                argument.setPresent(index < input.length);
                argument.setValue(input[index + 1]);
                valid = argument.isPresent();
            }
        }
        else
        {
            valid = true;
        }

        return valid ? dispatch() : "Invalid argument(s).";
    }

    public final String[] getAliases()
    {
        return aliases;
    }

    public final Argument[] getArguments()
    {
        return arguments;
    }

    public Argument getArgument(String label)
    {
        for (Argument argument : arguments)
        {
            if (label.equalsIgnoreCase(argument.getLabel()))
            {
                return argument;
            }
        }

        return null;
    }

    public String getSyntax()
    {
        StringJoiner stringJoiner = new StringJoiner(" ");

        for (Argument argument : arguments)
        {
            stringJoiner.add(String.format("&e[%s]", argument.getLabel()));
        }

        return stringJoiner.toString();
    }

    public abstract String dispatch();
}
