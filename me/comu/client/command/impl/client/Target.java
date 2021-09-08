package me.comu.client.command.impl.client;

import me.comu.client.command.Argument;
import me.comu.client.command.Command;
import me.comu.client.core.Gun;
import me.comu.client.module.impl.toggle.combat.KillAura;
import me.comu.client.module.impl.toggle.combat.SmoothAim;
import me.comu.client.utils.Helper;

/**
 * Created by august on 12/11/2018
 */
public final class Target extends Command {


    public Target() {
        super(new String[]{"target", "tar", "targ", "focus", "foc"}, new Argument("target"));
    }

    @Override
    public String dispatch() {
        String target = getArgument("target").getValue();
        KillAura ka = (KillAura) Gun.getInstance().getModuleManager().getModuleByAlias("killaura");
        SmoothAim sa = (SmoothAim) Gun.getInstance().getModuleManager().getModuleByAlias("smoothaim");
        if (target.equalsIgnoreCase("clear") || target.equalsIgnoreCase("c")) {
            ka.focusTarget = null;
            sa.focusedTarget = null;
            return "KillAura target cleared!";
        }
        if (Helper.world().getPlayerEntityByName(target) != null) {
            ka.focusTarget = Helper.world().getPlayerEntityByName(target);
            sa.focusedTarget = Helper.world().getPlayerEntityByName(target);
        } else {
            return "Player not found!";
        }


        return "KillAura focused on &e" + target + "&7.";
    }
}
