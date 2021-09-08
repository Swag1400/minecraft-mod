package me.comu.client.notification;

import java.util.concurrent.LinkedBlockingQueue;

public class NotificationManager {

    private static LinkedBlockingQueue<Notification> pendingNotifications = new LinkedBlockingQueue<>();
    private static Notification currentNotification = null;

    public static void notify(Notification notification) {
        pendingNotifications.add(notification);
    }

    public static void update() {
        if (currentNotification != null && !currentNotification.isShown()) {
        currentNotification = null;
        }

        if (currentNotification == null && !pendingNotifications.isEmpty()) {
            currentNotification = pendingNotifications.poll();
            currentNotification.Show();
        }

    }

    public static void render() {
        update();
        if (currentNotification != null) {
            currentNotification.render();
        }
    }

    public static LinkedBlockingQueue<Notification> getQueuedNotificationList() {
        return pendingNotifications;
    }

}
