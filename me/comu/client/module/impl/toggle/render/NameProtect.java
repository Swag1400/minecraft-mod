package me.comu.client.module.impl.toggle.render;

import me.comu.api.event.Listener;
import me.comu.client.core.Gun;
import me.comu.client.events.ShowMessageEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.EnumProperty;
import me.comu.client.properties.Property;
import net.minecraft.util.EnumChatFormatting;

public final class NameProtect extends ToggleableModule
{
    private final EnumProperty<EnumChatFormatting> color = new EnumProperty<>(EnumChatFormatting.DARK_AQUA, "Color", "c");
    private final Property<Boolean> legit = new Property<>(false, "Legit", "fake","fakehack","fakehacker");
    private final Property<Boolean> hideign = new Property<>(false, "Hide-IGN", "hideign","ign","hidename","name");

    public NameProtect()
    {
        super("NameProtect", new String[] {"nameprotect", "protect", "np","nameprot"}, ModuleType.RENDER);
        this.offerProperties(color, legit);
        this.listeners.add(new Listener<ShowMessageEvent>("name_protect_show_message_listener")
        {
            @Override
            public void call(ShowMessageEvent event)
            {
                Gun.getInstance().getFriendManager().getRegistry().forEach(friend ->
                {
                    if (event.getMessage().contains(friend.getLabel()))
                    {

                        String message = event.getMessage().replace(friend.getLabel(), String.format("%s%s%s", color.getValue(), friend.getAlias(), EnumChatFormatting.RESET));
                        event.setMessage(message);
                    }
                  if (legit.getValue() && event.getMessage().contains(friend.getLabel())) {
                    	 String message = event.getMessage().replace(friend.getLabel(), String.format("%s%s%s", EnumChatFormatting.WHITE, friend.getAlias(), EnumChatFormatting.RESET));
                    	event.setMessage(message);
                    	}
                });
            }
        });
        setRunning(true);
    }
}
