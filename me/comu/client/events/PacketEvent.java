package me.comu.client.events;

import me.comu.api.event.Event;
import net.minecraft.network.Packet;

public class PacketEvent extends Event {
    private Packet packet;

    private Status status;

    public PacketEvent(Packet packet, Status status) {
        this.packet = packet;
        this.status = status;
    }

    public Packet getPacket() {
        return packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public enum Status
    {
        INCOMING, OUTGOING, UNKNOWN
    }

}


