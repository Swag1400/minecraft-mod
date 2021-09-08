package me.comu.client.command.impl.client;

import me.comu.client.command.Argument;
import me.comu.client.command.Command;
import me.comu.client.core.Gun;
import me.comu.client.friend.Friend;

public final class Friends
{
    public static final class Add extends Command
    {
        public Add()
        {
            super(new String[] {"add", "a"}, new Argument("username"), new Argument("alias"));
        }

        @Override
        public String dispatch()
        {
            String username = getArgument("username").getValue();
            String alias = getArgument("alias").getValue();

            if (Gun.getInstance().getFriendManager().isFriend(username))
            {
                return "That user is already a friend.";
            }
//            if (alias == null) {
//            	Gun.getInstance().getFriendManager().register(new Friend(username));
//                return String.format("Added friend with alias %s.", username);
//            }

            Gun.getInstance().getFriendManager().register(new Friend(username, alias));
            return String.format("Added friend with alias %s.", alias);
        }
    }

    public static final class Remove extends Command
    {
        public Remove()
        {
            super(new String[] {"remove", "rem"}, new Argument("username/alias"));
        }

        @Override
        public String dispatch()
        {
            String name = getArgument("username/alias").getValue();

            if (!Gun.getInstance().getFriendManager().isFriend(name))
            {
                return "That user is not a friend.";
            }

            Friend friend = Gun.getInstance().getFriendManager().getFriendByAliasOrLabel(name);
            String oldAlias = friend.getAlias();
            Gun.getInstance().getFriendManager().unregister(friend);
            return String.format("Removed friend with alias %s.", oldAlias);
        }
    }
    
}
