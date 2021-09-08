package me.comu.client.command.impl.client;

import com.mojang.authlib.GameProfile;
import me.comu.api.minecraft.helper.EntityHelper;
import me.comu.api.minecraft.helper.PlayerHelper;
import me.comu.client.command.Argument;
import me.comu.client.command.Command;
import net.minecraft.client.entity.EntityOtherPlayerMP;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class FakePlayer
{

    private static List<EntityOtherPlayerMP> entities = new ArrayList<>();

    public static final class FakePlayerAdd extends Command
    {
        public FakePlayerAdd()
        {
            super(new String[] {"fakeplayeradd","addfakeplayer","addfake", "fakeadd","fakeplayeradd","addfp","fpadd"}, new Argument("username"));
        }

        @Override
        public String dispatch()
        {
            EntityOtherPlayerMP entity = new EntityOtherPlayerMP(minecraft.theWorld, new GameProfile(minecraft.thePlayer.getUniqueID(), getArgument("username").getValue()));
            entity.copyLocationAndAnglesFrom(minecraft.thePlayer);
            entity.inventory.copyInventory(minecraft.thePlayer.inventory);
            float[] rotations = EntityHelper.getRotationsAtLocation(EntityHelper.Location.HEAD, minecraft.thePlayer);
            entity.rotationYaw = PlayerHelper.wrapAngleTo180(rotations[0]);
            entity.rotationPitch = PlayerHelper.wrapAngleTo180(rotations[1]);
            entity.swingItem();
            minecraft.theWorld.addEntityToWorld(new Random().nextInt(6942069), entity);
            entities.add(entity);
            return "Added fake player \247e" + entity.getName() + "\2477.";

        }
    }

    public static final class FakePlayerRemove extends Command
    {
        public FakePlayerRemove()
        {
            super(new String[] {"fakeplayerremove", "removefakeplayer","removefp","removefake","fpremove","deletefakeplayer","fakeplayerdelete","fpdelete","deletefp","delfp","fpdel","clearfakeplayers","fakeplayersclear","fpclear","clearfp"});
        }

        @Override
        public String dispatch()
        {
            int size = entities.size();
            for (EntityOtherPlayerMP entityOtherPlayerMP : entities)
            {
                minecraft.theWorld.removeEntity(entityOtherPlayerMP);
            }
            return "\2477Removed \247e" + size + "\2477 fake players.";
        }
    }

}
