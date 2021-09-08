package me.comu.client.module;

import com.google.gson.*;
import me.comu.api.interfaces.Toggleable;
import me.comu.api.registry.ListRegistry;
import me.comu.client.config.Config;
import me.comu.client.core.Gun;
import me.comu.client.module.impl.active.combat.AntiAim;
import me.comu.client.module.impl.active.render.Render;
import me.comu.client.module.impl.active.render.TextGUI;
import me.comu.client.module.impl.toggle.combat.*;
import me.comu.client.module.impl.toggle.exploits.*;
import me.comu.client.module.impl.toggle.miscellaneous.*;
import me.comu.client.module.impl.toggle.movement.*;
import me.comu.client.module.impl.toggle.render.*;
import me.comu.client.module.impl.toggle.world.*;
import org.lwjgl.input.Keyboard;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;


public final class ModuleManager extends ListRegistry<Module> {
    public ModuleManager() {
        registry = new CopyOnWriteArrayList<>();

        register(new TextGUI());
        register(new KillAura());
        register(new AntiAim());
        register(new Fullbright());
        register(new Criticals());
        register(new NoFall());
        register(new PotionSaver2());
        register(new ClickAimbot());
        register(new LongJump());
        register(new Glide());
        register(new Flight());
        register(new Zoot());
        register(new BowAimbot());
        register(new Search());
        register(new InfiniteDurability());
        register(new Regen());
        register(new Speedmine());
        register(new Sprint2());
        register(new Step());
        register(new NameProtect());
        register(new MiddleClickFriends());
        register(new FastPlace());
        register(new NameTags());
        register(new AutoHeal());
        register(new AutoHeal2());
        register(new Jesus());
        register(new InventoryWalk());
        register(new AutoArmor());
        register(new AntiCommand());
        register(new AutoAccept());
        register(new Speed());
        register(new ClickGui());
        register(new FastLadder());
        register(new Blink());
        register(new SafeWalk());
        register(new TabGui());
        register(new Chest());
        register(new XCarry());
        register(new Phase());
        register(new Freecam());
        register(new AntiVelocity());
        register(new NoHunger());
        register(new NoSlow());
        register(new Tracers());
        register(new FastUse());
        register(new Sneak());
        register(new StorageESP());
        register(new Render());
        register(new Chams());
        register(new AutoClicker());
        register(new Seeker());
        register(new WorldeditESP());
        register(new SkinFlash());
        register(new Trails());
        register(new Waypoints());
        register(new Avoid());
        register(new AutoFarm());
//        register(new ItemSpoof());
        register(new QuakeAimbot());
        register(new CivBreak());
        register(new Respawn());
        register(new Scaffold());
        register(new InventoryCleaner());
        register(new AntiBot());
        register(new Spammer());
        register(new Derp());
        register(new Trajectories());
        register(new AutoEat());
        register(new AntiCactus());
        register(new Refill());
        register(new AutoSell());
        register(new MLG());
      register(new AutoWalk());
        register(new FastBow());
        register(new AntiWatermark());
        register(new SmoothAim());
        register(new CMDBot());
        register(new Smasher());
        register(new Nuker());
        register(new ToggleSounds());
        //register(new Panic());
//        register(new Teleport());
        register(new HighJump());
        register(new FakeHack());
        register(new FakeAura());
          register(new StackUp());
        register(new BowBoost());
        register(new Triggerbot());
        //    register(new DevGui());
        register(new AutoTool());
        // register(new ClickPearl()); (messes up packets)
        register(new Control());
        register(new PacketFly());
        register(new AntiAFK());
        register(new ViewClip());
        register(new ClickTp());
        //register(new HypixelFly());
        register(new VanillaFly());
        register(new InfoTags());
        register(new WorldTime());
        register(new Tower());
        register(new ChangeTags2());
        register(new Fucker());
        register(new JoinAlerts());
        register(new AutoBuild());
        register(new AutoMine());
        register(new SkidAura());
//         register(new PingSpoof());
        register(new Reach());
        register(new LeetSpeak());
           register(new Dolphin());
        //TODO below this = finish comu
        register(new Radar());
        register(new AntiDebuff());
        register(new Notebot());
        // register(new Scorch());
        register(new AntiVanish());
//         register(new Murder());
         register(new NoRender());
         register(new ClientCapes());
         register(new AutoSign());
         register(new ChatTimestamps());
         register(new Graph());
         register(new AutoLog());

        registry.sort((mod1, mod2) -> mod1.getLabel().compareTo(mod2.getLabel()));
        Gun.getInstance().getKeybindManager().getKeybindByLabel("Click Gui").setKey(Keyboard.KEY_RSHIFT);
        new Config("module_configurations.json") {
            @Override
            public void load(Object... source) {
                try {
                    if (!getFile().exists()) {
                        getFile().createNewFile();
                    }
                } catch (IOException exception) {
                    exception.printStackTrace();
                }

                File modDirectory = new File(Gun.getInstance().getDirectory(), "modules");

                if (!modDirectory.exists()) {
                    modDirectory.mkdir();
                }

                Gun.getInstance().getModuleManager().getRegistry().forEach(mod ->
                {
                    File file = new File(modDirectory, mod.getLabel().toLowerCase().replaceAll(" ", "") + ".json");

                    if (!file.exists()) {
                        return;
                    }
                    try {
                        FileReader reader = new FileReader(file);
                        Throwable throwable = null;

                        try {
                            JsonElement node = new JsonParser().parse(reader);

                            if (!node.isJsonObject()) {
                                return;
                            }
                            mod.loadConfig(node.getAsJsonObject());
                        } catch (Throwable node) {
                            throwable = node;
                            throw node;
                        } finally {
                            if (throwable != null) {
                                try {
                                    reader.close();
                                } catch (Throwable var6_9) {
                                    throwable.addSuppressed(var6_9);
                                }
                            } else {
                                reader.close();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                loadConfig();
            }

            @Override
            public void save(Object... destination) {
                try {
                    if (!getFile().exists()) {
                        getFile().createNewFile();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (!getFile().exists()) {
                    return;
                }

                Gun.getInstance().getModuleManager().getRegistry().forEach(Module::saveConfig);
                saveConfig();
            }

            private void loadConfig() {
                File modsFile = new File(getFile().getAbsolutePath());

                if (!modsFile.exists()) {
                    return;
                }

                JsonElement root;

                try (FileReader reader = new FileReader(modsFile)) {
                    root = new JsonParser().parse(reader);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }

                if (!(root instanceof JsonArray)) {
                    return;
                }

                JsonArray mods = (JsonArray) root;
                mods.forEach(node ->
                {
                    if (!(node instanceof JsonObject))
                        return;
                    try {
                        JsonObject modNode = (JsonObject) node;
                        Gun.getInstance().getModuleManager().getRegistry().forEach(mod -> {
                            if (mod.getLabel().equalsIgnoreCase(modNode.get("module-label").getAsString())) {
                                if (mod instanceof Toggleable) {
                                    ToggleableModule toggleableModule = (ToggleableModule) mod;

                                    if (modNode.get("module-state").getAsBoolean()) {
                                        toggleableModule.setRunning(true);
                                    }

                                    toggleableModule.setDrawn(modNode.get("module-drawn").getAsBoolean());
                                    Gun.getInstance().getKeybindManager().getKeybindByLabel(toggleableModule.getLabel()).setKey(modNode.get("module-keybind").getAsInt());
                                }
                                mod.setTag(modNode.get("module-tag").getAsString());
                            }
                        });
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                });
            }

            private void saveConfig() {
                File modsFile = new File(getFile().getAbsolutePath());

                if (modsFile.exists()) {
                    modsFile.delete();
                }

                if (Gun.getInstance().getModuleManager().getRegistry().isEmpty()) {
                    return;
                }

                JsonArray mods = new JsonArray();
                Gun.getInstance().getModuleManager().getRegistry().forEach(mod ->
                {
                    try {
                        JsonObject modObject = new JsonObject();
                        modObject.addProperty("module-label", mod.getLabel());

                        if (mod instanceof Toggleable) {
                            ToggleableModule toggleableModule = (ToggleableModule) mod;
                            modObject.addProperty("module-state", toggleableModule.isRunning());
                            modObject.addProperty("module-drawn", toggleableModule.isDrawn());
                            modObject.addProperty("module-keybind", Gun.getInstance().getKeybindManager().getKeybindByLabel(toggleableModule.getLabel()).getKey());
                        }
                        modObject.addProperty("module-tag", mod.getTag());
                        mods.add(modObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                try (FileWriter writer = new FileWriter(modsFile)) {
                    writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(mods));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public Module getModuleByAlias(String alias) {
        for (Module module : registry) {
            for (String moduleAlias : module.getAliases()) {
                if (alias.equalsIgnoreCase(moduleAlias)) {
                    return module;
                }
            }
        }

        return null;
    }
}
