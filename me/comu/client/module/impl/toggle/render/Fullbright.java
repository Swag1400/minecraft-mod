package me.comu.client.module.impl.toggle.render;

import me.comu.api.event.Listener;
import me.comu.client.events.GammaSettingEvent;
import me.comu.client.events.NightVisionEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.EnumProperty;

public final class Fullbright extends ToggleableModule
{
    private final EnumProperty<Mode> mode = new EnumProperty<>(Mode.POTION, "Mode", "m");

    public Fullbright()
    {
        super("Fullbright", new String[] {"fullbright", "bright", "brightness", "fb"}, 0xFFDBE300, ModuleType.RENDER);
        this.offerProperties(mode);
        this.listeners.add(new Listener<GammaSettingEvent>("brightness_gamma_setting_listener")
        {
            @Override
            public void call(GammaSettingEvent event)
            {
                if (mode.getValue() == Mode.GAMMA)
                {
                    event.setGammaSetting(1000F);
                }
            }
        });
        this.listeners.add(new Listener<NightVisionEvent>("brightness_night_vision_listener")
        {
            @Override
            public void call(NightVisionEvent event)
            {
                if (mode.getValue() == Mode.POTION)
                {
                    event.setCanceled(true);
                }
            }
        });
//        this.listeners.add(new Listener<PacketEvent>("brightness_night_vision_listener")
//		{
//        	@Override
//            public void call(PacketEvent event)
//            {
//               if (event.getPacket() instanceof S03PacketTimeUpdate) {
//            	   event.setCanceled(true);
//               }
//             
//            }
//        		});
//		
//        this.listeners.add(new Listener<UpdateEvent>("brightness_night_vision_listener")
//        		{
//        	@Override
//            public void call(UpdateEvent event)
//            {
//                if (mode.getValue() == Mode.NIGHT)
//                {
//                   minecraft.theWorld.setWorldTime(13000);
//                } 
//             
//            }
//        		});
        		
    }

    public enum Mode
    {
        GAMMA, POTION
        }
}
