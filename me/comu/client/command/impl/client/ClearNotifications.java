package me.comu.client.command.impl.client;

import me.comu.client.command.Command;
import me.comu.client.notification.NotificationManager;

/**
 * Created by comu on 09/21/19
 */

public final class ClearNotifications extends Command
{
    public ClearNotifications()
    {
        super(new String[] {"clearnotifications","cnotifs","clearnotifs","resetnotifications","resetnotifs","clearnotif","cf","notifsclear"});
    }

    @Override
    public String dispatch() {
        int size = NotificationManager.getQueuedNotificationList().size();
        NotificationManager.getQueuedNotificationList().clear();
        return "Notification Queue Cleared. (" + size + ")";


    }
}
