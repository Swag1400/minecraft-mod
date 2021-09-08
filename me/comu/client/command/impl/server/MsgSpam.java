package me.comu.client.command.impl.server;

import me.comu.api.stopwatch.Stopwatch;
import me.comu.client.command.Argument;
import me.comu.client.command.Command;
import me.comu.client.utils.ClientUtils;
import net.minecraft.network.play.client.C01PacketChatMessage;

import java.util.Random;

/**
 * TODO: fix this shit
 */
public final class MsgSpam extends Command
{
    private int lastUsed;
    private String[] phraseList = new String[] {"how much wood can a woodchuck chuck if a woodchuck could chuck wood"};

    private Stopwatch stopwatch = new Stopwatch();
    private boolean isSpamming;
    private String name;
    private int count;

    public MsgSpam()
    {
        super(new String[] {"spam", "spammsg", "msgspam"}, new Argument("Name"), new Argument("Count"));
    }
    @Override
    public String dispatch()
    {

                name = getArgument("Name").getValue();
                count = Integer.parseInt(getArgument("Count").getValue());
                for(int i = 0; i < count; i++) {
                        ClientUtils.mc().func_175102_a().addToSendQueue(new C01PacketChatMessage("/msg " + name + " " + randomPhrase()));
                        isSpamming = true;
                }
                    isSpamming = false;
                    return "Spammed " + name +".";
}

    private String randomPhrase() {
        Random rand;
        int randInt;

        for (rand = new Random(), randInt = rand.nextInt(this.phraseList.length); this.lastUsed == randInt; randInt = rand.nextInt(this.phraseList.length)) {
        }

        lastUsed = randInt;
        return phraseList[randInt];
    }



    private long randomDelay()
    {
        final Random randy = new Random();
        final int randyInt = randy.nextInt(2000) + 2000;
        return randyInt;
    }

}