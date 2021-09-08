package me.comu.client.command.impl.client;

import me.comu.client.command.Argument;
import me.comu.client.command.Command;
import me.comu.client.core.Gun;
import org.lwjgl.input.Keyboard;

public final class Macro
{
    public static final class MacroAdd extends Command
    {
        public MacroAdd()
        {
            super(new String[] {"macro","mac","macroadd", "madd","macadd","newmacro","newmac","addmacro","addmac"}, new Argument("key"), new Argument("action"));
        }

        @Override
        public String dispatch()
        {
            String key = (getArgument("key").getValue()).toUpperCase();
            String action = getArgument("action").getValue().replaceAll("_", " ");
            int keyValue = Keyboard.getKeyIndex(key);
            if (Gun.getInstance().getMacroManager().isMacro(keyValue))
            {
                return "A macro correlated with &e\"" + Keyboard.getKeyName(keyValue).toUpperCase() + "\" &7already exists.";
            }
            Gun.getInstance().getMacroManager().register(new me.comu.client.macro.Macro(keyValue, action));
            return "&e\"" + Keyboard.getKeyName(keyValue).toUpperCase() + "\" &7Macro created. (\"" + action + "\")";

        }
    }

    public static final class MacroRemove extends Command
    {
        public MacroRemove()
        {
            super(new String[] {"remmacro", "delmacro","removemacro","delmac","deletemacro","macrodel","macroremove"}, new Argument("key"));
        }

        @Override
        public String dispatch()
        {
            String key = getArgument("key").getValue().toUpperCase();

            me.comu.client.macro.Macro macro = Gun.getInstance().getMacroManager().getUsingKey(Keyboard.getKeyIndex(key));
            if (macro != null)
            {
                Gun.getInstance().getMacroManager().remove(macro.getKey());
                return "Removed Macro &e\"" + Keyboard.getKeyName(macro.getKey()).toUpperCase() + "\" (\"" + macro.getAction().getAction() + "\")&7.";
            }
            return "There is no macro correlated with &e\"" + key.toUpperCase() + "\"&7.";
        }
    }
    public static final class MacroList extends Command
    {
        public MacroList()
        {
            super(new String[] {"macrolist", "listmacros","listmacro","mlist"});
        }

        @Override
        public String dispatch()
        {
            StringBuilder stringBuilder = new StringBuilder("Macros (" + Gun.getInstance().getMacroManager().getElements().size() + ")\n");
            for (me.comu.client.macro.Macro macro : Gun.getInstance().getMacroManager().getElements())
            {
                stringBuilder.append(String.format("&e\"%s\"&7 - %s%s", Keyboard.getKeyName(macro.getKey()).toUpperCase(), macro.getAction().getAction(), "\n"));
            }

            return stringBuilder.toString();
        }
    }
    public static final class MacroReset extends Command
    {
        public MacroReset()
        {
            super(new String[] {"resetmacros", "macroreset","delmacros","purgemacros","purgemacro","macrosdel","macropurge","macropurge","purgemacro"});
        }

        @Override
        public String dispatch()
        {
            int size = Gun.getInstance().getMacroManager().getElements().size();
            Gun.getInstance().getMacroManager().getElements().clear();
           return String.format("&e%s &7macros were removed.", size);

        }
    }

}
