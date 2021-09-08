package me.comu.client.command.impl.client;

import me.comu.client.command.Command;
import me.comu.client.core.Gun;
import me.comu.client.logging.Logger;

public final class Save extends Command {

    public Save() {
        super(new String[]{"save", "saveconfig", "saveconfigs", "forcesave"});
    }

    @Override
    public String dispatch() {
        float preTime = System.currentTimeMillis();
        Gun.getInstance().getConfigManager().getRegistry().forEach(config -> {
            Logger.getLogger().printToChat("\2477Attempting to save Config: \247e" + config.getLabel() + "\2477");
            config.save();
        });
        return "Successfully finished force-saving configs. (" + (System.currentTimeMillis() - preTime) + "ms)";
    }

}


