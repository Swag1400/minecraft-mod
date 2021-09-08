package me.comu.client.module.impl.toggle.miscellaneous;

import me.comu.api.event.Listener;
import me.comu.client.events.EventTarget;
import me.comu.client.events.SignEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.EnumProperty;
import me.comu.client.utils.Helper;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class AutoSign extends ToggleableModule {

    private final EnumProperty<Mode> mode = new EnumProperty<>(Mode.SPAM, "Mode", "m");


    public AutoSign() {
        super("AutoSign", new String[]{"autosign", "sign"}, 0xFFFA8D61, ModuleType.MISCELLANEOUS);
        this.offerProperties(mode);
        this.listeners.add(new Listener<SignEvent>("auto_eat_update_listener") {
            @EventTarget
            public void call(SignEvent event) {
                switch (mode.getValue())
                {
                    case SPAM:
                        Helper.sendPacket(new C12PacketUpdateSign(event.getSign().getPos(), new IChatComponent[]{new ChatComponentText("\u9ed1\u9b3c\u67f4\u8349\u513f\u52d2\u5c41\u827e\u897f\u897f\u5409\u827e\u4f0a\u5a1c\u4f0a"), new ChatComponentText("\u9ed1\u9b3c\u67f4\u8349\u513f\u52d2\u5c41\u827e\u897f\u897f\u5409\u827e\u4f0a\u5a1c\u4f0a"), new ChatComponentText("\u9ed1\u9b3c\u67f4\u8349\u513f\u52d2\u5c41\u827e\u897f\u897f\u5409\u827e\u4f0a\u5a1c\u4f0a"), new ChatComponentText("\u9ed1\u9b3c\u67f4\u8349\u513f\u52d2\u5c41\u827e\u897f\u897f\u5409\u827e\u4f0a\u5a1c\u4f0a")}));
                        break;
                    case NAZI:
                        Helper.sendPacket(new C12PacketUpdateSign(event.getSign().getPos(), new IChatComponent[]{new ChatComponentText("\u5350\u534d\u2719\u5350\u534d\u2719\u5350\u534d\u2719\u5350\u534d\u2719\u5350\u534d\u2719"), new ChatComponentText("\u5350\u534d\u2719\u5350\u534d\u2719\u5350\u534d\u2719\u5350\u534d\u2719\u5350\u534d\u2719"), new ChatComponentText("\u5350\u534d\u2719\u5350\u534d\u2719\u5350\u534d\u2719\u5350\u534d\u2719\u5350\u534d\u2719"), new ChatComponentText("\u5350\u534d\u2719\u5350\u534d\u2719\u5350\u534d\u2719\u5350\u534d\u2719\u5350\u534d\u2719")}));
                        break;
                    case EXPLOIT:
                        String[] lines = new String[4];
                        final IntStream gen = new Random().ints(0x80, 0x10ffff - 0x800).map(i -> i < 0xd800 ? i : i + 0x800);
                        final String line = gen.limit(4 * 384).mapToObj(i -> String.valueOf((char) i)).collect(Collectors.joining());
                        for (int i = 0; i < 4; i++) {
                            lines[i] = line.substring(i * 384, (i + 1) * 384);
                        }
                        Helper.sendPacket(new C12PacketUpdateSign(event.getSign().getPos(), new IChatComponent[]{new ChatComponentText(lines[0]), new ChatComponentText(lines[1]), new ChatComponentText(lines[2]), new ChatComponentText(lines[3])}));
                        break;
                    case EXPLOIT2:
                        Helper.sendPacket(new C12PacketUpdateSign(event.getSign().getPos(), new IChatComponent[]{new ChatComponentText("\ud83c\udd75\ud83c\udd70\ud83c\udd76\ud83c\udd76\ud83c\udd7e\ud83c\udd83 \u0021\n\ud83c\udd75\ud83c\udd70\ud83c\udd76\ud83c\udd76\ud83c\udd7e\ud83c\udd83 \u0021\n\ud83c\udd75\ud83c\udd70\ud83c\udd76\ud83c\udd76\ud83c\udd7e\ud83c\udd83 \u0021\n\ud83c\udd75\ud83c\udd70\ud83c\udd76\ud83c\udd76\ud83c\udd7e\ud83c\udd83 \u0021\n\ud83c\udd75\ud83c\udd70\ud83c\udd76\ud83c\udd76\ud83c\udd7e\ud83c\udd83 \u0021\n\ud83c\udd75\ud83c\udd70\ud83c\udd76\ud83c\udd76\ud83c\udd7e\ud83c\udd83 \u0021\n\ud83c\udd75\ud83c\udd70\ud83c\udd76\ud83c\udd76\ud83c\udd7e\ud83c\udd83 \u0021\n\ud83c\udd75\ud83c\udd70\ud83c\udd76\ud83c\udd76\ud83c\udd7e\ud83c\udd83 \u0021\n\ud83c\udd75\ud83c\udd70\ud83c\udd76\ud83c\udd76\ud83c\udd7e\ud83c\udd83 \u0021\n\ud83c\udd75\ud83c\udd70\ud83c\udd76\ud83c\udd76\ud83c\udd7e\ud83c\udd83 \u0021\n"), new ChatComponentText("\ud83c\udd75\ud83c\udd70\ud83c\udd76\ud83c\udd76\ud83c\udd7e\ud83c\udd83 \u0021\n"), new ChatComponentText("\ud83c\udd75\ud83c\udd70\ud83c\udd76\ud83c\udd76\ud83c\udd7e\ud83c\udd83 \u0021\n"), new ChatComponentText("\u0021 \ud83c\udd3d\ud83c\udd38\ud83c\udd36\ud83c\udd36\ud83c\udd34\ud83c\udd41 \ud83c\udd35\ud83c\udd30\ud83c\udd36\ud83c\udd36\ud83c\udd3e\ud83c\udd43 \u0021")}));
                        break;
                    case VULGAR:
                        String lenny = "\u0028\u3063\u25d4\u25e1\u25d4\u0029\u3063";
                        String heart = "\u2665";
                        Helper.sendPacket(new C12PacketUpdateSign(event.getSign().getPos(), new IChatComponent[]{new ChatComponentText(heart + lenny + heart), new ChatComponentText("\u5350\u534d\uff2e\uff29\uff27\uff27\uff25\uff32\u5350\u534d"), new ChatComponentText("\u2719\u2719\uff26\uff21\uff27\uff27\uff2f\uff34\u2719\u2719"), new ChatComponentText(heart + lenny + heart)}));
                        break;

                    case NORMAL:
                        Helper.sendPacket(new C12PacketUpdateSign(event.getSign().getPos(), new IChatComponent[]{new ChatComponentText(""), new ChatComponentText(""), new ChatComponentText(""), new ChatComponentText("")}));
                        break;
                }
                minecraft.displayGuiScreen(null);
                event.setCanceled(true);


            }

        });
    }

    private enum Mode
    {
        NORMAL, SPAM, EXPLOIT, EXPLOIT2, NAZI, VULGAR
    }
}

