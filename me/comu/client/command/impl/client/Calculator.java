package me.comu.client.command.impl.client;

import me.comu.client.command.Argument;
import me.comu.client.command.Command;



public final class Calculator extends Command
{
    public Calculator()
    {
        super(new String[] {"calculator","calc"}, new Argument("expression"));
    }

    @Override
    public String dispatch() {
        String expression = (getArgument("expression").getValue());

        return "";
    }


    static boolean isDigit(char check) {
        if (Character.isDigit(check)) {
            return true;
        }
        return false;
    }

    public enum OPERATION
    {
        ADD, SUBTRACT, MULTIPLY, DIVIDE, POWER
    }

}
