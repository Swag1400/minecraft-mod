package me.comu.client.core;

import me.comu.api.event.basic.BasicEventManager;
import me.comu.client.command.CommandManager;
import me.comu.client.config.ConfigManager;
import me.comu.client.enemy.EnemyManager;
import me.comu.client.friend.FriendManager;
import me.comu.client.gui.screens.accountmanager.AccountManager;
import me.comu.client.keybind.KeybindManager;
import me.comu.client.logging.Logger;
import me.comu.client.macro.MacroManager;
import me.comu.client.module.ModuleManager;
import me.comu.client.notification.NotificationManager;
import me.comu.client.plugin.PluginManager;
import me.comu.client.staff.StaffManager;
import me.comu.client.utils.HWIDUtils;
import me.comu.client.utils.WebUtils;
import me.comu.client.waypoints.WaypointManager;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.Display;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

//import me.comu.client.macro.MacroManager;


// TODO:

// worldtime
// cheststealer drop and store
// make fake name tag hacker using getdisplayname from nametags
// fix antivanish notifications to make new lines for certain number of chars
// add modes for distance/health colors for esp
// add tower to scaffold
// nameprotect for scorreboard and shit

public final class Gun {

    private static Gun instance = null;
    public static String TITLE = "Client"; // flax xd! 10/26/17
    public static int BUILD = 1;
    public static final String AUTHORS = "Comu, Gringo, Tarik, Friendly, Nuf";
    private static final String HWIDURL = "https://pastebin.com/raw/V91R2VbQ";

    public static long startTime = System.nanoTime() / 1000000L;

    private final BasicEventManager eventManager;
    private final KeybindManager keybindManager;
    private final ModuleManager moduleManager;
    private final CommandManager commandManager;
    private final FriendManager friendManager;
    private final ConfigManager configManager;
    private final AccountManager accountManager;
    private final MacroManager macroManager;
    private final WaypointManager wayPointManager;
    private final PluginManager pluginManager;
    private final EnemyManager enemyManager;
    private final StaffManager staffManager;
    private final NotificationManager notificationManager;
    private final File directory;

    public Gun() {
        startTime = System.nanoTime() / 1000000L;
        Logger.getLogger().print("Initializing...");
        instance = this;
        directory = new File(System.getProperty("user.home"), "clarinet");

        if (!directory.exists()) {
            Logger.getLogger().print(String.format("%s client directory.", directory.mkdir() ? "Created" : "Failed to create"));
        }

        eventManager = new BasicEventManager();
        configManager = new ConfigManager();
        friendManager = new FriendManager();
        macroManager = new MacroManager();
        wayPointManager = new WaypointManager();
        enemyManager = new EnemyManager();
        staffManager = new StaffManager();
        keybindManager = new KeybindManager();
        commandManager = new CommandManager();
        moduleManager = new ModuleManager();
        accountManager = new AccountManager();
        pluginManager = new PluginManager();
        notificationManager = new NotificationManager();
        getConfigManager().getRegistry().forEach(config -> config.load());

        try {
            pluginManager.onLoad();
            System.out.println("Plugin manager started.");
            System.out.println(pluginManager.getList() + "has been loaded.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Runtime.getRuntime().addShutdownHook(new Thread("Shutdown Hook Thread") {
            @Override
            public void run() {
                Logger.getLogger().print("Shutting down...");
                getConfigManager().getRegistry().forEach(config -> config.save());
                Logger.getLogger().print("Shutdown.");
            }
        });
        Display.setTitle("Minecraft 1.8");
        try {
            if (!WebUtils.get(HWIDURL).contains(HWIDUtils.getHWID())) {
                Logger.getLogger().print("Unauthorized Access! Shutting Down");
                Minecraft.getMinecraft().shutdownMinecraftApplet();
            } else {
                Logger.getLogger().print("Access Authorized!");
                Logger.getLogger().print(String.format("Initialized, took %s milliseconds.", ((System.nanoTime() / 1000000L) - startTime)));
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            Logger.getLogger().print("Caught Exception! Shutting Down");
            Minecraft.getMinecraft().shutdownMinecraftApplet();
        }
    }

    public static Gun getInstance() {
        return instance;
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public KeybindManager getKeybindManager() {
        return keybindManager;
    }

    public NotificationManager getNotificationManager() {
        return notificationManager;
    }

    public FriendManager getFriendManager() {
        return friendManager;
    }

    public EnemyManager getEnemyManager() {
        return enemyManager;
    }

    public final MacroManager getMacroManager() {
        return macroManager;
    }

    public WaypointManager getWaypointManager() {
        return wayPointManager;
    }

    public StaffManager getStaffManager() {
        return staffManager;
    }

    public BasicEventManager getEventManager() {
        return eventManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public AccountManager getAccountManager() {
        return accountManager;
    }

    public PluginManager getPluginManager() {
        return pluginManager;
    }

    public File getDirectory() {
        return directory;
    }
}