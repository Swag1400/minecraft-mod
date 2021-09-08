package me.comu.client.module.impl.toggle.miscellaneous;

import me.comu.api.event.Listener;
import me.comu.api.interfaces.Toggleable;
import me.comu.client.core.Gun;
import me.comu.client.enemy.Enemy;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.events.PacketEvent;
import me.comu.client.friend.Friend;
import me.comu.client.module.Module;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.staff.Staff;
import me.comu.client.utils.ClientUtils;
import me.comu.client.utils.PathUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public final class CMDBot extends ToggleableModule
{
    private boolean following;
    private String followName;

    public CMDBot()
    {
        super("CMDBot", new String[] {"cmdbot", "bot"}, 0xFFFA8D61, ModuleType.MISCELLANEOUS);
        this.listeners.add(new Listener<MotionUpdateEvent>("derp_update_listener")
        {
            @Override
            public void call(MotionUpdateEvent event)
            {
            if (event.getTime() == MotionUpdateEvent.Time.BEFORE) {
                if (minecraft.thePlayer.isDead) {
                    minecraft.thePlayer.respawnPlayer();
                }
                if (following) {
                    try {
                        final PathUtils pf = new PathUtils(followName);
                        event.setRotationPitch(PathUtils.fakePitch - 30.0f);
                        event.setRotationYaw(PathUtils.fakeYaw);
                    }
                    catch (Exception ex) {

                    }
                }
                }
            }
        });
        this.listeners.add(new Listener<PacketEvent>("") {
            @Override
            public void call(PacketEvent event) {
            if (event.getPacket() instanceof S02PacketChat) {
                final S02PacketChat message = (S02PacketChat)event.getPacket();
                if (message.getChatComponent().getFormattedText().contains("-follow")) {
                    for (final Friend friend : Gun.getInstance().getFriendManager().getRegistry()) {
                        if (message.getChatComponent().getFormattedText().contains(friend.getLabel())) {
                            String s = message.getChatComponent().getFormattedText();
                                s = s.substring(s.indexOf("-follow ") + 8);
                                s = s.substring(0, s.indexOf("§"));
                                following = true;
                                followName = s;
                                ClientUtils.player().sendChatMessage("Now following " + s);
                                break;
                        }
                    }
                }
                if (message.getChatComponent().getFormattedText().contains("-stopfollow")) {
                    for (final Friend friend : Gun.getInstance().getFriendManager().getRegistry()) {
                        if (message.getChatComponent().getFormattedText().contains(friend.getLabel())) {
                            String s = message.getChatComponent().getFormattedText();
                            s = s.substring(s.indexOf("-stopfollow ") + 12);
                            s = s.substring(0, s.indexOf("§"));
                            following = false;
                            followName = "";
                            ClientUtils.player().sendChatMessage("No longer following " + s);
                            break;
                        }
                    }
                }
                if (message.getChatComponent().getFormattedText().contains("-suicide")) {
                    for (final Friend friend : Gun.getInstance().getFriendManager().getRegistry()) {
                        if (message.getChatComponent().getFormattedText().contains(friend.getLabel())) {
                            ClientUtils.player().sendChatMessage("/suicide");
                            break;
                        }
                    }
                }
                if (message.getChatComponent().getFormattedText().contains("-tpahere")) {
                    for (final Friend friend : Gun.getInstance().getFriendManager().getRegistry()) {
                        if (message.getChatComponent().getFormattedText().contains(friend.getLabel())) {
                            ClientUtils.player().sendChatMessage("/tpa " + friend.getLabel());
                            break;
                        }
                    }
                }
                if (message.getChatComponent().getFormattedText().contains("-unstuck")) {
                    for (final Friend friend : Gun.getInstance().getFriendManager().getRegistry()) {
                        if (message.getChatComponent().getFormattedText().contains(friend.getLabel())) {
                            ClientUtils.player().sendChatMessage("Attempting to restart movement.");
                            ClientUtils.packet(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                            break;
                        }
                    }
                }
                if (message.getChatComponent().getFormattedText().contains("-enemy ")) {
                    for (final Entity mod : ClientUtils.loadedEntityList()) {
                        if (mod instanceof EntityPlayer && message.getChatComponent().getFormattedText().contains("-enemy " + mod.getName())) {
                            for (final Friend friend2 : Gun.getInstance().getFriendManager().getRegistry()) {
                                if (message.getChatComponent().getFormattedText().contains(friend2.getLabel())) {
                                    if (Gun.getInstance().getEnemyManager().isEnemy(mod.getName())) {
                                        ClientUtils.player().sendChatMessage(String.valueOf(mod.getName()) + " is already enemied.");
                                        break;
                                    }
                                    if (!Gun.getInstance().getEnemyManager().isEnemy(mod.getName())) {
                                        ClientUtils.player().sendChatMessage(String.valueOf(mod.getName()) + " has been enemied.");
                                         Gun.getInstance().getEnemyManager().register(new Enemy (mod.getName(), mod.getName()));
                                        break;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
                if (message.getChatComponent().getFormattedText().contains("-enemyremove ")) {
                    for (final Entity mod : ClientUtils.loadedEntityList()) {
                        if (mod instanceof EntityPlayer && message.getChatComponent().getFormattedText().contains("-enemyremove " + mod.getName())) {
                            for (final Friend friend2 : Gun.getInstance().getFriendManager().getRegistry()) {
                                if (message.getChatComponent().getFormattedText().contains(friend2.getLabel())) {
                                    if (Gun.getInstance().getEnemyManager().isEnemy(mod.getName())) {
                                        Gun.getInstance().getEnemyManager().unregister(Gun.getInstance().getEnemyManager().getEnemyByAliasOrLabel(mod.getName()));
                                        ClientUtils.player().sendChatMessage(String.valueOf(mod.getName()) + " has been removed from enemies.");
                                        break;
                                    }
                                    if (!Gun.getInstance().getEnemyManager().isEnemy(mod.getName())) {
                                        ClientUtils.player().sendChatMessage(String.valueOf(mod.getName()) + " is not enemied.");
                                        break;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
                if (message.getChatComponent().getFormattedText().contains("-staff ")) {
                    for (final Entity mod : ClientUtils.loadedEntityList()) {
                        if (mod instanceof EntityPlayer && message.getChatComponent().getFormattedText().contains("-staff " + mod.getName())) {
                            for (final Friend friend2 : Gun.getInstance().getFriendManager().getRegistry()) {
                                if (message.getChatComponent().getFormattedText().contains(friend2.getLabel())) {
                                    if (Gun.getInstance().getStaffManager().isStaff(mod.getName())) {
                                        ClientUtils.player().sendChatMessage(String.valueOf(mod.getName()) + " is already added as a staff member.");
                                        break;
                                    }
                                    if (!Gun.getInstance().getStaffManager().isStaff(mod.getName())) {
                                        ClientUtils.player().sendChatMessage(String.valueOf(mod.getName()) + " has been added as a staff member.");
                                        Gun.getInstance().getStaffManager().register(new Staff(mod.getName(), mod.getName()));
                                        break;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
                if (message.getChatComponent().getFormattedText().contains("-staffremove ")) {
                    for (final Entity mod : ClientUtils.loadedEntityList()) {
                        if (mod instanceof EntityPlayer && message.getChatComponent().getFormattedText().contains("-staffremove " + mod.getName())) {
                            for (final Friend friend2 : Gun.getInstance().getFriendManager().getRegistry()) {
                                if (message.getChatComponent().getFormattedText().contains(friend2.getLabel())) {
                                    if (Gun.getInstance().getStaffManager().isStaff(mod.getName())) {
                                        Gun.getInstance().getStaffManager().unregister((Gun.getInstance().getStaffManager().getStaffByAliasOrLabel(mod.getName())));
                                        ClientUtils.player().sendChatMessage(String.valueOf(mod.getName()) + " has been removed as a staff member.");
                                        break;
                                    }
                                    if (!Gun.getInstance().getStaffManager().isStaff(mod.getName())) {
                                        ClientUtils.player().sendChatMessage(String.valueOf(mod.getName()) + " is not added as a staff member.");
                                        break;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
                if (message.getChatComponent().getFormattedText().contains("-friend ")) {
                    for (final Entity mod : ClientUtils.loadedEntityList()) {
                        if (mod instanceof EntityPlayer && message.getChatComponent().getFormattedText().contains("-friend " + mod.getName())) {
                            for (final Friend friend2 : Gun.getInstance().getFriendManager().getRegistry()) {
                                if (message.getChatComponent().getFormattedText().contains(friend2.getLabel())) {
                                    if (Gun.getInstance().getFriendManager().isFriend(mod.getName())) {
                                        ClientUtils.player().sendChatMessage(String.valueOf(mod.getName()) + " is already a friend.");
                                        break;
                                    }
                                    if (!Gun.getInstance().getFriendManager().isFriend(mod.getName())) {
                                        ClientUtils.player().sendChatMessage(String.valueOf(mod.getName()) + " has been friended.");
                                        Gun.getInstance().getFriendManager().register(new Friend(mod.getName(), mod.getName()));
                                        break;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
                if (message.getChatComponent().getFormattedText().contains("-friendremove ")) {
                    for (final Entity mod : ClientUtils.loadedEntityList()) {
                        if (mod instanceof EntityPlayer && message.getChatComponent().getFormattedText().contains("-friendremove " + mod.getName())) {
                            for (final Friend friend2 : Gun.getInstance().getFriendManager().getRegistry()) {
                                if (message.getChatComponent().getFormattedText().contains(friend2.getLabel())) {
                                    if (Gun.getInstance().getFriendManager().isFriend(mod.getName())) {
                                        Gun.getInstance().getFriendManager().unregister(Gun.getInstance().getFriendManager().getFriendByAliasOrLabel(mod.getName()));
                                        ClientUtils.player().sendChatMessage(String.valueOf(mod.getName()) + " has been removed from friends.");
                                        break;
                                    }
                                    if (!Gun.getInstance().getFriendManager().isFriend(mod.getName())) {
                                        ClientUtils.player().sendChatMessage(String.valueOf(mod.getName()) + " is not friended.");
                                        break;
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    if (message.getChatComponent().getFormattedText().contains("-aura tick")) {
                        for (final Friend friend : Gun.getInstance().getFriendManager().getRegistry()) {
                            if (message.getChatComponent().getFormattedText().contains(friend.getLabel())) {
                                ClientUtils.player().sendChatMessage( Gun.getInstance().getCommandManager().getPrefix() + "aura t tick");
                                break;
                            }
                        }
                    }
                    if (message.getChatComponent().getFormattedText().contains("-aura single")) {
                        for (final Friend friend : Gun.getInstance().getFriendManager().getRegistry()) {
                            if (message.getChatComponent().getFormattedText().contains(friend.getLabel())) {
                                ClientUtils.player().sendChatMessage(Gun.getInstance().getCommandManager().getPrefix() + "aura t single");
                                break;
                            }
                        }
                    }
                    if (message.getChatComponent().getFormattedText().contains("-aura switch")) {
                        for (final Friend friend : Gun.getInstance().getFriendManager().getRegistry()) {
                            if (message.getChatComponent().getFormattedText().contains(friend.getLabel())) {
                                ClientUtils.player().sendChatMessage(Gun.getInstance().getCommandManager().getPrefix() + "aura t switch");
                                break;
                            }
                        }
                    }
                    if (message.getChatComponent().getFormattedText().contains("-toggle ")) {
                        for (final Module mod2 : Gun.getInstance().getModuleManager().getRegistry()) {
                            if (message.getChatComponent().getFormattedText().contains("-toggle " + mod2.getLabel())) {
                                for (final Friend friend2 : Gun.getInstance().getFriendManager().getRegistry()) {
                                    if (message.getChatComponent().getFormattedText().contains(friend2.getLabel())) {
                                        if (mod2 instanceof Toggleable) {
                                            ((Toggleable) mod2).toggle();
                                            final boolean state = ((Toggleable) mod2).isRunning();
                                            final String s2 = state ? "enabled" : "disabled";
                                            ClientUtils.player().sendChatMessage(String.valueOf(mod2.getLabel()) + " has been " + s2);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
            }
        });
    }
    }
