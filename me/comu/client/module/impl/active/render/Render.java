package me.comu.client.module.impl.active.render;

import me.comu.api.event.Listener;
import me.comu.api.minecraft.render.RenderMethods;
import me.comu.client.core.Gun;
import me.comu.client.events.RenderEvent;
import me.comu.client.events.RenderGameInfoEvent;
import me.comu.client.events.RenderGameOverlayEvent;
import me.comu.client.events.ViewmodelEvent;
import me.comu.client.module.Module;
import me.comu.client.properties.EnumProperty;
import me.comu.client.properties.NumberProperty;
import me.comu.client.properties.Property;
import me.comu.client.utils.ColorHelper;
import me.comu.client.utils.GuiUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSign;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;


public final class Render extends Module {
    private final Property<Boolean> rotations = new Property<Boolean>(true, "Rotations", "f5", "realyaw", "realpitch", "rotationset", "rotation", "realrotation"), chunkBorders = new Property<>(false, "Chunks", "chunk", "chunkborders", "chunkborder"), customBars = new Property<>(true, "CustomBars", "c", "custom", "cb"), blockpos = new Property<>(false, "BlockPos", "block", "blockoutline", "blockfill"), blind = new Property<>(true, "Blidnness", "blind"), blockAnimation = new Property<>(true, "BlockAnimation", "ba", "block"), hitAnimation = new Property<>(true, "HitAnimation", "ha", "hit"), nofov = new Property<>(false, "NoFOV", "nf"), pumpkin = new Property<>(true, "NoPumpkin", "p", "np"), fire = new Property<>(true, "NoFire", "fire", "nf"), hurtcam = new Property<>(false, "NoHurtcam", "hurtcam", "nh"), arrows = new Property<>(true, "Arrows", "Arrow", "arr", "a"), itemPhysics = new Property<>(false, "ItemPhysics", "physics", "physic", "itemphysic"), notifications = new Property<>(true, "Notifications", "notif", "notification", "notifs"), keystrokes = new Property<>(false, "Keystrokes", "Key-strokes", "strokes");
    public final Property<Float> blockHeight = new NumberProperty<>(-0.2F, -1.5F, 1.5F, 0.1f, "BlockHeight");
    private final EnumProperty<BlockMode> blockMode = new EnumProperty<>(BlockMode.VANILLA, "BlockMode", "Mode", "bmode", "m");

