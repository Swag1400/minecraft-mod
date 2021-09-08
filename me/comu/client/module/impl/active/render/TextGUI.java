package me.comu.client.module.impl.active.render;

import me.comu.api.event.Listener;
import me.comu.api.interfaces.Toggleable;
import me.comu.api.minecraft.helper.PlayerHelper;
import me.comu.api.minecraft.render.CustomFont;
import me.comu.client.core.Gun;
import me.comu.client.events.PacketEvent;
import me.comu.client.events.RenderGameOverlayEvent;
import me.comu.client.events.TickEvent;
import me.comu.client.module.Module;
import me.comu.client.module.ToggleableModule;
import me.comu.client.module.impl.toggle.combat.AutoHeal;
import me.comu.client.module.impl.toggle.combat.AutoHeal2;
import me.comu.client.module.impl.toggle.combat.KillAura;
import me.comu.client.module.impl.toggle.miscellaneous.InventoryCleaner;
import me.comu.client.module.impl.toggle.movement.Blink;
import me.comu.client.module.impl.toggle.movement.LongJump;
import me.comu.client.module.impl.toggle.movement.Speed;
import me.comu.client.module.impl.toggle.render.TabGui;
import me.comu.client.module.impl.toggle.render.Waypoints;
import me.comu.client.module.impl.toggle.world.Phase;
import me.comu.client.properties.EnumProperty;
import me.comu.client.properties.Property;
import me.comu.client.utils.ClientUtils;
import me.comu.client.utils.ColorHelper;
import me.comu.client.utils.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public final class TextGUI extends Module {
    private final Property<Boolean> watermark = new Property<>(false, "Watermark", "wm", "water"), transparent = new Property<>(true, "Transparent", "trans"), suffix = new Property<>(false, "Suffix", "suff", "suf", "s"), potions = new Property<>(true, "Potions", "pots"), armor = new Property<>(true, "Armor", "a"), armorDura = new Property<>(false, "Armor-Dura", "dura", "armordura"), direction = new Property<>(true, "Direction", "facing", "d"), time = new Property<>(true, "Time", "t"), coords = new Property<>(true, "Coords", "coord", "c", "cord"), fps = new Property<>(true, "FPS"), ping = new Property<>(true, "Ping", "ms"), bps = new Property<>(true, "BPS", "blockspersec", "blockspersecond"), arraylist = new Property<>(true, "ArrayList", "array", "al"), itemDura = new Property<>(true, "Item-Dura", "item", "itemdura"), healthOverlay = new Property<>(false, "Health-Overlay", "healthoverlay", "overlayhealth", "screenhealth", "oh"), tps = new Property<>(true, "TPS"), serverBrand = new Property<>(true,"ServerBrand", "brand","server");
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a");
    private final EnumProperty<Organize> organize = new EnumProperty<>(Organize.LONGEST, "Organize", "o");
    private final EnumProperty<Color> arrayListColor = new EnumProperty<>(Color.DEFAULT, "Color", "array-listcolor", "arraycolor", "arraylistcolor");
    private final EnumProperty<Position> arrayListPosition = new EnumProperty<>(Position.TOPRIGHT, "Position", "pos");
    private final EnumProperty<Look> look = new EnumProperty<>(Look.DEFAULT, "Casing", "c");
    public final CustomFont textFont = new CustomFont("Futura Std Medium", 14);
    public static boolean shown;
    private int fadeState = 0;
    private boolean goingUp;
    private int currentColor;
    private long prevTime = -1;
    public static float tpsVal = 0.0f;

    public TextGUI() {
        super("Text-GUI", new String[]{"textgui", "hud", "overlay"});
        this.offerProperties(look, healthOverlay, watermark, organize, transparent, suffix, bps, potions, armor, time, direction, arraylist, coords, fps, ping, itemDura, arrayListColor, arrayListPosition, tps, serverBrand);
        Gun.getInstance().getEventManager().register(new Listener<RenderGameOverlayEvent>("text_gui_render_game_overlay_listener") {
            @Override
            public void call(RenderGameOverlayEvent event) {
                if (minecraft.gameSettings.showDebugInfo || event.getType() != RenderGameOverlayEvent.Type.IN_GAME) {
                    return;
                }
                TabGui tabGui = (TabGui) Gun.getInstance().getModuleManager().getModuleByAlias("tabgui");
                boolean tabGuiShown = tabGui.isRunning();
                ScaledResolution scaledResolution = event.getScaledResolution();
                int positionY = -7;
                int positionX = -7;
                int yDiff = -7;
                if (watermark.getValue()) {
                    yDiff += 9;
                }
                if (tabGuiShown) {
                    yDiff += 76;
                }
                if (itemDura.getValue() && shown) {
                    yDiff += 11;
                }
                if (watermark.getValue()) {
                    GlStateManager.pushMatrix();
                    GlStateManager.enableBlend();
                    minecraft.fontRenderer.drawStringWithShadow(String.format("%s \2477b%s", Gun.TITLE, Gun.BUILD), 2, 2, transparent.getValue() ? 0x99FFFFFF : 0xFFFFFFFF);
                    GlStateManager.disableBlend();
                    GlStateManager.popMatrix();

                }


                if (arraylist.getValue()) {
                    List<Module> modules = Gun.getInstance().getModuleManager().getRegistry();

                    switch (organize.getValue()) {
                        case ABC:
                            modules.sort((mod1, mod2) -> mod1.getTag().compareTo(mod2.getTag()));
                            break;

                        case LONGEST:
                            modules.sort((mod1, mod2) -> minecraft.fontRenderer.getStringWidth(mod2.getTag()) - minecraft.fontRenderer.getStringWidth(mod1.getTag()));
                            break;
                        case SHORTEST:
                            modules.sort(Comparator.comparingInt(mod -> minecraft.fontRenderer.getStringWidth(mod.getTag())));
                            break;
                    }

                    int yCount = 2;
                    int index = 0;
                    final int[] counter = {1};
                    long x = 0;
                    int random = ColorHelper.generateColor();
                    Waypoints waypoints = (Waypoints) Gun.getInstance().getModuleManager().getModuleByAlias("waypoints");
                    Property<Boolean> textDisplay = waypoints.getPropertyByAlias("textdisplay");
                    if (waypoints.isRunning() && textDisplay.getValue() && Waypoints.shouldRender && !Waypoints.points.isEmpty()) {
                        yDiff += 9 * (Waypoints.points.size()) + 4;
                    }
                    for (Module module : modules) {
                        if (module instanceof Toggleable) {
                            ToggleableModule toggleableModule = (ToggleableModule) module;

                            if (toggleableModule.isDrawn() && toggleableModule.getColor() != 0 && toggleableModule.isRunning()) {
                                int labelWidth = minecraft.fontRenderer.getStringWidth(getTag(toggleableModule.getTag()));
                                // minecraft.fontRenderer.drawStringWithShadow(getTag(toggleableModule.getTag()), (scaledResolution.getScaledWidth() - labelWidth) -2, yCount, ColorUtils.rainbowEffect(index + positionX*2000000000L, 1.0F));
                                final int i[] = {0xAA0000, 0xFF5555, 0xFFAA00, 0xFFFF55, 0x00AA00, 0x55FF55, 0x55FFFF, 0x00AAAA, 0x0000AA, 0x5555FF, 0xFF55FF, 0xAA00AA, 0xFFFFFF, 0xAAAAAA, 0x555555, 0x000000};
                                if (index == i.length)
                                    index = 0;
                                if (arrayListPosition.getValue() == Position.TOPRIGHT)
                                    minecraft.fontRenderer.drawStringWithShadow(getTag(toggleableModule.getTag()), (scaledResolution.getScaledWidth() - labelWidth) - 2, positionY += 9, (arrayListColor.getValue() == Color.WHITE) ? 0xFFFFFF : (arrayListColor.getValue() == Color.RAINBOW) ? GuiUtils.rainbow(counter[0] * 300) : (arrayListColor.getValue() == Color.ANIMATED) ? currentColor : (arrayListColor.getValue() == Color.GRAYSCALE) ? 0x666666 : (arrayListColor.getValue() == Color.PRECEDENT) ? i[index] : (arrayListColor.getValue() == Color.RANDOM) ? random : toggleableModule.getColor());
                                else if (arrayListPosition.getValue() == Position.TOPLEFT) {
                                    minecraft.fontRenderer.drawStringWithShadow(getTag(toggleableModule.getTag()), 2, yDiff += 9, (arrayListColor.getValue() == Color.WHITE) ? 0xFFFFFF : (arrayListColor.getValue() == Color.RAINBOW) ? GuiUtils.rainbow(counter[0] * 300) : (arrayListColor.getValue() == Color.ANIMATED) ? currentColor : (arrayListColor.getValue() == Color.GRAYSCALE) ? 0x666666 : (arrayListColor.getValue() == Color.PRECEDENT) ? i[index] : (arrayListColor.getValue() == Color.RANDOM) ? random : toggleableModule.getColor());
                                }
                                counter[0]++;
                                if (index != i.length)
                                    index++;
                                else
                                    index = 0;
                            }
                        }
                    }
                }

                if (bps.getValue()) {
                    final DecimalFormat df = new DecimalFormat("#.##");
                    final double deltaX = Minecraft.getMinecraft().thePlayer.posX - Minecraft.getMinecraft().thePlayer.prevPosX;
                    final double deltaZ = Minecraft.getMinecraft().thePlayer.posZ - Minecraft.getMinecraft().thePlayer.prevPosZ;
                    final float tickRate = tpsVal;
                    String calculateDSpeed = df.format((Math.sqrt(deltaX * deltaX + deltaZ * deltaZ) / tickRate)*400);
                    double bpsTemp = minecraft.thePlayer.getSpeed() * minecraft.thePlayer.movementInput.moveForward;
                    String bps = calculateDSpeed + " bp/s";
                    if (minecraft.playerController.isInCreativeMode()) {
                        minecraft.fontRenderer.drawStringWithShadow(bps, (float) scaledResolution.getScaledWidth() / 2 + 95, (float) scaledResolution.getScaledHeight() / 2 + 325, 0xFFFFF);
                    } else {
                        minecraft.fontRenderer.drawStringWithShadow(bps, (float) scaledResolution.getScaledWidth() / 2 + 95,
                                armor.getValue() && isArmorItemBeingRendered() ? (float) (scaledResolution.getScaledHeight() / 2 + 305) : (float) scaledResolution.getScaledHeight() / 2 + 325,
                                0xFFFFF);
                    }
                }
                if (tps.getValue()) {
                    final DecimalFormat df = new DecimalFormat("#.##");
                    if (tpsVal > 20) tpsVal = 20.0f;
                    if (tpsVal < 0) tpsVal = 0.0f;
                    String tpsColor;
                    if (tpsVal >= 19.4) {
                        tpsColor = "\247a";
                    } else if (tpsVal > 19) {
                        tpsColor = "\2472";
                    } else if (tpsVal > 17) {
                        tpsColor = "\247e";
                    } else if (tpsVal > 10) {
                        tpsColor = "\2476";
                    } else {
                        tpsColor = "\247c";
                    }
                    String tpsString = tpsColor + df.format(tpsVal) + " \2477TPS";
                    if (minecraft.playerController.isInCreativeMode()) {
                        minecraft.fontRenderer.drawStringWithShadow(tpsString, (float) scaledResolution.getScaledWidth() / 2 + 95, (float) scaledResolution.getScaledHeight() / 2 + 335, 0xFFFFF);
                    } else {
                        minecraft.fontRenderer.drawStringWithShadow(tpsString, (float) scaledResolution.getScaledWidth() / 2 + 95,
                                armor.getValue() && isArmorItemBeingRendered() ? (float) (scaledResolution.getScaledHeight() / 2 + 315) : (float) scaledResolution.getScaledHeight() / 2 + 335,
                                0xFFFFF);
                    }
                }
                if (healthOverlay.getValue()) {
                    int health = (int) minecraft.thePlayer.getHealth();
                    int color;
                    if (health > 16) {
                        color = 0x55FF55; // light green
//                    } else if (health > 16) {
//                        color = 0x00AA00; // dark green
                    } else if (health > 12) {
                        color = 0xFFFF55; // yellow
                    } else if (health > 8) {
                        color = 0xFFAA00; //gold
                    } else if (health > 5) {
                        color = 0xFF5555; //red
                    } else {
                        color = 0xAA0000; //dark red
                    }
                    AutoHeal autoHeal = (AutoHeal) Gun.getInstance().getModuleManager().getModuleByAlias("autoheal");
                    AutoHeal2 autoHeal2 = (AutoHeal2) Gun.getInstance().getModuleManager().getModuleByAlias("autopot2");
//                    EnumProperty<AutoHeal.Mode> mode = (EnumProperty<AutoHeal.Mode>) autoHeal.getPropertyByAlias("Mode");
                    if (autoHeal.isRunning() || autoHeal2.isRunning()) {
                        minecraft.fontRenderer.drawStringWithShadow(getCount() > 0 ? Integer.toString(health) + String.format(" \2477%s", getCount()) : Integer.toString(health), 650, 325, color);
                    } else {
                        minecraft.fontRenderer.drawStringWithShadow(Integer.toString(health), 650, 325, color);
                    }
                }
                if (itemDura.getValue()) {
                    ItemStack stack = minecraft.thePlayer.getHeldItem();
                    if (stack == null || !stack.isItemDamaged()) {
                        shown = false;
                    } else if (stack.getItem() != null && stack.isItemDamaged()) {
                        shown = true;
                        minecraft.fontRenderer.drawStringWithShadow("§7<" + Integer.toString((stack.getMaxDamage() - stack.getItemDamage())) + ">", 2, (watermark.getValue() && tabGuiShown) ? 89 : (!watermark.getValue() && tabGuiShown) ? 79 : (!watermark.getValue() && !tabGuiShown) ? 2 : 13, 0xFFFFF);
                    }
                } else {
                    shown = false;
                }

                if (suffix.getValue() == false) {
                    KillAura ka = (KillAura) Gun.getInstance().getModuleManager().getModuleByAlias("killaura");
                    Speed sp = (Speed) Gun.getInstance().getModuleManager().getModuleByAlias("speed");
                    LongJump lj = (LongJump) Gun.getInstance().getModuleManager().getModuleByAlias("longjump");
                    Phase ph = (Phase) Gun.getInstance().getModuleManager().getModuleByAlias("phase");
                    Blink bl = (Blink) Gun.getInstance().getModuleManager().getModuleByAlias("blink");
                    InventoryCleaner iv = (InventoryCleaner) Gun.getInstance().getModuleManager().getModuleByAlias("inventorycleaner");
                    ka.setTag("KillAura");
                    sp.setTag("Swiftness");
                    lj.setTag("LongJump");
                    ph.setTag("Phase");
                    bl.setTag("Blink");
                    iv.setTag("InvCleaner");

                }

                /*
                    if (armor.getValue())
                {
                    int x = 15;
                    GlStateManager.pushMatrix();
                    RenderHelper.enableGUIStandardItemLighting();

                    for (int index = 3; index >= 0; index--)
                    {
                        ItemStack stack = minecraft.thePlayer.inventory.armorInventory[index];

                        if (stack != null)
                        {
                            int y;
                            int yy = scaledResolution.getScaledHeight() - 62;

                            if (minecraft.thePlayer.isInsideOfMaterial(Material.water) && !minecraft.thePlayer.capabilities.isCreativeMode)
                            {
                                y = 65;
                                yy -= 12;
                            }
                            else if (minecraft.thePlayer.capabilities.isCreativeMode)
                            {
                                y = 38;
                                yy = 642;
                            }
                            else
                            {
                                y = 55;
                                yy = 625;
                            }
                            	if (armorDura.getValue() && !(minecraft.currentScreen instanceof GuiChat)) {
                                    if (stack.getMaxDamage() - stack.getItemDamage() < stack.getMaxDamage()) {
                                        RenderHelper.enableGUIStandardItemLighting();
                                        textFont.drawCenteredString((stack.getMaxDamage() - stack.getItemDamage()) + "", scaledResolution.getScaledWidth() / 2.0115F + x  / .8F, yy, 0xFFFFFFFF);
                                        RenderHelper.disableStandardItemLighting();
                                    }

                            	}
                            if (!(minecraft.currentScreen instanceof GuiChat)) {
                            minecraft.getRenderItem().renderItemAndEffectIntoGUI(stack, scaledResolution.getScaledWidth() / 2 + x, scaledResolution.getScaledHeight() - y);
                            minecraft.getRenderItem().renderItemOverlays(minecraft.fontRenderer, stack, scaledResolution.getScaledWidth() / 2 + x, scaledResolution.getScaledHeight() - y);
                            x += 18;
                        }
                        }
                    }

                    RenderHelper.disableStandardItemLighting();
                    GlStateManager.popMatrix();
                }
                 */
                if (armor.getValue()) {
                    if (minecraft.playerController.isNotCreative()) {
                        GL11.glPushMatrix();
                        final RenderItem ir = new RenderItem(minecraft.getTextureManager(), minecraft.modelManager);
                        final List<ItemStack> stuff = new ArrayList<ItemStack>();
                        int split = 15;
                        for (int index = 3; index >= 0; --index) {
                            final ItemStack armer = minecraft.thePlayer.inventory.armorInventory[index];
                            if (armer != null) {
                                stuff.add(armer);
                            }
                        }
                        if (minecraft.thePlayer.getCurrentEquippedItem() != null) {
                            stuff.add(minecraft.thePlayer.getCurrentEquippedItem());
                        }
                        for (final ItemStack errything : stuff) {
                            if (minecraft.theWorld != null) {
                                RenderHelper.enableGUIStandardItemLighting();
                                ir.func_175042_a(errything, split + scaledResolution.getScaledWidth() / 2 + 77, scaledResolution.getScaledHeight() - 18);
                                ir.renderItemOverlays(minecraft.fontRenderer, errything, split + scaledResolution.getScaledWidth() / 2 + 77, scaledResolution.getScaledHeight() - 17);
                                RenderHelper.enableGUIStandardItemLighting();
                                split += 16;
                            }
                            int arrow = 0;
                            for (int index = 9; index < 45; index++) {
                                ItemStack itemStack = minecraft.thePlayer.inventoryContainer.getSlot(index).getStack();
                                if (itemStack == null)
                                    continue;
                                if (itemStack.getDisplayName().equalsIgnoreCase("Arrow")) {
                                    arrow += itemStack.stackSize;
                                }
                            }

                            GlStateManager.enableAlpha(); // TODO: THIS BLOCK IS WHAT YOU NEED TO DO TO PREVENT BAD LIGHTING ON TEXT RENDER
                            GlStateManager.disableCull();
                            GlStateManager.disableBlend();
                            GlStateManager.disableLighting();
                            GlStateManager.clear(256);
                            if (minecraft.thePlayer.getCurrentEquippedItem() != null && minecraft.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBow) {
                                minecraft.fontRenderer.drawStringWithShadow(Integer.toString(arrow), scaledResolution.getScaledWidth() / 2 + 168, scaledResolution.getScaledHeight() - 13, 0xFFFFFF);
                                //  ClientUtils.drawSmallString(Integer.toString(arrow), scaledResolution.getScaledWidth() / 2 + 165, scaledResolution.getScaledHeight() - 10, 0xFFFFFFFF);
                            }
                            final NBTTagList enchants = errything.getEnchantmentTagList();
                            if (enchants != null) {
                                int ency = 0;
                                for (int index2 = 0; index2 < enchants.tagCount(); ++index2) {
                                    final short id = enchants.getCompoundTagAt(index2).getShort("id");
                                    final short level = enchants.getCompoundTagAt(index2).getShort("lvl");
                                    if (Enchantment.ENCHANTMENTS[id] != null) {
                                        final Enchantment enc = Enchantment.ENCHANTMENTS[id];
                                        final String encName = enc.getTranslatedName(level).substring(0, 2).toLowerCase();
                                        ClientUtils.drawSmallString(String.valueOf(encName) + "§6" + level, split + 75 + scaledResolution.getScaledWidth() / 2 - 12, scaledResolution.getScaledHeight() - 19 + ency, -1);
                                        ency += minecraft.fontRenderer.FONT_HEIGHT / 2;
                                    }
                                }
                            }
                        }
                        GL11.glPopMatrix();
                    }
                }

                int y = (scaledResolution.getScaledHeight() - (minecraft.currentScreen instanceof GuiChat ? 24 : 10));

                if (potions.getValue()) {
                    Collection<PotionEffect> effects = minecraft.thePlayer.getActivePotionEffects();

                    if (effects != null && !effects.isEmpty()) {
                        for (PotionEffect effect : effects) {
                            if (effect != null) {
                                Potion potion = Potion.potionTypes[effect.getPotionID()];

                                if (potion != null) {
                                    String name = StatCollector.translateToLocal(potion.getName());
                                    name += String.format(" \2477%s : %s", effect.getAmplifier() + 1, Potion.getDurationString(effect));
                                    int align = scaledResolution.getScaledWidth() - minecraft.fontRenderer.getStringWidth(name) - 2;
                                    minecraft.fontRenderer.drawStringWithShadow(name, align, y, potion.getLiquidColor());
                                    y -= 9;
                                }
                            }
                        }
                    }
                }

                y += 9;

                if (coords.getValue()) {
                    y -= 9;
                    String coordinatesFormat = String.format("\247f%s, %s, %s \2477XYZ", (int) minecraft.thePlayer.posX, (int) minecraft.thePlayer.posY, (int) minecraft.thePlayer.posZ);
                    minecraft.fontRenderer.drawStringWithShadow(coordinatesFormat, (scaledResolution.getScaledWidth() - minecraft.fontRenderer.getStringWidth(coordinatesFormat)) - 2, y, 0xFFFFFFFF);
                }
                if (serverBrand.getValue())
                {
                    if (minecraft.getCurrentServerData() != null) {
                        y -= 9;
                        final String brand = minecraft.isSingleplayer() ? "Vanilla" : minecraft.getCurrentServerData() == null ? "Vanilla" : minecraft.getCurrentServerData().gameVersion;
                        minecraft.fontRenderer.drawStringWithShadow("\2477" + brand, (scaledResolution.getScaledWidth() - minecraft.fontRenderer.getStringWidth(brand)) - 2, y, 0xFFFFFFFF);
                    }
                }
                if (time.getValue()) {
                    y -= 9;
                    String time = String.format("\2477%s", dateFormat.format(new Date()));
                    minecraft.fontRenderer.drawStringWithShadow(time, (scaledResolution.getScaledWidth() - minecraft.fontRenderer.getStringWidth(time)) - 2, y, 0xFFFFFFFF);
                }

                if (fps.getValue()) {
                    y -= 9;
                    String fps = String.format("\2477%s FPS", minecraft.debugFPS);
                    minecraft.fontRenderer.drawStringWithShadow(fps, (scaledResolution.getScaledWidth() - minecraft.fontRenderer.getStringWidth(fps)) - 2, y, 0xFFFFFFFF);
                }

                if (ping.getValue() && minecraft.func_175102_a().func_175104_a(minecraft.thePlayer.getName()) != null) {
                    {
                        y -= 9;
                        String ping = String.format("\2477%s ms", minecraft.func_175102_a().func_175102_a(minecraft.thePlayer.getGameProfile().getId()).getResponseTime());
                        minecraft.fontRenderer.drawStringWithShadow(ping, (scaledResolution.getScaledWidth() - minecraft.fontRenderer.getStringWidth(ping)) - 2, y, 0xFFFFFFFF);
                    }
                }

                if (direction.getValue()) {
                    y -= 9;
                    String direction = String.format("\2477%s", PlayerHelper.getFacingWithProperCapitals().toUpperCase());
                    minecraft.fontRenderer.drawStringWithShadow(direction, (scaledResolution.getScaledWidth() - minecraft.fontRenderer.getStringWidth(direction)) - 2, y, 0xFFFFFFFF);
                }
            }
        });

        Gun.getInstance().getEventManager().register(new Listener<TickEvent>("text_gui_render_game_overlay_listener") {
            @Override
            public void call(TickEvent event) {

                if (arrayListColor.getValue() == Color.ANIMATED)
                    updateFade();
            }
        });
        Gun.getInstance().getEventManager().register(new Listener<PacketEvent>("textgui_render_game_info_listener") {

            @Override
            public void call(PacketEvent event) {
                if (event.getPacket() instanceof S03PacketTimeUpdate) {
                    if (prevTime != -1) {
                        tpsVal = (20.0f / ((float) (System.currentTimeMillis() - prevTime)) / 1000f)*1000000;

                    }
                    prevTime = System.currentTimeMillis();
                }
            }
        });


    }


    public String getTag(String tag) {
        switch (look.getValue()) {
            case UPPER:
                tag = tag.toUpperCase();
                break;

            case LOWER:
                tag = tag.toLowerCase();
                break;
            case CUB:
                tag = String.format("[%s]", tag.toLowerCase());

            case DEFAULT:
                break;
        }

        return tag;
    }


    private void updateFade() {
        if (this.fadeState >= 20 || this.fadeState <= 0) {
            this.goingUp = !this.goingUp;
        }
        if (this.goingUp) {
            ++this.fadeState;
        } else {
            --this.fadeState;
        }
        final double ratio = this.fadeState / 25.0;
        this.currentColor = getFadeHex(-23614, -3394561, ratio);
    }


    private int getFadeHex(final int hex1, final int hex2, final double ratio) {
        int r = hex1 >> 16;
        int g = hex1 >> 8 & 0xFF;
        int b = hex1 & 0xFF;
        r += (int) (((hex2 >> 16) - r) * ratio);
        g += (int) (((hex2 >> 8 & 0xFF) - g) * ratio);
        b += (int) (((hex2 & 0xFF) - b) * ratio);
        return r << 16 | g << 8 | b;
    }

    public int getCount() {
        final int pot = -1;
        int counter = 0;
        for (int i = 9; i < 45; ++i) {
            if (minecraft.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack is = minecraft.thePlayer.inventoryContainer.getSlot(i).getStack();
                final Item item = is.getItem();
                if (item instanceof ItemPotion) {
                    final ItemPotion potion = (ItemPotion) item;
                    if (potion.getEffects(is) != null) {
                        for (final Object o : potion.getEffects(is)) {
                            final PotionEffect effect = (PotionEffect) o;
                            if (effect.getPotionID() == Potion.heal.id && ItemPotion.isSplash(is.getItemDamage())) {
                                ++counter;
                            }
                        }
                    }
                }
                if (item instanceof ItemSoup) {
                    ++counter;
                }
            }
        }
        return counter;
    }

    private boolean isArmorItemBeingRendered()
    {
        if (minecraft.thePlayer.getCurrentEquippedItem() != null)
        {
            return true;
        }

        if (minecraft.thePlayer.getTotalArmorValue() > 0)
        {
            return true;
        }
        return false;
    }

    private enum Organize {
        ABC, LONGEST, SHORTEST
    }

    private enum Look {
        DEFAULT, LOWER, UPPER, CUB
    }

    private enum Color {
        DEFAULT, RAINBOW, PRECEDENT, RANDOM, ANIMATED, GRAYSCALE, WHITE
    }

    private enum Position {
        TOPRIGHT, TOPLEFT
    }
}