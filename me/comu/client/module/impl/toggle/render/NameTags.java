package me.comu.client.module.impl.toggle.render;

import me.comu.api.event.Listener;
import me.comu.api.minecraft.render.RenderMethods;
import me.comu.client.core.Gun;
import me.comu.client.events.PassSpecialRenderEvent;
import me.comu.client.events.RenderEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.EnumProperty;
import me.comu.client.properties.NumberProperty;
import me.comu.client.properties.Property;
import me.comu.client.utils.ColorHelper;
import me.comu.client.utils.Vector4f;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public final class NameTags extends ToggleableModule {
    private final Property<Boolean> armor = new Property<>(true, "Armor", "a"), ping = new Property<>(true, "Ping"),
            heart = new Property<>(true, "Heart"), health = new Property<>(true, "Health", "h"),
            self = new Property<>(false, "Self", "s", "body", "body", "player", "myself"),
            durability = new Property<>(true, "Durability", "d", "dura"),
            distance = new Property<>(true, "Distance", "dist", "blocks"),
            potion = new Property<>(true, "Potion", "pot", "p", "potions"),
            hunger = new Property<>(true, "Hunger", "hung", "h", "food"),
            customEnchants = new Property<>(false, "Custom-Enchants", "customenchants", "customenchant", "ce", "ces"),
            itemName = new Property<>(false, "Item-Names", "item-name", "itemname", "itemnames", "name", "names"),
            purple = new Property<>(false, "Purple", "omega", "omegas", "pp", "purpleprison");
    private final Property<Boolean> invis = new Property<>(false, "Invisibles", "invis", "invisible", "i");
    private final NumberProperty<Float> scaling = new NumberProperty<>(0.0030F, 0.001F, 0.0100F, 0.001F, "Scaling", "scale",
            "s"), width = new NumberProperty<>(1.6F, 1F, 5F, 0.1F, "Width", "w");
    private final EnumProperty<Health> healthLook = new EnumProperty<>(Health.TWENTY, "Health-Look", "HealthLook", "look", "hl");
    private int pingInt;

    public NameTags() {
        super("NameTags", new String[]{"nametags", "np", "nt", "tags", "plates", "nameplates", "nametag", "tag"}, 0xFFB2E665, ModuleType.RENDER);
        this.offerProperties(armor, customEnchants, itemName, health, self, scaling, width, healthLook, durability, heart, potion, ping, invis, distance, hunger, purple);
        this.listeners.add(new Listener<RenderEvent>("name_tags_render_listener") {
            @Override
            public void call(RenderEvent event) {
                for (Object o : minecraft.theWorld.playerEntities) {
                    Entity entity = (Entity) o;
                    EntityPlayer playerSelf = minecraft.thePlayer;
                    if (entity instanceof EntityPlayer) {
                        if (entity.isEntityAlive() && isEntityValid((EntityPlayer) entity)) { // TODO: renders nametag on self
                            if (entity != minecraft.thePlayer) {
                                double x = interpolate(entity.lastTickPosX, entity.posX, event.getPartialTicks()) - minecraft.getRenderManager().renderPosX;
                                double y = interpolate(entity.lastTickPosY, entity.posY, event.getPartialTicks()) - minecraft.getRenderManager().renderPosY;
                                double z = interpolate(entity.lastTickPosZ, entity.posZ, event.getPartialTicks()) - minecraft.getRenderManager().renderPosZ;
                                if (ping.getValue() && minecraft.func_175102_a().func_175104_a(entity.getName()) != null) {
                                    pingInt = minecraft.func_175102_a().func_175104_a(entity.getName()).getResponseTime();
                                }

//							if (sideHealth.getValue()) {
//								final Vector4f transformed = new Vector4f(minecraft.displayHeight * 2.0f,
//										minecraft.displayHeight * 40.0f, -50.0f, -60.0f);
//								transformed.setX((float) Math.min(transformed.getX(), minecraft.displayHeight / 2));
//								transformed.setY((float) Math.min(transformed.getY(), 10));
//								transformed.setW((float) Math.max(transformed.getW(), 20));
//								transformed.setH((float) Math.max(transformed.getH(), 40));
//								drawHealthBar(transformed, (EntityPlayer) entity);
//							}

                                renderNameTag((EntityPlayer) entity, x, y, z, event.getPartialTicks());
                            }
                            if (playerSelf.isEntityAlive() && self.getValue()) {
                                double xx = interpolate(minecraft.thePlayer.lastTickPosX, minecraft.thePlayer.posX, event.getPartialTicks()) - minecraft.getRenderManager().renderPosX;
                                double yy = interpolate(minecraft.thePlayer.lastTickPosY, minecraft.thePlayer.posY, event.getPartialTicks()) - minecraft.getRenderManager().renderPosY;
                                double zz = interpolate(minecraft.thePlayer.lastTickPosZ, minecraft.thePlayer.posZ, event.getPartialTicks()) - minecraft.getRenderManager().renderPosZ;
                                renderNameTag(playerSelf, xx, yy, zz, event.getPartialTicks());
                            }

                        }
                    }
                }
            }

        });
        this.listeners.add(new Listener<PassSpecialRenderEvent>("name_tags_pass_special_render_listener") {
            @Override
            public void call(PassSpecialRenderEvent event) {
                event.setCanceled(true);
            }
        });
        setRunning(true);
    }

    private boolean isEntityValid(EntityPlayer player) {
        if (player.isInvisible()) {
            return invis.getValue();
        }

        return true;
    }


    private void renderNameTag(EntityPlayer player, double x, double y, double z, float delta) {
        double tempY = y;
        tempY += (player.isSneaking() ? 0.5D : 0.7D);
        Entity camera = minecraft.getRenderViewEntity();
        double originalPositionX = camera.posX;
        double originalPositionY = camera.posY;
        double originalPositionZ = camera.posZ;
        camera.posX = interpolate(camera.prevPosX, camera.posX, delta);
        camera.posY = interpolate(camera.prevPosY, camera.posY, delta);
        camera.posZ = interpolate(camera.prevPosZ, camera.posZ, delta);
        double distance = camera.getDistance(x + minecraft.getRenderManager().viewerPosX, y + minecraft.getRenderManager().viewerPosY, z + minecraft.getRenderManager().viewerPosZ);
        int width = minecraft.fontRenderer.getStringWidth(this.getDisplayName(player)) / 2;
        double scale = 0.0018 + scaling.getValue() * distance;
        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset(1, -1500000);
        GlStateManager.disableLighting();
        GlStateManager.translate((float) x, (float) tempY + 1.4F, (float) z);
        GlStateManager.rotate(-minecraft.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(minecraft.getRenderManager().playerViewX, minecraft.gameSettings.thirdPersonView == 2 ? -1.0F : 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-scale, -scale, scale);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        RenderMethods.drawBorderedRectReliant(-width - 2, -(minecraft.fontRenderer.FONT_HEIGHT + 1), purple.getValue() & hasOmega(player) ? width + 32F : width + 2F, 1.5F, this.width.getValue(), 0x77000000, 0x55000000);
        GlStateManager.enableAlpha();
        if (distance <= 8) {
            scale = 0.0245D;
        }


        if (purple.getValue()) {
            if (hasOmega(player) && !Gun.getInstance().getFriendManager().isFriend(player.getName())) {
                minecraft.fontRenderer.drawStringWithShadow("\247c\247lOMEGA \2477" + this.getDisplayName(player), -width, -(minecraft.fontRenderer.FONT_HEIGHT - 1), this.getDisplayColour(player));
            } else if (Gun.getInstance().getFriendManager().isFriend(player.getName())) {
                minecraft.fontRenderer.drawString("\2477" + player.getName(), -width, -(minecraft.fontRenderer.FONT_HEIGHT - 1), ColorHelper.getColor(0, 195, 255, 255));
            } else {
                minecraft.fontRenderer.drawString("\2477" + player.getName(), -width, -(minecraft.fontRenderer.FONT_HEIGHT - 1), 0x403a3f);

            }
        } else {
            minecraft.fontRenderer.drawStringWithShadow(this.getDisplayName(player), -width, -(minecraft.fontRenderer.FONT_HEIGHT - 1), this.getDisplayColour(player));
        }
        if (armor.getValue()) {
            GlStateManager.pushMatrix();
            int xOffset = 0;

            for (int index = 3; index >= 0; index--) {
                ItemStack stack = player.inventory.armorInventory[index];

                if (stack != null) {
                    xOffset -= 8;
                }
            }

            if (player.getCurrentEquippedItem() != null) {
                xOffset -= 8;
                ItemStack renderStack = player.getCurrentEquippedItem().copy();

                if (renderStack.hasEffect()
                        && (renderStack.getItem() instanceof ItemTool || renderStack.getItem() instanceof ItemArmor)) {
                    renderStack.stackSize = 1;
                }
                if (purple.getValue()) {
                    if (hasOmega(player))
                        this.renderItemStack(renderStack, xOffset, -26);
                } else
                    this.renderItemStack(renderStack, xOffset, -26);
                xOffset += 16;
            }

            for (int index = 3; index >= 0; index--) {
                ItemStack stack = player.inventory.armorInventory[index];

                if (stack != null) {
                    ItemStack armourStack = stack.copy();

                    if (armourStack.hasEffect() && (armourStack.getItem() instanceof ItemTool
                            || armourStack.getItem() instanceof ItemArmor)) {
                        armourStack.stackSize = 1;
                    }
                    if (purple.getValue()) {
                        if (hasOmega(player))
                            this.renderItemStack(armourStack, xOffset, -26);
                    } else
                        this.renderItemStack(armourStack, xOffset, -26);
                    xOffset += 16;
                }
            }

            GlStateManager.popMatrix();
        }
//		if (potion.getValue()) {
//			int y3 = -53;
//			for (final Object o : (player).getActivePotionEffects()) {
//				final PotionEffect pot = (PotionEffect)o;
//				final String potName = StringUtils.capitalize(pot.getEffectName().substring(pot.getEffectName().lastIndexOf(".") + 1));
//				final int XD = pot.getDuration() / 20;
//				final SimpleDateFormat df = new SimpleDateFormat("m:ss");
//				final String time = df.format(XD * 1000);
//				minecraft.fontRenderer.drawStringWithShadow((XD > 0) ? (potName + " " + time) : "", -30.0f, (float)y3, -1);
//				y3 -= 8;
//			}
//		}
        if (potion.getValue()) {
            final ArrayList<ItemStack> equipped = new ArrayList<ItemStack>();
            final ItemStack hand = player.getCurrentEquippedItem();
            final ItemStack[] items = player.getInventory();
            if (hand != null) {
                equipped.add(hand);
            }
            if (items[3] != null) {
                equipped.add(items[3]);
            }
            if (items[2] != null) {
                equipped.add(items[2]);
            }
            if (items[1] != null) {
                equipped.add(items[1]);
            }
            if (items[0] != null) {
                equipped.add(items[0]);
            }
            float yp = (float) (y - 39.0) + (equipped.isEmpty() ? 16 : 0);
            for (final Object o : player.getActivePotionEffects()) {
                final PotionEffect effect = (PotionEffect) o;
                String name = I18n.format(effect.getEffectName());
                if (effect.getAmplifier() == 1) {
                    name = name + " " + I18n.format("enchantment.level.2");
                } else if (effect.getAmplifier() == 2) {
                    name = name + " " + I18n.format("enchantment.level.3");
                } else if (effect.getAmplifier() == 3) {
                    name = name + " " + I18n.format("enchantment.level.4");
                } else if (effect.getAmplifier() > 0) {
                    name = name + " " + (effect.getAmplifier() + 1);
                }
                name = String.format("%s: \2477%s", name, Potion.getDurationString(effect));
                minecraft.fontRenderer.drawStringWithShadow(effect.getDuration() > 0 ? name : "", (float) x - 30, yp, Potion.potionTypes[effect.getPotionID()].getLiquidColor());
                yp -= 8.0f;
            }
        }
        camera.posX = originalPositionX;
        camera.posY = originalPositionY;
        camera.posZ = originalPositionZ;
        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.disablePolygonOffset();
        GlStateManager.doPolygonOffset(1, 1500000);
        GlStateManager.popMatrix();
    }

    private void renderItemStack(ItemStack stack, int x, int y) {
        GlStateManager.pushMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.clear(GL11.GL_ACCUM);
        RenderHelper.enableStandardItemLighting();
        minecraft.getRenderItem().zLevel = -150.0F;
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableAlpha();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableAlpha();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        minecraft.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
        minecraft.getRenderItem().renderItemOverlays(minecraft.fontRenderer, stack, x, y);
        minecraft.getRenderItem().zLevel = 0.0F;
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableCull();
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        GlStateManager.scale(0.5F, 0.5F, 0.5F);
        GlStateManager.disableDepth();
        renderEnchantmentText(stack, x, y);
//		renderLores(stack, x, y);
        GlStateManager.enableDepth();
        GlStateManager.scale(2F, 2F, 2F);
        GlStateManager.popMatrix();
    }

    private void renderEnchantmentText(ItemStack stack, int x, int y) {
        int enchantmentY = y - 24;

        if (stack.getEnchantmentTagList() != null && stack.getEnchantmentTagList().tagCount() >= 6) {
            minecraft.fontRenderer.drawString("god", x * 2, enchantmentY, 0xFFFF5757);
            return;
        }

        int color = 0xFFFFFF;

        if (stack.getItem() instanceof ItemArmor) {
            int preCustomEnchants = enchantmentY;
            if (customEnchants.getValue()) {
                if (stack.stackTagCompound != null && stack.stackTagCompound.tagMap != null) {
                    NBTTagCompound nbtTagCompound = (NBTTagCompound) stack.stackTagCompound.tagMap.get("display");
                    if (nbtTagCompound != null) {
                        NBTTagList nbtTagList = (NBTTagList) nbtTagCompound.tagMap.get("Lore");
                        if (nbtTagList != null) {
                            for (Object nbtString : nbtTagList.tagList) {
                                if (nbtString != null) {
                                    String lore = ((NBTTagString) nbtString).getString();
                                    if (!lore.matches("[a-zA-Z]+") && !lore.equals("") && !lore.equals(" ")) {
                                        minecraft.fontRenderer.drawStringWithShadow(lore, x * 2 - 8, enchantmentY - 12, color);
                                        enchantmentY -= 9;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (itemName.getValue()) {
                if (stack.stackTagCompound != null && stack.stackTagCompound.tagMap != null) {
                    NBTTagCompound nbtTagCompound = (NBTTagCompound) stack.stackTagCompound.tagMap.get("display");
                    if (nbtTagCompound != null) {
                        NBTTagString nbtTagString = (NBTTagString) nbtTagCompound.tagMap.get("Name");
                        if (nbtTagString != null) {
                            minecraft.fontRenderer.drawStringWithShadow(nbtTagString.getString(), x * 2 - 12, enchantmentY - 12, color);
                        }
                    }
                }
            }
            enchantmentY = preCustomEnchants;

            stack.getItem().getItemStackDisplayName(stack);
            int protectionLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.PROTECTION.effectId, stack);
            int projectileProtectionLevel = EnchantmentHelper
                    .getEnchantmentLevel(Enchantment.PROJECTILE_PROTECTION.effectId, stack);
            int blastProtectionLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.BLAST_PROTECTION.effectId,
                    stack);
            int fireProtectionLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.FIRE_PROTECTION.effectId,
                    stack);
            int thornsLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.THORNS.effectId, stack);
            int featherFallingLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.FEATHER_FALLING.effectId,
                    stack);
            int unbreakingLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.UNBREAKING.effectId, stack);
            int aquaInfinityLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.AQUA_AFFINITY.effectId, stack);
            int respirationLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.RESPIRATION.effectId, stack);

            if (protectionLevel > 0) {
                minecraft.fontRenderer.drawString("pr" + protectionLevel, x * 2, enchantmentY, color);
                enchantmentY += 8;
            }

            if (unbreakingLevel > 0) {
                minecraft.fontRenderer.drawString("un" + unbreakingLevel, x * 2, enchantmentY, color);
                enchantmentY += 8;
            }

            if (projectileProtectionLevel > 0) {
                minecraft.fontRenderer.drawString("pp" + projectileProtectionLevel, x * 2, enchantmentY, color);
                enchantmentY += 8;
            }

            if (blastProtectionLevel > 0) {
                minecraft.fontRenderer.drawString("bp" + blastProtectionLevel, x * 2, enchantmentY, color);
                enchantmentY += 8;
            }

            if (fireProtectionLevel > 0) {
                minecraft.fontRenderer.drawString("fp" + fireProtectionLevel, x * 2, enchantmentY, color);
                enchantmentY += 8;
            }

            if (thornsLevel > 0) {
                minecraft.fontRenderer.drawString("tho" + thornsLevel, x * 2, enchantmentY, color);
                enchantmentY += 8;
            }

            if (featherFallingLevel > 0) {
                minecraft.fontRenderer.drawString("ff" + featherFallingLevel, x * 2, enchantmentY, color);
                enchantmentY += 8;
            }

            if (aquaInfinityLevel > 0) {
                minecraft.fontRenderer.drawString("ai" + aquaInfinityLevel, x * 2, enchantmentY, color);
                enchantmentY += 8;
            }

            if (respirationLevel > 0) {
                minecraft.fontRenderer.drawString("res" + respirationLevel, x * 2, enchantmentY, color);
                enchantmentY += 8;
            }

            if (durability.getValue()) {
                if (stack.getMaxDamage() - stack.getItemDamage() < stack.getMaxDamage()) {
                    minecraft.fontRenderer.drawString((stack.getMaxDamage() - stack.getItemDamage()) + "", x * 2,
                            enchantmentY + 2, 0xFFff9999);
                    enchantmentY += 8;
                }
            }
        }

        if (stack.getItem() instanceof ItemBow) {
            int preCustomEnchants = enchantmentY;
            if (customEnchants.getValue()) {
                if (stack.stackTagCompound != null && stack.stackTagCompound.tagMap != null) {
                    NBTTagCompound nbtTagCompound = (NBTTagCompound) stack.stackTagCompound.tagMap.get("display");
                    if (nbtTagCompound != null) {
                        NBTTagList nbtTagList = (NBTTagList) nbtTagCompound.tagMap.get("Lore");
                        if (nbtTagList != null) {
                            for (Object nbtString : nbtTagList.tagList) {
                                if (nbtString != null) {
                                    String lore = ((NBTTagString) nbtString).getString();
                                    if (!lore.matches("[a-zA-Z]+") && !lore.equals("") && !lore.equals(" ")) {
                                        minecraft.fontRenderer.drawStringWithShadow(lore, x * 2 - 8, enchantmentY - 12, color);
                                        enchantmentY -= 9;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (itemName.getValue()) {
                if (stack.stackTagCompound != null && stack.stackTagCompound.tagMap != null) {
                    NBTTagCompound nbtTagCompound = (NBTTagCompound) stack.stackTagCompound.tagMap.get("display");
                    if (nbtTagCompound != null) {
                        NBTTagString nbtTagString = (NBTTagString) nbtTagCompound.tagMap.get("Name");
                        if (nbtTagString != null) {
                            minecraft.fontRenderer.drawStringWithShadow(nbtTagString.getString(), x * 2 - 12, enchantmentY - 12, color);
                        }
                    }
                }
            }
            enchantmentY = preCustomEnchants;
            int powerLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.POWER.effectId, stack);
            int punchLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.PUNCH.effectId, stack);
            int flameLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.FLAME.effectId, stack);
            int unbreakingLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.UNBREAKING.effectId, stack);
            int infinityLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.INFINITY.effectId, stack);

            if (powerLevel > 0) {
                minecraft.fontRenderer.drawString("po" + powerLevel, x * 2, enchantmentY, color);
                enchantmentY += 8;
            }

            if (punchLevel > 0) {
                minecraft.fontRenderer.drawString("pu" + punchLevel, x * 2, enchantmentY, color);
                enchantmentY += 8;
            }

            if (flameLevel > 0) {
                minecraft.fontRenderer.drawString("fl" + flameLevel, x * 2, enchantmentY, color);
                enchantmentY += 8;
            }

            if (unbreakingLevel > 0) {
                minecraft.fontRenderer.drawString("un" + unbreakingLevel, x * 2, enchantmentY, color);
                enchantmentY += 8;
            }

            if (infinityLevel > 0) {
                minecraft.fontRenderer.drawString("i", x * 2, enchantmentY, color);
                enchantmentY += 8;
            }

        }

        if (stack.getItem() instanceof ItemPickaxe) {
            int preCustomEnchants = enchantmentY;
            if (customEnchants.getValue()) {
                if (stack.stackTagCompound != null && stack.stackTagCompound.tagMap != null) {
                    NBTTagCompound nbtTagCompound = (NBTTagCompound) stack.stackTagCompound.tagMap.get("display");
                    if (nbtTagCompound != null) {
                        NBTTagList nbtTagList = (NBTTagList) nbtTagCompound.tagMap.get("Lore");
                        if (nbtTagList != null) {
                            for (Object nbtString : nbtTagList.tagList) {
                                if (nbtString != null) {
                                    String lore = ((NBTTagString) nbtString).getString();
                                    if (!lore.matches("[a-zA-Z]+") && !lore.equals("") && !lore.equals(" ")) {
                                        minecraft.fontRenderer.drawStringWithShadow(lore, x * 2 - 8, enchantmentY - 12, color);
                                        enchantmentY -= 9;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (itemName.getValue()) {
                if (stack.stackTagCompound != null && stack.stackTagCompound.tagMap != null) {
                    NBTTagCompound nbtTagCompound = (NBTTagCompound) stack.stackTagCompound.tagMap.get("display");
                    if (nbtTagCompound != null) {
                        NBTTagString nbtTagString = (NBTTagString) nbtTagCompound.tagMap.get("Name");
                        if (nbtTagString != null) {
                            minecraft.fontRenderer.drawStringWithShadow(nbtTagString.getString(), x * 2 - 12, enchantmentY - 12, color);
                        }
                    }
                }
            }
            enchantmentY = preCustomEnchants;


            int efficiencyLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.EFFICIENCY.effectId, stack);
            int fortuneLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.FORTUNE.effectId, stack);
            int unbreakingLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.UNBREAKING.effectId, stack);
            int silkTouchLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.SILK_TOUCH.effectId, stack);


            if (efficiencyLevel > 0) {
                minecraft.fontRenderer.drawString("ef" + efficiencyLevel, x * 2, enchantmentY, color);
                enchantmentY += 8;
            }

            if (fortuneLevel > 0) {
                minecraft.fontRenderer.drawString("fo" + fortuneLevel, x * 2, enchantmentY, color);
                enchantmentY += 8;
            }

            if (unbreakingLevel > 0) {
                minecraft.fontRenderer.drawString("un" + unbreakingLevel, x * 2, enchantmentY, color);
                enchantmentY += 8;
            }

            if (silkTouchLevel > 0) {
                minecraft.fontRenderer.drawString("st" + silkTouchLevel, x * 2, silkTouchLevel, color);
                enchantmentY += 8;
            }
        }

        if (stack.getItem() instanceof ItemAxe) {
            int preCustomEnchants = enchantmentY;
            if (customEnchants.getValue()) {
                if (stack.stackTagCompound != null && stack.stackTagCompound.tagMap != null) {
                    NBTTagCompound nbtTagCompound = (NBTTagCompound) stack.stackTagCompound.tagMap.get("display");
                    if (nbtTagCompound != null) {
                        NBTTagList nbtTagList = (NBTTagList) nbtTagCompound.tagMap.get("Lore");
                        if (nbtTagList != null) {
                            for (Object nbtString : nbtTagList.tagList) {
                                if (nbtString != null) {
                                    String lore = ((NBTTagString) nbtString).getString();
                                    if (!lore.matches("[a-zA-Z]+") && !lore.equals("") && !lore.equals(" ")) {
                                        minecraft.fontRenderer.drawStringWithShadow(lore, x * 2 - 8, enchantmentY - 12, color);
                                        enchantmentY -= 9;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (itemName.getValue()) {
                if (stack.stackTagCompound != null && stack.stackTagCompound.tagMap != null) {
                    NBTTagCompound nbtTagCompound = (NBTTagCompound) stack.stackTagCompound.tagMap.get("display");
                    if (nbtTagCompound != null) {
                        NBTTagString nbtTagString = (NBTTagString) nbtTagCompound.tagMap.get("Name");
                        if (nbtTagString != null) {
                            minecraft.fontRenderer.drawStringWithShadow(nbtTagString.getString(), x * 2 - 12, enchantmentY - 12, color);
                        }
                    }
                }
            }
            enchantmentY = preCustomEnchants;

            int sharpnessLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.SHARPNESS.effectId, stack);
            int fireAspectLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.FIRE_ASPECT.effectId, stack);
            int efficiencyLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.EFFICIENCY.effectId, stack);
            int unbreakingLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.UNBREAKING.effectId, stack);
            int silkTouchLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.SILK_TOUCH.effectId, stack);

            if (sharpnessLevel > 0) {
                minecraft.fontRenderer.drawString("sh" + sharpnessLevel, x * 2, enchantmentY, sharpnessLevel >= 80 ? 0xFF1706 : color);
                enchantmentY += 8;
            }

            if (fireAspectLevel > 0) {
                minecraft.fontRenderer.drawString("fa" + fireAspectLevel, x * 2, enchantmentY, sharpnessLevel >= 80 ? 0xFF1706 : color);
                enchantmentY += 8;
            }

            if (efficiencyLevel > 0) {
                minecraft.fontRenderer.drawString("ef" + efficiencyLevel, x * 2, enchantmentY, color);
                enchantmentY += 8;
            }

            if (unbreakingLevel > 0) {
                minecraft.fontRenderer.drawString("un" + unbreakingLevel, x * 2, enchantmentY, sharpnessLevel >= 80 ? 0xFF1706 : color);
                enchantmentY += 8;
                {
                }

                if (silkTouchLevel > 0) {
                    minecraft.fontRenderer.drawString("st", x * 2, enchantmentY, color);
                    enchantmentY += 8;
                }
            }
        }

        if (stack.getItem() instanceof ItemSword) {
            int preCustomEnchants = enchantmentY;
            if (customEnchants.getValue()) {
                if (stack.stackTagCompound != null && stack.stackTagCompound.tagMap != null) {
                    NBTTagCompound nbtTagCompound = (NBTTagCompound) stack.stackTagCompound.tagMap.get("display");
                    if (nbtTagCompound != null) {
                        NBTTagList nbtTagList = (NBTTagList) nbtTagCompound.tagMap.get("Lore");
                        if (nbtTagList != null) {
                            for (Object nbtString : nbtTagList.tagList) {
                                if (nbtString != null) {
                                    String lore = ((NBTTagString) nbtString).getString();
                                    if (!lore.matches("[a-zA-Z]+") && !lore.equals("") && !lore.equals(" ")) {
                                        minecraft.fontRenderer.drawStringWithShadow(lore, x * 2 - 8, enchantmentY - 12, color);
                                        enchantmentY -= 9;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (itemName.getValue()) {
                if (stack.stackTagCompound != null && stack.stackTagCompound.tagMap != null) {
                    NBTTagCompound nbtTagCompound = (NBTTagCompound) stack.stackTagCompound.tagMap.get("display");
                    if (nbtTagCompound != null) {
                        NBTTagString nbtTagString = (NBTTagString) nbtTagCompound.tagMap.get("Name");
                        if (nbtTagString != null) {
                            minecraft.fontRenderer.drawStringWithShadow(nbtTagString.getString(), x * 2 - 12, enchantmentY - 12, color);
                        }
                    }
                }
            }
            enchantmentY = preCustomEnchants;
            int sharpnessLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.SHARPNESS.effectId, stack);
            int knockbackLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.KNOCKBACK.effectId, stack);
            int unbreakingLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.UNBREAKING.effectId, stack);
            int fireAspectLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.FIRE_ASPECT.effectId, stack);
            int smiteLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.SMITE.effectId, stack);
            int arthropodsLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.BANE_OF_ARTHROPODS.effectId, stack);

            if (sharpnessLevel > 0) {
                minecraft.fontRenderer.drawString("sh" + sharpnessLevel, x * 2, enchantmentY, sharpnessLevel >= 85 ? 0xFF1706 : color);
                enchantmentY += 8;
            }

            if (knockbackLevel > 0) {
                minecraft.fontRenderer.drawString("kn" + knockbackLevel, x * 2, enchantmentY, color);
                enchantmentY += 8;
            }

            if (fireAspectLevel > 0) {
                minecraft.fontRenderer.drawString("fa" + fireAspectLevel, x * 2, enchantmentY, color);
                enchantmentY += 8;
            }

            if (unbreakingLevel > 0) {
                minecraft.fontRenderer.drawString("un" + unbreakingLevel, x * 2, enchantmentY, color);
                enchantmentY += 8;
            }

            if (smiteLevel > 0) {
                minecraft.fontRenderer.drawString("sm" + smiteLevel, x * 2, enchantmentY, color);
                enchantmentY += 8;
            }

            if (arthropodsLevel > 0) {
                minecraft.fontRenderer.drawString("ar" + arthropodsLevel, x * 2, enchantmentY, color);
                enchantmentY += 8;
            }
        }

        if (stack.getItem() == Items.golden_apple && stack.hasEffect()) {
            minecraft.fontRenderer.drawStringWithShadow("god", x * 2, enchantmentY, 0xFFc34d41);
        }
    }

    private void drawHealthBar(final Vector4f vector, final EntityPlayer entity) {
        final int dmg = (int) Math.round(255.0 - entity.getHealth() * 255.0 / entity.getMaxHealth());
        final int dmgint = 255 - dmg << 8 | dmg << 16;
        final float hpHeight = entity.getHealth() * (vector.h - vector.y) / entity.getMaxHealth();
        RenderMethods.drawRectESP(vector.x - 3.0f, vector.y - 0.5f, vector.x - 1.0f, vector.h + 0.5f, -1726934767);
        RenderMethods.drawRectESP(vector.x - 2.5f, vector.h - hpHeight, vector.x - 1.5f, vector.h,
                ColorHelper.changeAlpha(dmgint, 255));
    }

    private String getDisplayName(EntityPlayer player) {
        if (purple.getValue() && !hasOmega(player)) {
            return player.getName();
        }
        String name = player.getDisplayName().getFormattedText();
        // int pingInt =
        // minecraft.getNetHandler().func_175104_a(player.getName()).getResponseTime();
        String heartUnicode = " \u2764";

        if (Gun.getInstance().getFriendManager().isFriend(player.getName())) {
            name = Gun.getInstance().getFriendManager().getFriendByAliasOrLabel(player.getName()).getAlias();
        }

        if (Gun.getInstance().getEnemyManager().isEnemy(player.getName())) {
            name = Gun.getInstance().getEnemyManager().getEnemyByAliasOrLabel(player.getName()).getAlias();
        }
        if (Gun.getInstance().getStaffManager().isStaff((player.getName()))) {
            name = Gun.getInstance().getStaffManager().getStaffByAliasOrLabel(player.getName()).getAlias();
        }

        if (name.contains(minecraft.getSession().getUsername())) {
            name = "You";
        }

        if (ping.getValue()) {
            name += " [" + pingInt + "ms]";
        }


        float health = player.getHealth();
        EnumChatFormatting color;
        EnumChatFormatting color1;
        if (health > 18) {
            color = EnumChatFormatting.GREEN;
        } else if (health > 16) {
            color = EnumChatFormatting.DARK_GREEN;
        } else if (health > 12) {
            color = EnumChatFormatting.YELLOW;
        } else if (health > 8) {
            color = EnumChatFormatting.GOLD;
        } else if (health > 5) {
            color = EnumChatFormatting.RED;
        } else {
            color = EnumChatFormatting.DARK_RED;
        }

        if (this.health.getValue()) {
            switch (healthLook.getValue()) {
                case HUNDRED:
                    health = health * 5;
                    break;

                case TWENTY:
                    break;

                case TEN:
                    health = health / 2;
                    break;
            }

        if (Math.floor(health) == health) {
            name = name + color + " " + (health > 0 ? (int) Math.floor(health) : "dead");
        } else {
            name = name + color + " " + (health > 0 ? (int) health : "dead");
        }

        if (healthLook.getValue() == Health.HUNDRED) {
            name += "%";
        }

        if (healthLook.getValue() == Health.TEN && heart.getValue()){
            name += heartUnicode;
        }
    }

        float hungerLevel = player.getFoodStats().getFoodLevel();
        EnumChatFormatting hungerColor;
        if (hungerLevel > 6) {
            hungerColor = EnumChatFormatting.GREEN;
        } else if (hungerLevel <= 6) {
            hungerColor = EnumChatFormatting.RED;
        } else {
            hungerColor = EnumChatFormatting.YELLOW;
        }
        if (hunger.getValue()) {
            name += hungerColor + " " + hungerLevel / 2;
        }

        if (!hunger.getValue()) {
            return name;
        }
        return name;
    }

    private void renderLores(ItemStack stack, int x, int y) {
        int enchantmentY = y - 24;


        int color = 0xFFFFFF;

        if (stack.getItem() instanceof Item) {
            if (stack.hasTagCompound()) {
                NBTTagCompound nbtTagCompound = new NBTTagCompound();
                //stack.getTagCompound(nbtTagCompound).getTag();

            }
        }
    }

    private int getDisplayColour(EntityPlayer player) {
        int color = 0xFFAAAAAA;

        if (Gun.getInstance().getFriendManager().isFriend(player.getName())) {
            return 0xFF55C0ED;
        } else if (player.isInvisible()) {
            color = 0xFFef0147;
        } else if (player.isSneaking()) {
            color = 0xFF9d1995;
        } else if (Gun.getInstance().getEnemyManager().isEnemy(player.getName())) {
            color = 0xf442e8;
        } else if (Gun.getInstance().getStaffManager().isStaff(player.getName())) {
            color = 0xFFDD2E;
        }

        return color;
    }

    private boolean hasOmega(EntityPlayer player) {
        if (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof ItemAxe) {
            int sharpnessLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.SHARPNESS.effectId, player.getCurrentEquippedItem());
            return sharpnessLevel >= 80;
        }
        return false;
    }

    private double interpolate(double previous, double current, float delta) {
        return (previous + (current - previous) * delta);
    }

    public enum Health {
        HUNDRED, TWENTY, TEN
    }

}
