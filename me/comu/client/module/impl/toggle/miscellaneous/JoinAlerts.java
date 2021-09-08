package me.comu.client.module.impl.toggle.miscellaneous;

import me.comu.api.event.Listener;
import me.comu.api.stopwatch.Stopwatch;
import me.comu.client.core.Gun;
import me.comu.client.events.PacketEvent;
import me.comu.client.logging.Logger;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.notification.Notification;
import me.comu.client.notification.NotificationManager;
import me.comu.client.notification.NotificationType;
import net.minecraft.network.play.server.S38PacketPlayerListItem;

public final class JoinAlerts extends ToggleableModule {
    private final Stopwatch stopwatch = new Stopwatch();

    public JoinAlerts() {
        super("JoinAlerts", new String[]{"joinalerts", "joinalert", "alertjoin"}, ModuleType.MISCELLANEOUS);
        this.listeners.add(new Listener<PacketEvent>("auto_accept_packet_listener") {
            @Override
            public void call(PacketEvent event) {
                if (event.getPacket() instanceof S38PacketPlayerListItem) {
                        final S38PacketPlayerListItem packet = (S38PacketPlayerListItem)event.getPacket();
                        if (packet.func_179768_b() == S38PacketPlayerListItem.Action.ADD_PLAYER) {
                            for (final Object o : packet.func_179767_a()) {
                                final S38PacketPlayerListItem.AddPlayerData data = (S38PacketPlayerListItem.AddPlayerData)o;
                                if (Gun.getInstance().getFriendManager().isFriend(data.field_179964_d.getName())) {
                                    NotificationManager.notify(new Notification(NotificationType.INFO, "Alerts",data.field_179964_d.getName() + " joined.", 5));
                                    Logger.getLogger().printToChat("\2473" + data.field_179964_d.getName() + "\2477 has joined the server.");
                                }
                                if (Gun.getInstance().getEnemyManager().isEnemy(data.field_179964_d.getName())) {
                                    NotificationManager.notify(new Notification(NotificationType.INFO, "Alerts",data.field_179964_d.getName() + " joined.", 5));
                                    Logger.getLogger().printToChat("\2474" + data.field_179964_d.getName() + "\2477 has joined the server.");
                                }
                                if (Gun.getInstance().getStaffManager().isStaff(data.field_179964_d.getName())) {
                                    NotificationManager.notify(new Notification(NotificationType.INFO, "\247n" + getLabel(),data.field_179964_d.getName() + " joined.", 5));
                                    Logger.getLogger().printToChat("\2478"+data.field_179964_d.getName() + "\2477 has joined the server.");
                                }
                            }
                        }

                }
                }
            });
    }
}
