package me.comu.client.module.impl.toggle.render;

import me.comu.api.event.Listener;
import me.comu.api.minecraft.render.RenderMethods;
import me.comu.client.events.InputEvent;
import me.comu.client.events.RenderEvent;
import me.comu.client.logging.Logger;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.Property;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.item.ItemAxe;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.Explosion;

public final class WorldeditESP extends ToggleableModule {
    public static Property<Boolean> explosions = new Property<>(true, "Explosions", "tnt", "e", "bomb", "bombs");
    private Position position1, position2;

    public WorldeditESP() {
        super("WorldeditESP", new String[]{"worldeditesp", "wee", "weesp"}, 0xFFD4BB90, ModuleType.RENDER);
        this.offerProperties(explosions);
        this.listeners.add(new Listener<RenderEvent>("worldedit_esp_render_listener") {
            @Override
            public void call(RenderEvent event) {
                if (explosions.getValue()) {
                    if (Explosion.affectedBlockPositions != null && !Explosion.affectedBlockPositions.isEmpty()) {
                        for (Object objBlock : Explosion.affectedBlockPositions) {
                            BlockPos blockPos = (BlockPos) objBlock;
                                RenderMethods.enableGL3D();
                                double x = blockPos.getX() - minecraft.getRenderManager().viewerPosX;
                                double y = blockPos.getY() - minecraft.getRenderManager().viewerPosY;
                                double z = blockPos.getZ() - minecraft.getRenderManager().viewerPosZ;
                                AxisAlignedBB boundingBox = AxisAlignedBB.fromBounds(x, y, z, x + 1.0D, y + 1.0D, z + 1.0D);
                                GlStateManager.color(0.0F, 0.0F, 1.0F, 0.3F);
                                RenderGlobal.drawOutlinedBoundingBox(boundingBox, -1);
                                GlStateManager.color(0.0f, 0.0f, 1f, 0.2F);
                                RenderMethods.drawBox(boundingBox);
                                RenderMethods.disableGL3D();
                        }

                    }
                }

                GlStateManager.pushMatrix();
                RenderMethods.enableGL3D();

                if (position1 != null && position2 != null) {
                    double x = position1.getX() - minecraft.getRenderManager().renderPosX;
                    double y = position1.getY() - minecraft.getRenderManager().renderPosY;
                    double z = position1.getZ() - minecraft.getRenderManager().renderPosZ;
                    double x1 = position2.getX() - minecraft.getRenderManager().renderPosX;
                    double y1 = position2.getY() - minecraft.getRenderManager().renderPosY;
                    double z1 = position2.getZ() - minecraft.getRenderManager().renderPosZ;
                    AxisAlignedBB axisAlignedBB = new AxisAlignedBB(x, y, z, x1, y1, z1);
                    GlStateManager.color(0.1F, 0.1F, 0.3F, 1F);
                    RenderMethods.renderCrosses(axisAlignedBB);
                    RenderMethods.drawOutlinedBox(axisAlignedBB);
                }

                RenderMethods.disableGL3D();
                GlStateManager.popMatrix();
            }
        });
        this.listeners.add(new Listener<InputEvent>("worldedit_esp_input_listener") {
            @Override
            public void call(InputEvent event) {
                switch (event.getType()) {
                    case MOUSE_LEFT_CLICK:
                        if (minecraft.thePlayer.inventory.getCurrentItem() != null) {
                            if (minecraft.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemAxe) {
                                if (minecraft.objectMouseOver != null) {
                                    if (minecraft.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                                        position1 = new Position(minecraft.objectMouseOver.getPos().getX(), minecraft.objectMouseOver.getPos().getY(), minecraft.objectMouseOver.getPos().getZ());
                                        Logger.getLogger().printToChat("pos1 set to " + position1.x + ", " + position1.y + ", " + position1.z + ".");
                                    }
                                }
                            }
                        }

                        break;

                    case MOUSE_RIGHT_CLICK:
                        if (minecraft.thePlayer.inventory.getCurrentItem() != null) {
                            if (minecraft.thePlayer.inventory.getCurrentItem().getItem() instanceof ItemAxe) {
                                if (minecraft.objectMouseOver != null) {
                                    if (minecraft.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                                        position2 = new Position(minecraft.objectMouseOver.getPos().getX(), minecraft.objectMouseOver.getPos().getY() + 1, minecraft.objectMouseOver.getPos().getZ());
                                        Logger.getLogger().printToChat("pos2 set to " + position2.x + ", " + position2.y + ", " + position2.z + ".");
                                    }
                                }
                            }
                        }

                        break;

                    case KEYBOARD_KEY_PRESS:
                        break;

                    case MOUSE_MIDDLE_CLICK:
                        break;

                    default:
                        break;
                }
            }
        });
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        position1 = position2 = null;
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        position1 = position2 = null;
    }

    public class Position {
        private int x, y, z;

        public Position(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getZ() {
            return z;
        }
    }
}