    public Render() {
        super("Render", new String[]{"render","nr"});
        this.offerProperties(itemPhysics, rotations, pumpkin, blockpos, customBars, fire, hurtcam, nofov, blockAnimation, blockHeight, notifications, blockMode, hitAnimation, chunkBorders);
        Gun.getInstance().getEventManager().register(new Listener<RenderGameOverlayEvent>("norender_render_game_overlay_listener") {
            @Override
            public void call(RenderGameOverlayEvent event) {
                switch (event.getType()) {
                    case FIRE:
                        event.setRenderFire(fire.getValue());
                        break;
                    case HURTCAM:
                        event.setRenderHurtcam(hurtcam.getValue());
                        break;
                    case PUMPKIN:
                        event.setRenderPumpkin(pumpkin.getValue());
                        break;
                }

            }
        });
        Gun.getInstance().getEventManager().register(new Listener<RenderEvent>("textgui_render_game_info_listener") {
            @Override
            public void call(RenderEvent event) {
                if (blockpos.getValue()) {
                    GL11.glPushMatrix();
                    RenderMethods.enableGL3D();
                    final int x = getBlinkBlock().getPos().getX();
                    final int y = getBlinkBlock().getPos().getY();
                    final int z = getBlinkBlock().getPos().getZ();
                    final Block block1 = getBlock(x, y, z);
                    final Block block2 = getBlock(x, y + 1, z);
                    final Block block3 = getBlock(x, y + 2, z);
                    int playerX = (int)minecraft.thePlayer.posX;
                    int playerZ = (int)minecraft.thePlayer.posZ;
                    final boolean blockBelow = !(block1 instanceof BlockSign) && block1.getMaterial().isSolid();
                    final boolean blockLevel = !(block2 instanceof BlockSign) && block1.getMaterial().isSolid();
                    final boolean blockAbove = !(block3 instanceof BlockSign) && block1.getMaterial().isSolid();
                    if (getBlock(getBlinkBlock().getPos()).getMaterial() != Material.air && blockBelow && blockLevel && blockAbove) {
                            float[] color = ColorHelper.getRGBA(GuiUtils.rainbow(3));
                            GL11.glColor4f(color[0], color[1], color[2], 1.0f);
                            AxisAlignedBB boundingBox = AxisAlignedBB.fromBounds(x - RenderManager.renderPosX, y - RenderManager.renderPosY, z - RenderManager.renderPosZ, x - RenderManager.renderPosX + 1.0D, y - RenderManager.renderPosY + 1.0D, z - RenderManager.renderPosZ + 1.0D);
                            GlStateManager.color(color[0], color[1], color[2], 0.6F);
                            RenderGlobal.drawOutlinedBoundingBox(boundingBox, -1);
                            GlStateManager.color(color[0], color[1], color[2], 0.2F);
                            RenderMethods.drawBox(boundingBox);
//                            ClientUtils.drawOutlinedBox((AxisAlignedBB.fromBounds(x - RenderManager.renderPosX, y - RenderManager.renderPosY, z - RenderManager.renderPosZ, x - RenderManager.renderPosX + 1.0, y + 1 - RenderManager.renderPosY, z - RenderManager.renderPosZ + 1.0)));

                    }
                    RenderMethods.disableGL3D();
                    GL11.glPopMatrix();
                }
                if (chunkBorders.getValue() && minecraft.theWorld != null) {
                    for (int chunkZ = -2; chunkZ <= 2; ++chunkZ) {
                        for (int chunkX = -2; chunkX <= 2; ++chunkX) {
                            if (Math.abs(chunkX) != 2 || Math.abs(chunkZ) != 2) {
                                double x = chunkX * 16;
                                double z = chunkZ * 16;
                                GlStateManager.pushMatrix();
                                RenderMethods.enableGL3D();
                                AxisAlignedBB b1 = AxisAlignedBB.fromBounds(x, 0.0, z, x, 256.0, z);
                                AxisAlignedBB b2 = AxisAlignedBB.fromBounds(x + 16.0, 0.0, z, x + 16.0, 256.0, z);
                                AxisAlignedBB b3 = AxisAlignedBB.fromBounds(x, 0.0, z + 16.0, x, 256.0, z + 16.0);
                                AxisAlignedBB b4 = AxisAlignedBB.fromBounds(x + 16.0, 0.0, z + 16.0, x + 16.0, 256.0, z + 16.0);
                                GlStateManager.color(1.0F, 0.0F, 0.0F, 0.3F);
                                GL11.glLineWidth(3);
                                GL11.glLoadIdentity();
                                minecraft.gameSettings.viewBobbing = false;
                                minecraft.entityRenderer.orientCamera(event.getPartialTicks());
                                GL11.glBegin(GL11.GL_LINES);
                                GL11.glVertex3d(x, 0.0, z);
                                GL11.glVertex3d(x, 256.0, z);
                                GL11.glVertex3d(x + 16.0, 0.0, z);
                                GL11.glVertex3d(x + 16.0, 256.0, z);
                                GL11.glVertex3d(x, 0.0, z + 16.0);
                                GL11.glVertex3d(x, 256.0, z + 16.0);
                                GL11.glVertex3d(x + 16.0, 0.0, z + 16.0);
                                GL11.glVertex3d(x + 16.0, 256.0, z + 16.0);
                                GL11.glEnd();
//                                    RenderGlobal.drawOutlinedBoundingBox(b1, -1);
//                                    RenderGlobal.drawOutlinedBoundingBox(b2, -1);
//                                    RenderGlobal.drawOutlinedBoundingBox(b3, -1);
//                                    RenderGlobal.drawOutlinedBoundingBox(b4, -1);
                                RenderMethods.disableGL3D();
                                GlStateManager.popMatrix();
                            }
                        }
                    }
                }

            }
        });

        Gun.getInstance().getEventManager().register(new Listener<RenderGameInfoEvent>("textgui_render_game_info_listener") {
            @Override
            public void call(RenderGameInfoEvent event) {
                ScaledResolution scaledResolution = event.getScaledResolution();
//                if (arrows.getValue()) {
//                	int arrow = 0;
//                	 for (int index = 9; index < 45; index++)
//                     {
//                         ItemStack itemStack = minecraft.thePlayer.inventoryContainer.getSlot(index).getStack();
//                         if (itemStack == null)
//                        	 continue;
//                         if (itemStack.getDisplayName().equalsIgnoreCase("Arrow")) {
//                        	 arrow += itemStack.stackSize;
//
//                         }
//
//                     }
//                	if (minecraft.thePlayer.getCurrentEquippedItem() != null && minecraft.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemBow)
//                	minecraft.fontRenderer.drawStringWithShadow(Integer.toString(arrow), scaledResolution.getScaledWidth() / 2 + 100, scaledResolution.getScaledHeight() / 2 + 330, 0xFFFFFFFF);
//                }
                if (customBars.getValue()) {
                    int minusHealth = (int) minecraft.thePlayer.getHealth();
                    int minusFood = minecraft.thePlayer.getFoodStats().getFoodLevel();

                    if (minusFood > 20) {
                        minusFood = 20;
                    }

                    if (minusHealth > 20) {
                        minusHealth = 20;
                    }

                    // health
                    RenderMethods.drawGradientBorderedRect(scaledResolution.getScaledWidth() / 2 - 91, scaledResolution.getScaledHeight() - 39, scaledResolution.getScaledWidth() / 2 - 10, scaledResolution.getScaledHeight() - 30, 1F, 0xDD000000, 0xFF444444, 0xFF222222);
                    RenderMethods.drawGradientBorderedRect(scaledResolution.getScaledWidth() / 2 - 91, scaledResolution.getScaledHeight() - 39, scaledResolution.getScaledWidth() / 2 - 90 + minusHealth * 4, scaledResolution.getScaledHeight() - 30, 1F, 0xDD000000, 0xDD990000, 0xDD440000);
                    minecraft.fontRenderer.drawStringWithShadow(String.format("%s", (int) minecraft.thePlayer.getHealth()), scaledResolution.getScaledWidth() / 2 - 56, scaledResolution.getScaledHeight() - 38, 0xFFFFFFFF);
                    // food
                    RenderMethods.drawGradientBorderedRect(scaledResolution.getScaledWidth() / 2 + 10, scaledResolution.getScaledHeight() - 39, scaledResolution.getScaledWidth() / 2 + 91, scaledResolution.getScaledHeight() - 30, 1F, 0xDD000000, 0xFF444444, 0xFF222222);
                    RenderMethods.drawGradientBorderedRect(scaledResolution.getScaledWidth() / 2 + 90 - minusFood * 4, scaledResolution.getScaledHeight() - 39, scaledResolution.getScaledWidth() / 2 + 91, scaledResolution.getScaledHeight() - 30, 1F, 0xDD000000, 0xDD09BA00, 0xDD056300);
                    minecraft.fontRenderer.drawStringWithShadow(String.format("%s", minecraft.thePlayer.getFoodStats().getFoodLevel()), scaledResolution.getScaledWidth() / 2 + 45, scaledResolution.getScaledHeight() - 38, 0xFFFFFFFF);
                }

                if (blockAnimation.getValue()) {
                    ItemRenderer.blockHeigh = blockHeight.getValue();
                    ItemRenderer.blockAnimation = true;
                } else {
                    ItemRenderer.blockAnimation = false;
                    ItemRenderer.blockHeigh = -0.2F;
                }
                if (hitAnimation.getValue()) {
                    ItemRenderer.hitAnimation = true;
                } else {
                    ItemRenderer.hitAnimation = false;
                }
                if (keystrokes.getValue()) {
                    final boolean forward = minecraft.gameSettings.keyBindForward.getIsKeyPressed();
                    final boolean back = minecraft.gameSettings.keyBindBack.getIsKeyPressed();
                    final boolean left = minecraft.gameSettings.keyBindLeft.getIsKeyPressed();
                    final boolean right = minecraft.gameSettings.keyBindRight.getIsKeyPressed();
                    final boolean jump = minecraft.gameSettings.keyBindJump.getIsKeyPressed();
                    int width = (int) minecraft.fontRenderer.getStringWidth(Keyboard.getKeyName(minecraft.gameSettings.keyBindBack.getKeyCode()));
                    RenderMethods.drawUnfilledRect(scaledResolution.getScaledWidth() - 63.0f, scaledResolution.getScaledHeight() - 55.0f, 19, 19, ColorHelper.getColor(200, 200, 200, 190), 1.0);
                    Gui.drawRect(scaledResolution.getScaledWidth() - 62.0f, scaledResolution.getScaledHeight() - 54.0f, scaledResolution.getScaledWidth() - 44.0f, scaledResolution.getScaledHeight() - 36.0f, back ? ColorHelper.getColor(Color.RED.getRGB(), Color.GREEN.getRGB(), Color.BLUE.getRGB(), 209) : ColorHelper.getColor(Color.RED.getRGB(), Color.GREEN.getRGB(), Color.BLUE.getRGB(), 109));
                    minecraft.fontRenderer.drawStringWithShadow(Keyboard.getKeyName(minecraft.gameSettings.keyBindBack.getKeyCode()), scaledResolution.getScaledWidth() - (51.0f + width / 1.5f), (float) (scaledResolution.getScaledHeight() - 48), back ? ColorHelper.getColor(255, 255, 255) : ColorHelper.getColor(200, 200, 200));
                    width = (int) minecraft.fontRenderer.getStringWidth(Keyboard.getKeyName(minecraft.gameSettings.keyBindForward.getKeyCode()));
                    RenderMethods.drawUnfilledRect(scaledResolution.getScaledWidth() - 63.0f, scaledResolution.getScaledHeight() - 78.0f, 19, 19, ColorHelper.getColor(200, 200, 200, 190), 1.0);
                    Gui.drawRect(scaledResolution.getScaledWidth() - 62.0f, scaledResolution.getScaledHeight() - 77.0f, scaledResolution.getScaledWidth() - 44.0f, scaledResolution.getScaledHeight() - 59.0f, forward ? ColorHelper.getColor(Color.RED.getRGB(), Color.GREEN.getRGB(), Color.BLUE.getRGB(), 209) : ColorHelper.getColor(Color.RED.getRGB(), Color.GREEN.getRGB(), Color.BLUE.getRGB(), 109));
                    minecraft.fontRenderer.drawStringWithShadow(Keyboard.getKeyName(minecraft.gameSettings.keyBindForward.getKeyCode()), scaledResolution.getScaledWidth() - (51.0f + width / 1.4f), (float) (scaledResolution.getScaledHeight() - 71), forward ? ColorHelper.getColor(255, 255, 255) : ColorHelper.getColor(200, 200, 200));
                    width = (int) minecraft.fontRenderer.getStringWidth(Keyboard.getKeyName(minecraft.gameSettings.keyBindLeft.getKeyCode()));
                    RenderMethods.drawUnfilledRect(scaledResolution.getScaledWidth() - 86.0f, scaledResolution.getScaledHeight() - 55.0f, 19, 19, ColorHelper.getColor(200, 200, 200, 190), 1.0);
                    Gui.drawRect(scaledResolution.getScaledWidth() - 85.0f, scaledResolution.getScaledHeight() - 54.0f, scaledResolution.getScaledWidth() - 67.0f, scaledResolution.getScaledHeight() - 36.0f, left ? ColorHelper.getColor(Color.RED.getRGB(), Color.GREEN.getRGB(), Color.BLUE.getRGB(), 209) : ColorHelper.getColor(Color.RED.getRGB(), Color.GREEN.getRGB(), Color.BLUE.getRGB(), 109));
                    minecraft.fontRenderer.drawStringWithShadow(Keyboard.getKeyName(minecraft.gameSettings.keyBindLeft.getKeyCode()), scaledResolution.getScaledWidth() - (74.0f + width / 1.4f), (float) (scaledResolution.getScaledHeight() - 48), left ? ColorHelper.getColor(255, 255, 255) : ColorHelper.getColor(200, 200, 200));
                    width = (int) minecraft.fontRenderer.getStringWidth(Keyboard.getKeyName(minecraft.gameSettings.keyBindRight.getKeyCode()));
                    RenderMethods.drawUnfilledRect(scaledResolution.getScaledWidth() - 40.0f, scaledResolution.getScaledHeight() - 55.0f, 19, 19, ColorHelper.getColor(200, 200, 200, 190), 1.0);
                    Gui.drawRect(scaledResolution.getScaledWidth() - 39.0f, scaledResolution.getScaledHeight() - 54.0f, scaledResolution.getScaledWidth() - 21.0f, scaledResolution.getScaledHeight() - 36.0f, right ? ColorHelper.getColor(Color.RED.getRGB(), Color.GREEN.getRGB(), Color.BLUE.getRGB(), 209) : ColorHelper.getColor(Color.RED.getRGB(), Color.GREEN.getRGB(), Color.BLUE.getRGB(), 109));
                    minecraft.fontRenderer.drawStringWithShadow(Keyboard.getKeyName(minecraft.gameSettings.keyBindRight.getKeyCode()), scaledResolution.getScaledWidth() - (28.0f + width / 1.4f), (float) (scaledResolution.getScaledHeight() - 48), right ? ColorHelper.getColor(255, 255, 255) : ColorHelper.getColor(200, 200, 200));
                    RenderMethods.drawUnfilledRect(scaledResolution.getScaledWidth() - 71.0f, scaledResolution.getScaledHeight() - 31.0f, 35, 16, ColorHelper.getColor(200, 200, 200, 190), 1.0);
                    Gui.drawRect(scaledResolution.getScaledWidth() - 70.0f, scaledResolution.getScaledHeight() - 30.0f, scaledResolution.getScaledWidth() - 36.0f, scaledResolution.getScaledHeight() - 15.0f, jump ? ColorHelper.getColor(Color.RED.getRGB(), Color.GREEN.getRGB(), Color.BLUE.getRGB(), 209) : ColorHelper.getColor(Color.RED.getRGB(), Color.GREEN.getRGB(), Color.BLUE.getRGB(), 109));
                    minecraft.fontRenderer.drawStringWithShadow("Space", (float) (scaledResolution.getScaledWidth() - 67), (float) (scaledResolution.getScaledHeight() - 28), jump ? ColorHelper.getColor(255, 255, 255) : ColorHelper.getColor(200, 200, 200));
                }
            }
        });
        Gun.getInstance().getEventManager().register(new Listener<ViewmodelEvent>("textgui_view_model_listener") {
            @Override
            public void call(ViewmodelEvent event) {
                if (nofov.getValue()) {
                    event.setCanceled(true);
                }
            }
        });


    }

    public MovingObjectPosition getBlinkBlock() {
        final Vec3 var4 = minecraft.thePlayer.func_174824_e(minecraft.timer.renderPartialTicks);
        final Vec3 var5 = minecraft.thePlayer.getLook(minecraft.timer.renderPartialTicks);
        final Vec3 var6 = var4.addVector(var5.xCoord * 70.0, var5.yCoord * 70.0, var5.zCoord * 70.0);
        return minecraft.thePlayer.worldObj.rayTraceBlocks(var4, var6, false, false, true);
    }

    private Block getBlock(final int x, final int y, final int z) {
        return minecraft.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
    }

    private Block getBlock(final BlockPos pos) {
        return minecraft.theWorld.getBlockState(pos).getBlock();
    }

    public enum BlockMode {
        VANILLA, SWEEP, TAP
    }
}
