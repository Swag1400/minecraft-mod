package me.comu.client.module.impl.toggle.miscellaneous;

import me.comu.api.event.Listener;
import me.comu.api.stopwatch.Stopwatch;
import me.comu.client.events.MotionUpdateEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.Property;
import me.comu.client.utils.ClientUtils;
import net.minecraft.network.play.client.C01PacketChatMessage;

import java.util.Random;

public final class Spammer extends ToggleableModule {
    private final Property<Boolean> hash = new Property(false, "Hash", "leet");
    private int lastUsed;
    private final String[] phraseList = new String[]{"test"};

    private String[] hashList = new String[]{"grfigferw23ioegrwiobuefgwiowfegiobnwefgino[gweior[biougrwqghfi90w0h9fgt432t0934bgvriowgbr jbhjnv8i3h",
            "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"};
    private final Stopwatch stopwatch = new Stopwatch();

    public Spammer() {
        super("Spammer", new String[]{"spammer"}, 0xFF4BCFE3, ModuleType.MISCELLANEOUS);
        super.offerProperties(hash);
        this.listeners.add(new Listener<MotionUpdateEvent>("potion_saver_packet_listener") {
            @Override
            public void call(MotionUpdateEvent event) {
                if (stopwatch.hasCompleted(randomDelay())) {
                    ClientUtils.mc().func_175102_a().addToSendQueue(new C01PacketChatMessage(randomPhrase()));
                    stopwatch.reset();
                }
            }
        });
    }

    private String randomPhrase() {
        Random rand;
        int randInt;

        for (rand = new Random(), randInt = rand.nextInt(hash.getValue() ? this.hashList.length : this.phraseList.length); this.lastUsed == randInt; randInt = rand.nextInt(hash.getValue() ? this.hashList.length : this.phraseList.length)) {
        }

        lastUsed = randInt;
        return this.hash.getValue() ? hashList[randInt] : phraseList[randInt];
    }

    private long randomDelay() {
        final Random randy = new Random();
        final int randyInt = randy.nextInt(2000) + 2000;
        return randyInt;
    }
}
