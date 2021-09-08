package me.comu.client.events;

import me.comu.api.event.Event;
import net.minecraft.tileentity.TileEntitySign;

public class SignEvent extends Event {

    private TileEntitySign sign;

    public SignEvent(TileEntitySign sign) {
        this.sign = sign;
    }

    public TileEntitySign getSign() {
        return sign;
    }

    public void setSign(TileEntitySign sign) {
        this.sign = sign;
    }
}
