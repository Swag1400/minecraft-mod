package me.comu.client.command.impl.client;

import me.comu.client.command.Argument;
import me.comu.client.command.Command;

public final class Profile
{
    public static final class ProfileAdd extends Command
    {
        public ProfileAdd()
        {
            super(new String[] {"profileadd", "addprofile", "createprofile", "saveprofile","profilesave","profileadd","profadd","addprof","createprof"}, new Argument("username"), new Argument("profile"));
        }

        @Override
        public String dispatch()
        {
            return "";
        }
    }

    public static final class ProfileRemove extends Command
    {
        public ProfileRemove()
        {
            super(new String[] {"profileremove", "profilerem", "prem","delprofile","deleteprofile","removeprofile","remprofile","remprof","profrem","delprof","profdel"}, new Argument("profile"));
        }

        @Override
        public String dispatch()
        {
            return "";
        }
    }

    public static final class ProfileList extends Command
{
    public ProfileList()
    {
        super(new String[] {"profilelist", "profileslist", "listprofiles","proflist","listprof"});
    }

    @Override
    public String dispatch()
    {
       return "";
    }
}

}
