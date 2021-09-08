package me.comu.client.command.impl.client;

import me.comu.client.command.Argument;
import me.comu.client.command.Command;
import me.comu.client.core.Gun;
import me.comu.client.staff.Staff;

public final class StaffCom
{
    public static final class StaffAdd extends Command
    {
        public StaffAdd()
        {
            super(new String[] {"staffadd", "sadd", "staff"}, new Argument("username"), new Argument("alias"));
        }

        @Override
        public String dispatch()
        {
            String username = getArgument("username").getValue();
            String alias = getArgument("alias").getValue();

            if (Gun.getInstance().getStaffManager().isStaff(username))
            {
                return "That user is already added as a staff member.";
            }

            Gun.getInstance().getStaffManager().register(new Staff(username, alias));
            return String.format("Added staff member with alias %s.", alias);
        }
    }

    public static final class StaffRemove extends Command
    {
        public StaffRemove()
        {
            super(new String[] {"staffremove","staffrem", "sremove", "srem"}, new Argument("username/alias"));
        }

        @Override
        public String dispatch()
        {
            String name = getArgument("username/alias").getValue();

            if (!Gun.getInstance().getStaffManager().isStaff(name))
            {
                return "That user is not added as a staff member.";
            }

            Staff staff = Gun.getInstance().getStaffManager().getStaffByAliasOrLabel(name);
            String oldAlias = staff.getAlias();
            Gun.getInstance().getStaffManager().unregister(staff);
            return String.format("Removed staff member with alias %s.", oldAlias);
        }
    }
}
