package me.comu.client.module.impl.toggle.miscellaneous;

import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.Property;

public final class AntiWatermark extends ToggleableModule
{
    private final Property<Boolean> p2h = new Property<>(false, "p2h", "/p2h", "p2", "plot", "/p", "p", "plot2h");
    public AntiWatermark()
    {
        super("AntiWatermark", new String[] {"AntiWatermark","watermark","antiwater","AntiHCF", "Anti-HCF", "hcf","ahcf"}, ModuleType.MISCELLANEOUS);
        this.offerProperties(p2h);
    }

    public void onEnable() {
        if (p2h.getValue() && minecraft.theWorld != null) {
            minecraft.thePlayer.sendChatMessage("/p2 h");
        }
    }
}
