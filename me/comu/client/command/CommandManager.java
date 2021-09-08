package me.comu.client.command;


import me.comu.api.event.Listener;
import me.comu.api.registry.ListRegistry;
import me.comu.client.command.impl.client.Runtime;
import me.comu.client.command.impl.client.*;
import me.comu.client.command.impl.player.*;
import me.comu.client.command.impl.server.*;
import me.comu.client.config.Config;
import me.comu.client.core.Gun;
import me.comu.client.events.PacketEvent;
import me.comu.client.logging.Logger;
import me.comu.client.module.Module;
import me.comu.client.properties.EnumProperty;
import me.comu.client.properties.Property;
import net.minecraft.network.play.client.C01PacketChatMessage;

import java.io.*;
import java.util.ArrayList;
import java.util.StringJoiner;


public final class CommandManager extends ListRegistry<Command>
{
    public static String prefix = ".";

    public CommandManager()
    {
        registry = new ArrayList<>();
        register(new Toggle());
        register(new Runtime());
        register(new Grab());
        register(new Help());
        register(new Modules());
        register(new Breed());
        register(new Prefix());
        register(new Connect());
        register(new Crash());
        register(new StackSize());
        register(new Damage());
        register(new Presets());
        register(new HClip());
        register(new Drown());
        register(new VClip());
        register(new Friends.Add());
        register(new Friends.Remove());
        register(new Enemies.EnemyAdd());
        register(new Enemies.EnemyRemove());
        register(new StaffCom.StaffAdd());
        register(new StaffCom.StaffRemove());
        register(new Bind());
        register(new Ban());
        register(new ScreenShot());
        register(new History());
        register(new PClip());
        register(new Suicide());
        register(new Invsee());
        register(new Dupe());
        register(new Scrape());
        register(new DupeHand());
        register(new Unstuck());
        register(new Target());
        register(new IGN());
        register(new GeoIP());
        register(new Calculator());
        register(new CalculatorAdd());
        register(new CalculatorSubtract());
        register(new CalculatorMultiply());
        register(new CalculatorDivide());
        register(new CalculatorPower());
        register(new ClearNotifications());
        register(new Debug());
        register(new SetTitle());
        register(new Visible());
        //register(new Invsee());
      //  register(new Teleport())
          register(new Plugins());
        register(new Disconnect());
        register(new MsgSpam());
        register(new Macro.MacroAdd());
        register(new Macro.MacroRemove());
        register(new Macro.MacroList());
        register(new Macro.MacroReset());
        register(new Panic());
        register(new Rename());
        register(new Save());
        register(new FakePlayer.FakePlayerAdd());
        register(new FakePlayer.FakePlayerRemove());
        registry.sort((cmd1, cmd2) -> cmd1.getAliases()[0].compareTo(cmd2.getAliases()[0]));
        Gun.getInstance().getEventManager().register(new Listener<PacketEvent>("commands_packet_listener")
        {
            @Override
            public void call(final PacketEvent event) {
                if (event.getPacket() instanceof C01PacketChatMessage) {
                    final C01PacketChatMessage packet = (C01PacketChatMessage)event.getPacket();
                    final String message = packet.getMessage().trim();
                    if (message.startsWith(CommandManager.this.getPrefix())) {
                        event.setCanceled(true);
                        boolean exists = false;
                        final String[] arguments = message.split(" ");
                        if (message.length() < 1) {
                            Logger.getLogger().printToChat("No command was entered.");
                            return;
                        }
                        final String execute = message.contains(" ") ? arguments[0] : message;
                        for (final Command command : CommandManager.this.getRegistry()) {
                            String[] aliases;
                            for (int length2 = (aliases = command.getAliases()).length, j = 0; j < length2; ++j) {
                                final String alias = aliases[j];
                                if (execute.replace(CommandManager.this.getPrefix(), "").equalsIgnoreCase(alias.replaceAll(" ", ""))) {
                                    exists = true;
                                    try {
                                        Logger.getLogger().printToChat(command.dispatch(arguments));
                                    }
                                    catch (Exception e2) {
                                        Logger.getLogger().printToChat(String.format("%s%s %s", CommandManager.this.getPrefix(), alias, command.getSyntax()));
                                    }
                                }
                            }
                        }
                        final String[] argz = message.split(" ");
                        for (final Module mod : Gun.getInstance().getModuleManager().getRegistry()) {
                            String[] aliases2;
                            for (int length3 = (aliases2 = mod.getAliases()).length, k = 0; k < length3; ++k) {
                                final String alias2 = aliases2[k];
                                try {
                                    if (argz[0].equalsIgnoreCase(String.valueOf(CommandManager.this.getPrefix()) + alias2.replace(" ", ""))) {
                                        exists = true;
                                        if (argz.length > 1) {
                                            final String valueName = argz[1];
                                            if (argz[1].equalsIgnoreCase("list")) {
                                                if (mod.getProperties().size() > 0) {
                                                    final StringJoiner stringJoiner = new StringJoiner(", ");
                                                    for (final Property property : mod.getProperties()) {
                                                        stringJoiner.add(String.format("%s&e[%s]&7", property.getAliases()[0], (property.getValue() instanceof Enum) ? ((EnumProperty)property).getFixedValue() : property.getValue()));
                                                    }
                                                    Logger.getLogger().printToChat(String.format("Properties (%s) %s.", mod.getProperties().size(), stringJoiner.toString()));
                                                }
                                                else {
                                                    Logger.getLogger().printToChat(String.format("&e%s&7 has no properties.", mod.getLabel()));
                                                }
                                            }
                                            else {
                                                final Property property2 = mod.getPropertyByAlias(valueName);
                                                if (property2 != null) {
                                                    if (property2.getValue() instanceof Number) {
                                                        if (!argz[2].equalsIgnoreCase("get")) {
                                                            if (property2.getValue() instanceof Double) {
                                                                property2.setValue(Double.parseDouble(argz[2]));
                                                            }
                                                            if (property2.getValue() instanceof Integer) {
                                                                property2.setValue(Integer.parseInt(argz[2]));
                                                            }
                                                            if (property2.getValue() instanceof Float) {
                                                                property2.setValue(Float.parseFloat(argz[2]));
                                                            }
                                                            if (property2.getValue() instanceof Long) {
                                                                property2.setValue(Long.parseLong(argz[2]));
                                                            }
                                                            Logger.getLogger().printToChat(String.format("&e%s&7 has been set to &e%s&7 for &e%s&7.", property2.getAliases()[0], property2.getValue(), mod.getLabel()));
                                                        }
                                                        else {
                                                            Logger.getLogger().printToChat(String.format("&e%s&7 current value is &e%s&7 for &e%s&7.", property2.getAliases()[0], property2.getValue(), mod.getLabel()));
                                                        }
                                                    }
                                                    else if (property2.getValue() instanceof Enum) {
                                                        if (!argz[2].equalsIgnoreCase("list")) {
                                                            ((EnumProperty)property2).setValue(argz[2]);
                                                            Logger.getLogger().printToChat(String.format("&e%s&7 has been set to &e%s&7 for &e%s&7.", property2.getAliases()[0], ((EnumProperty)property2).getFixedValue(), mod.getLabel()));
                                                        }
                                                        else {
                                                            final StringJoiner stringJoiner2 = new StringJoiner(", ");
                                                            Enum[] array;
                                                            for (int length = (array = (Enum[])property2.getValue().getClass().getEnumConstants()).length, i = 0; i < length; ++i) {
                                                                stringJoiner2.add(String.format("%s%s&7", array[i].name().equalsIgnoreCase(property2.getValue().toString()) ? "&a" : "&c", CommandManager.this.getFixedValue(array[i])));
                                                            }
                                                            Logger.getLogger().printToChat(String.format("Modes (%s) %s.", array.length, stringJoiner2.toString()));
                                                        }
                                                    }
                                                    else if (property2.getValue() instanceof String) {
                                                        property2.setValue(argz[2]);
                                                        Logger.getLogger().printToChat(String.format("&e%s&7 has been set to &e\"%s\"&7 for &e%s&7.", property2.getAliases()[0], property2.getValue(), mod.getLabel()));
                                                    }
                                                    else if (property2.getValue() instanceof Boolean) {
                                                        property2.setValue(!(Boolean)property2.getValue());
                                                        Logger.getLogger().printToChat(String.format("&e%s&7 has been %s&7 for &e%s&7.", property2.getAliases()[0], (boolean)property2.getValue() ? "&aenabled" : "&cdisabled", mod.getLabel()));
                                                    }
                                                }
                                            }
                                        }
                                        else {
                                            Logger.getLogger().printToChat(String.format("%s &e[list|valuename] [list|get]", argz[0]));
                                        }
                                    }
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        if (!exists) {
                            Logger.getLogger().printToChat("Invalid command entered.");
                        }
                    }
                }
            }
        });
        new Config("command_prefix.txt")
        {
            @Override
            public void load(Object... source)
            {
                try
                {
                    if (!getFile().exists())
                    {
                        getFile().createNewFile();
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                if (!getFile().exists())
                {
                    return;
                }

                try
                {
                    BufferedReader br = new BufferedReader(new FileReader(getFile()));
                    String readLine;

                    while ((readLine = br.readLine()) != null)
                    {
                        try
                        {
                            String[] split = readLine.split(":");
                            prefix = split[0];
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }

                    br.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            @Override
            public void save(Object... destination)
            {
                try
                {
                    if (!getFile().exists())
                    {
                        getFile().createNewFile();
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                try
                {
                    BufferedWriter bw = new BufferedWriter(new FileWriter(getFile()));
                    bw.write(prefix);
                    bw.newLine();
                    bw.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        };
    }

    public String getPrefix()
    {
        return prefix;
    }

    public void setPrefix(String prefix)
    {
        this.prefix = prefix;
    }

    private String getFixedValue(Enum enumd)
    {
        return Character.toString(enumd.name().charAt(0)) + enumd.name().toLowerCase().replace(Character.toString(enumd.name().charAt(0)).toLowerCase(), "");
    }
}
