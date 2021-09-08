//package me.comu.client.module.impl.toggle.miscellaneous;
//
//import com.gtranslate.Language;
//import com.gtranslate.Translator;
//import me.comu.api.event.Listener;
//import me.comu.client.core.Gun;
//import me.comu.client.events.PacketEvent;
//import me.comu.client.logging.Logger;
//import me.comu.client.module.ModuleType;
//import me.comu.client.module.ToggleableModule;
//import me.comu.client.properties.EnumProperty;
//import me.comu.client.properties.Property;
//import net.minecraft.network.play.client.C01PacketChatMessage;
//
//
//public final class ChatTranslater extends ToggleableModule {
//    private final Property<Boolean> weapons = new Property<>(true, "weapons", "weapon");
//    private final EnumProperty<Mode> mode = new EnumProperty<>(Mode.SPANISH, "Mode", "m");
//    Translator translator = Translator.getInstance();
//
//    @Override
//    protected void onEnable() {
//        super.onEnable();
//        Logger.getLogger().printToChat("GoogleTranslateAPI v1 outdated! Try updating...");
//    }
//
//    public ChatTranslater() {
//        super("Translate", new String[]{"Translate", "translater", "trans", "spanish"}, 0xFF4BCFE3, ModuleType.MISCELLANEOUS);
//        this.offerProperties(mode);
//        this.listeners.add(new Listener<PacketEvent>("auto_tool_?_event_listener") {
//            @Override
//            public void call(PacketEvent event)
//            {
//                if (minecraft.currentScreen != null && event.getPacket() instanceof C01PacketChatMessage) {
//                        C01PacketChatMessage c01PacketChatMessage = (C01PacketChatMessage) event.getPacket();
//                        String message = c01PacketChatMessage.getMessage();
//                        if (message.startsWith("/") || message.startsWith(Gun.getInstance().getCommandManager().getPrefix())) {
//                            return;
//                        }
//                        // might need handling for special chars
//                        switch (mode.getValue()) {
//                            case SPANISH:
//                                    c01PacketChatMessage.setMessage(translator.translate(message, Language.ENGLISH, Language.SPANISH));
//                                break;
//                            case AFRIKAANS:
//                                c01PacketChatMessage.setMessage(translate(message, Language.AFRIKAANS));
//                                break;
//                            case THAI:
//                                c01PacketChatMessage.setMessage(translate(message, Language.THAI));
//                                break;
//                            case URDU:
//                                c01PacketChatMessage.setMessage(translate(message, Language.URDU));
//                                break;
//                            case CZECH:
//                                c01PacketChatMessage.setMessage(translate(message, Language.CZECH));
//                                break;
//                            case DUTCH:
//                                c01PacketChatMessage.setMessage(translate(message, Language.DUTCH));
//                                break;
//                            case GREEK:
//                                c01PacketChatMessage.setMessage(translate(message, Language.GREEK));
//                                break;
//                            case HINDI:
//                                c01PacketChatMessage.setMessage(translate(message, Language.HINDI));
//                                break;
//                            case IRISH:
//                                c01PacketChatMessage.setMessage(translate(message, Language.IRISH));
//                                break;
//                            case LATIN:
//                                c01PacketChatMessage.setMessage(translate(message, Language.LATIN));
//                                break;
//                            case MALAY:
//                                c01PacketChatMessage.setMessage(translate(message, Language.MALAY));
//                                break;
//                            case TAMIL:
//                                c01PacketChatMessage.setMessage(translate(message, Language.TAMIL));
//                                break;
//                            case WELSH:
//                                c01PacketChatMessage.setMessage(translate(message, Language.WELSH));
//                                break;
//                            case ARABIC:
//                                c01PacketChatMessage.setMessage(translate(message, Language.ARABIC));
//                                break;
//                            case BASQUE:
//                                c01PacketChatMessage.setMessage(translate(message, Language.BASQUE));
//                                break;
//                            case DANISH:
//                                c01PacketChatMessage.setMessage(translate(message, Language.DANISH));
//                                break;
//                            case FRENCH:
//                                c01PacketChatMessage.setMessage(translate(message, Language.FRENCH));
//                                break;
//                            case GERMAN:
//                                c01PacketChatMessage.setMessage(translate(message, Language.GERMAN));
//                                break;
//                            case HEBREW:
//                                c01PacketChatMessage.setMessage(translate(message, Language.HEBREW));
//                                break;
//                            case KOREAN:
//                                c01PacketChatMessage.setMessage(translate(message, Language.KOREAN));
//                                break;
//                            case POLISH:
//                                c01PacketChatMessage.setMessage(translate(message, Language.POLISH));
//                                break;
//                            case SLOVAK:
//                                c01PacketChatMessage.setMessage(translate(message, Language.SLOVAK));
//                                break;
//                            case TELUGU:
//                                c01PacketChatMessage.setMessage(translate(message, Language.TELUGU));
//                                break;
//                            case BENGALI:
//                                c01PacketChatMessage.setMessage(translate(message, Language.BENGALI));
//                                break;
//                            case CATALAN:
//                                c01PacketChatMessage.setMessage(translate(message, Language.CATALAN));
//                                break;
//                            case CHINESE:
//                                c01PacketChatMessage.setMessage(translate(message, Language.CHINESE));
//                                break;
//                            case ITALIAN:
//                                c01PacketChatMessage.setMessage(translate(message, Language.ITALIAN));
//                                break;
//                            case KANNADA:
//                                c01PacketChatMessage.setMessage(translate(message, Language.KANNADA));
//                                break;
//                            case LATVIAN:
//                                c01PacketChatMessage.setMessage(translate(message, Language.LATVIAN));
//                                break;
//                            case MALTESE:
//                                c01PacketChatMessage.setMessage(translate(message, Language.MALTESE));
//                                break;
//                            case PERSIAN:
//                                c01PacketChatMessage.setMessage(translate(message, Language.PERSIAN));
//                                break;
//                            case RUSSIAN:
//                                c01PacketChatMessage.setMessage(translate(message, Language.RUSSIAN));
//                                break;
//                            case SERBIAN:
//                                c01PacketChatMessage.setMessage(translate(message, Language.SERBIAN));
//                                break;
//                            case SWAHILI:
//                                c01PacketChatMessage.setMessage(translate(message, Language.SWAHILI));
//                                break;
//                            case SWEDISH:
//                                c01PacketChatMessage.setMessage(translate(message, Language.SWEDISH));
//                                break;
//                            case TURKISH:
//                                c01PacketChatMessage.setMessage(translate(message, Language.TURKISH));
//                                break;
//                            case YIDDISH:
//                                c01PacketChatMessage.setMessage(translate(message, Language.YIDDISH));
//                                break;
//                            case ALBANIAN:
//                                c01PacketChatMessage.setMessage(translate(message, Language.ALBANIAN));
//                                break;
//                            case ARMENIAN:
//                                c01PacketChatMessage.setMessage(translate(message, Language.ARMENIAN));
//                                break;
//                            case CROATIAN:
//                                c01PacketChatMessage.setMessage(translate(message, Language.CROATIAN));
//                                break;
//                            case ESTONIAN:
//                                c01PacketChatMessage.setMessage(translate(message, Language.ESTONIAN));
//                                break;
//                            case FILIPINO:
//                                c01PacketChatMessage.setMessage(translate(message, Language.FILIPINO));
//                                break;
//                            case GALICIAN:
//                                c01PacketChatMessage.setMessage(translate(message, Language.GALICIAN));
//                                break;
//                            case GEORGIAN:
//                                c01PacketChatMessage.setMessage(translate(message, Language.GEORGIAN));
//                                break;
//                            case GUJARATI:
//                                c01PacketChatMessage.setMessage(translate(message, Language.GUJARATI));
//                                break;
//                            case JAPANESE:
//                                c01PacketChatMessage.setMessage(translate(message, Language.JAPANESE));
//                                break;
//                            case ROMANIAN:
//                                c01PacketChatMessage.setMessage(translate(message, Language.ROMANIAN));
//                                break;
//                            case BULGARIAN:
//                                c01PacketChatMessage.setMessage(translate(message, Language.BULGARIAN));
//                                break;
//                            case HUNGARIAN:
//                                c01PacketChatMessage.setMessage(translate(message, Language.HUNGARIAN));
//                                break;
//                            case ICELANDIC:
//                                c01PacketChatMessage.setMessage(translate(message, Language.ICELANDIC));
//                                break;
//                            case NORWEGIAN:
//                                c01PacketChatMessage.setMessage(translate(message, Language.NORWEGIAN));
//                                break;
//                            case SLOVENIAN:
//                                c01PacketChatMessage.setMessage(translate(message, Language.SLOVENIAN));
//                                break;
//                            case UKRAINIAN:
//                                c01PacketChatMessage.setMessage(translate(message, Language.UKRAINIAN));
//                                break;
//                            case INDONESIAN:
//                                c01PacketChatMessage.setMessage(translate(message, Language.INDONESIAN));
//                                break;
//                            case LITHUANIAN:
//                                c01PacketChatMessage.setMessage(translate(message, Language.LITHUANIAN));
//                                break;
//                            case MACEDONIAN:
//                                c01PacketChatMessage.setMessage(translate(message, Language.MACEDONIAN));
//                                break;
//                            case PORTUGUESE:
//                                c01PacketChatMessage.setMessage(translate(message, Language.PORTUGUESE));
//                                break;
//                            case VIETNAMESE:
//                                c01PacketChatMessage.setMessage(translate(message, Language.VIETNAMESE));
//                                break;
//                            case AZERBAIJANI:
//                                c01PacketChatMessage.setMessage(translate(message, Language.AZERBAIJANI));
//                                break;
//                            case HAITIAN_CREOLE:
//                                c01PacketChatMessage.setMessage(translate(message, Language.HAITIAN_CREOLE));
//                                break;
//                            case CHINESE_SIMPLIFIED:
//                                c01PacketChatMessage.setMessage(translate(message, Language.CHINESE_SIMPLIFIED));
//                                break;
//                            case CHINESE_TRADITIONAL:
//                                c01PacketChatMessage.setMessage(translate(message, Language.CHINESE_TRADITIONAL));
//                                break;
//
//
//                        }
//                    }
//                }
//        });
//
//    }
//
//    private String translate(String message, String language)
//    {
//        return translator.translate(message, Language.ENGLISH, language);
//    }
//
//    private enum Mode {
//        SPANISH,
//        AFRIKAANS,
//        ALBANIAN,
//        ARABIC,
//        ARMENIAN,
//        AZERBAIJANI,
//        BASQUE,
//        BENGALI,
//        BULGARIAN,
//        CATALAN,
//        CHINESE,
//        CROATIAN,
//        CZECH,
//        DANISH,
//        DUTCH,
//        ESTONIAN,
//        FILIPINO,
//        FRENCH,
//        GALICIAN,
//        GEORGIAN,
//        GERMAN,
//        GREEK,
//        GUJARATI,
//        HAITIAN_CREOLE,
//        HEBREW,
//        HINDI,
//        HUNGARIAN,
//        ICELANDIC,
//        INDONESIAN,
//        IRISH,
//        ITALIAN,
//        JAPANESE,
//        KANNADA,
//        KOREAN,
//        LATIN,
//        LATVIAN,
//        LITHUANIAN,
//        MACEDONIAN,
//        MALAY,
//        MALTESE,
//        NORWEGIAN,
//        PERSIAN,
//        POLISH,
//        PORTUGUESE,
//        ROMANIAN,
//        RUSSIAN,
//        SERBIAN,
//        SLOVAK,
//        SLOVENIAN,
//        SWAHILI,
//        SWEDISH,
//        TAMIL,
//        TELUGU,
//        THAI,
//        TURKISH,
//        UKRAINIAN,
//        URDU,
//        VIETNAMESE,
//        WELSH,
//        YIDDISH,
//        CHINESE_SIMPLIFIED,
//        CHINESE_TRADITIONAL
//    }
//
//}
//
//
