package me.comu.client.module.impl.toggle.render;

import me.comu.api.event.Listener;
import me.comu.client.command.Argument;
import me.comu.client.command.Command;
import me.comu.client.core.Gun;
import me.comu.client.events.PassSpecialRenderEvent;
import me.comu.client.events.RenderEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class ChangeTags extends ToggleableModule {

    String playerName;
    String newName;
    public ChangeTags() {
        super("ChangeTags", new String[]{"changetags"}, 0x54FF33, ModuleType.RENDER);
        this.listeners.add(new Listener<RenderEvent>("change_tags_render_event") {
            @Override
            public void call(RenderEvent event) {
                Gun.getInstance().getCommandManager().register(new Command(new String[] {"changetag","ctag","ctags","ct","chagename"}, new Argument("IGN"), new Argument("name"))
                {
                    @Override
                    public String dispatch()
                    {
                        String playerNameCom = getArgument("IGN").getValue();
                        String newNameCom = getArgument("name").getValue();
                        playerName = playerNameCom;
                        newName = newNameCom;
                        return "Changed " + playerNameCom + " to " + newNameCom + ".";
                    }
                });
                for (Object o : minecraft.theWorld.playerEntities) {
                    Entity entity = (Entity) o;
                    if (entity instanceof EntityPlayer) {
                        if (entity.getName().equalsIgnoreCase(playerName)) {
                            setDisplayName(((EntityPlayer) entity), newName);
                        }
                    }
                }
            }
        });
        this.listeners.add(new Listener<PassSpecialRenderEvent>("name_tags_pass_special_render_listener") {
            @Override
            public void call(PassSpecialRenderEvent event) {
                event.setCanceled(true);
            }
        });
        setRunning(true);
    }
    public static String setDisplayName(EntityPlayer player, String newName) {
        String name = player.getDisplayName().getFormattedText();
        name = newName;
        return name;
    }


}
