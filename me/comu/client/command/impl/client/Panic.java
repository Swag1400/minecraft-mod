package me.comu.client.command.impl.client;

import me.comu.client.command.Command;
import me.comu.client.core.Gun;
import me.comu.client.module.Module;
import me.comu.client.module.ToggleableModule;
import me.comu.client.module.impl.active.render.Render;
import me.comu.client.module.impl.active.render.TextGUI;
import me.comu.client.properties.Property;


public final class Panic extends Command {
    public Panic() {
        super(new String[]{"panic", "panick", "panik"});
    }

    @Override
    public String dispatch() {
        for (Module module : Gun.getInstance().getModuleManager().getRegistry()) {
            if (module instanceof ToggleableModule) {
                if (((ToggleableModule) module).isRunning())
                    ((ToggleableModule) module).toggle();
            }
        }
        TextGUI textGUI = (TextGUI) Gun.getInstance().getModuleManager().getModuleByAlias("textgui");
        Render render = (Render) Gun.getInstance().getModuleManager().getModuleByAlias("render");
        Property<Boolean> notifications = render.getPropertyByAlias("notifications");
        Property<Boolean> blockpos = render.getPropertyByAlias("blockpos");
        Property<Boolean> itemPhysics = render.getPropertyByAlias("itemphysics");
        Property<Boolean> arrayList = textGUI.getPropertyByAlias("arraylist");
        Property<Boolean> armor = textGUI.getPropertyByAlias("armor");
        Property<Boolean> fps = textGUI.getPropertyByAlias("fps");
        Property<Boolean> ping = textGUI.getPropertyByAlias("ping");
        Property<Boolean> direction = textGUI.getPropertyByAlias("direction");
        Property<Boolean> time = textGUI.getPropertyByAlias("time");
        Property<Boolean> watermark = textGUI.getPropertyByAlias("watermark");
        Property<Boolean> potions = textGUI.getPropertyByAlias("potions");
        Property<Boolean> coords = textGUI.getPropertyByAlias("coords");
        Property<Boolean> bps = textGUI.getPropertyByAlias("bps");
        Property<Boolean> itemdura = textGUI.getPropertyByAlias("item-dura");
        Property<Boolean> healthoverlay = textGUI.getPropertyByAlias("health-overlay");
        Property<Boolean> tps = textGUI.getPropertyByAlias("tps");
        Property<Boolean> brand = textGUI.getPropertyByAlias("serverbrand");
        blockpos.setValue(false);
        itemPhysics.setValue(false);
        notifications.setValue(false);
        arrayList.setValue(false);
        armor.setValue(false);
        fps.setValue(false);
        ping.setValue(false);
        direction.setValue(false);
        watermark.setValue(false);
        potions.setValue(false);
        coords.setValue(false);
        bps.setValue(false);
        itemdura.setValue(false);
        healthoverlay.setValue(false);
        time.setValue(false);
        tps.setValue(false);
        brand.setValue(false);
        return "Good luck with the screen-share";
    }
}
