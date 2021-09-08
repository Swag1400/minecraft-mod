package me.comu.client.module.impl.toggle.miscellaneous;

import me.comu.api.event.Listener;
import me.comu.client.core.Gun;
import me.comu.client.enemy.Enemy;
import me.comu.client.events.InputEvent;
import me.comu.client.friend.Friend;
import me.comu.client.logging.Logger;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.EnumProperty;
import me.comu.client.properties.Property;
import me.comu.client.staff.Staff;
import me.comu.client.notification.Notification;
import me.comu.client.notification.NotificationManager;
import me.comu.client.notification.NotificationType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;

public final class MiddleClickFriends extends ToggleableModule
{

    private final Property<Boolean> commands = new Property<>(true, "Commands", "com","command","c");
    private final EnumProperty<Mode> mode = new EnumProperty<>(Mode.FRIEND, "Mode", "m");

    public MiddleClickFriends()
    {
        super("MiddleClick", new String[] {"middleclick", "mcf", "middleclick","middleclickfriend","middleclickenemy","middleclicstaff"}, 0xFF4D7ED1, ModuleType.MISCELLANEOUS);
        this.offerProperties(commands, mode);
        this.listeners.add(new Listener<InputEvent>("middle_click_input_listener")
        {
            @Override
            public void call(InputEvent event)
            {
                if (event.getType() == InputEvent.Type.MOUSE_MIDDLE_CLICK)
                {
                    if (minecraft.objectMouseOver != null)
                    {
                        if (minecraft.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY)
                        {
                            if (minecraft.objectMouseOver.entityHit instanceof EntityPlayer) {
                                EntityPlayer entityPlayer = (EntityPlayer) minecraft.objectMouseOver.entityHit;
                                switch (mode.getValue()) {
                                    case FRIEND:
                                    if (Gun.getInstance().getFriendManager().isFriend(entityPlayer.getName())) {
                                        Gun.getInstance().getFriendManager().unregister(Gun.getInstance().getFriendManager().getFriendByAliasOrLabel(entityPlayer.getName()));
                                        if (commands.getValue())
                                            Logger.getLogger().printToChat("Removed friend \247e" + entityPlayer.getName() + "\2477.");
                                        NotificationManager.notify(new Notification(NotificationType.INFO, "\247nFriend Removed", entityPlayer.getName() + " removed from friends list", 3));
                                    } else {
                                        Gun.getInstance().getFriendManager().register(new Friend(entityPlayer.getName(), entityPlayer.getName()));
                                        if (commands.getValue())
                                            Logger.getLogger().printToChat("Added friend \247e" + entityPlayer.getName() + "\2477.");
                                         NotificationManager.notify(new Notification(NotificationType.INFO, "\247nFriend Added", entityPlayer.getName() + " added to friends list", 3));
                                    }
                                    break;
                                    case ENEMY:
                                        if (Gun.getInstance().getEnemyManager().isEnemy(entityPlayer.getName())) {
                                            Gun.getInstance().getEnemyManager().unregister(Gun.getInstance().getEnemyManager().getEnemyByAliasOrLabel(entityPlayer.getName()));
                                            if (commands.getValue())
                                                Logger.getLogger().printToChat("Removed enemy \247e" + entityPlayer.getName() + "\2477.");
                                            NotificationManager.notify(new Notification(NotificationType.INFO, "\247nEnemy Removed", entityPlayer.getName() + " removed from enemies list", 3));
                                        } else {
                                            Gun.getInstance().getEnemyManager().register(new Enemy(entityPlayer.getName(), entityPlayer.getName()));
                                            if (commands.getValue())
                                                Logger.getLogger().printToChat("Added enemy \247e" + entityPlayer.getName() + "\2477.");
                                            NotificationManager.notify(new Notification(NotificationType.INFO, "\247nEnemy Added", entityPlayer.getName() + " added to enemies list", 3));
                                        }

                                    case STAFF:
                                        if (Gun.getInstance().getStaffManager().isStaff(entityPlayer.getName())) {
                                            Gun.getInstance().getStaffManager().unregister(Gun.getInstance().getStaffManager().getStaffByAliasOrLabel(entityPlayer.getName()));
                                            if (commands.getValue())
                                                Logger.getLogger().printToChat("Removed staff member \247e" + entityPlayer.getName() + "\2477.");
                                            NotificationManager.notify(new Notification(NotificationType.INFO, "\247nStaff Removed", entityPlayer.getName() + " removed from staff list", 3));
                                        } else {
                                            Gun.getInstance().getStaffManager().register(new Staff(entityPlayer.getName(), entityPlayer.getName()));
                                            if (commands.getValue())
                                                Logger.getLogger().printToChat("Added staff member \2477e" + entityPlayer.getName() + "\2477.");
                                            NotificationManager.notify(new Notification(NotificationType.INFO, "\247nStaff Added", entityPlayer.getName() + " added to staff list", 3));
                                        }
                                        break;
                                }
                            }
                        }
                    }
                }
            }
        });
        setRunning(true);
    }

    private enum Mode {
        FRIEND, ENEMY, STAFF
    }
}
