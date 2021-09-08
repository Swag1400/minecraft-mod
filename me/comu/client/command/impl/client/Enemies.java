package me.comu.client.command.impl.client;

import me.comu.client.command.Argument;
import me.comu.client.command.Command;
import me.comu.client.core.Gun;
import me.comu.client.enemy.Enemy;

public final class Enemies
{
    public static final class EnemyAdd extends Command
    {
        public EnemyAdd()
        {
            super(new String[] {"enemyadd", "enemadd", "eadd", "e"}, new Argument("username"), new Argument("alias"));
        }

        @Override
        public String dispatch()
        {
            String username = getArgument("username").getValue();
            String alias = getArgument("alias").getValue();

            if (Gun.getInstance().getEnemyManager().isEnemy(username))
            {
                return "That user is already an enemy.";
            }
//            if (alias == null) {
//                Gun.getInstance().getEnemyManager().register(new Enemy(username));
//                return String.format("Added enemy with username %s.", username);
//            }
                Gun.getInstance().getEnemyManager().register(new Enemy(username, alias));
            return String.format("Added enemy with alias %s.", alias);
        }
    }

    public static final class EnemyRemove extends Command
    {
        public EnemyRemove()
        {
            super(new String[] {"enemyremove", "eremove", "erem"}, new Argument("username/alias"));
        }

        @Override
        public String dispatch()
        {
            String name = getArgument("username/alias").getValue();

            if (!Gun.getInstance().getEnemyManager().isEnemy(name))
            {
                return "That user is not an enemy.";
            }

            Enemy enemy = Gun.getInstance().getEnemyManager().getEnemyByAliasOrLabel(name);
            String oldAlias = enemy.getAlias();
            Gun.getInstance().getEnemyManager().unregister(enemy);
            return String.format("Removed enemy with alias %s.", oldAlias);
        }
    }
}
