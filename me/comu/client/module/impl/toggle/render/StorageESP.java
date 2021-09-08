package me.comu.client.module.impl.toggle.render;

import me.comu.client.core.Gun;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

import me.comu.api.event.Listener;
import me.comu.api.minecraft.render.RenderMethods;
import me.comu.client.events.RenderEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.presets.Preset;
import me.comu.client.properties.NumberProperty;
import me.comu.client.properties.Property;

public final class StorageESP extends ToggleableModule
{
    private final Property<Boolean> tags = new Property<>(true, "NameTags", "plates", "tags", "labels", "np", "nameplates", "nametag", "nt"), outline = new Property(true, "Outline", "outline", "o"), lines = new Property<>(true, "Lines", "line", "l"), chest = new Property<>(true, "Chests", "chest", "c"), enderchest = new Property<>(true, "EnderChests", "enderchest", "echest","ender","ec");
    private final NumberProperty<Float> scaling = new NumberProperty<>(0.0030F, 0.001F, 0.0100F, 0.001F, "Scaling", "scale", "s"), width = new NumberProperty<>(1.8F, 1F, 5F, 0.1F, "Width", "w");

    public StorageESP()
    {
        super("StorageESP", new String[] {"storageesp", "cesp", "sesp", "chestesp"}, 0xFF696969, ModuleType.RENDER);
        this.offerProperties(tags, chest, enderchest, lines, width, outline);
        this.offsetPresets(new Preset("Cluster")
        
        {
            @Override
            public void onSet()
            {
                tags.setValue(true);
                lines.setValue(true);
            }
        }, new Preset("Minimal")
        {
            @Override
            public void onSet()
            {
                tags.setValue(false);
                lines.setValue(false);
            }
        });
        this.listeners.add(new Listener<RenderEvent>("chest_esp_render_listener")
        {
            @Override
            public void call(RenderEvent event)
            {
            	
                GlStateManager.pushMatrix();
                RenderMethods.enableGL3D();

                for (Object object : minecraft.theWorld.loadedTileEntityList)
                {
                    TileEntity tileEntity = (TileEntity) object;

                    if (shouldDraw(tileEntity))
                    {
                        double x = tileEntity.getPos().getX() - minecraft.getRenderManager().renderPosX;
                        double y = tileEntity.getPos().getY() - minecraft.getRenderManager().renderPosY;
                        double z = tileEntity.getPos().getZ() - minecraft.getRenderManager().renderPosZ;
                        float[] color = getColor(tileEntity);
                        AxisAlignedBB box = new AxisAlignedBB(x, y, z, x + 1D, y + 1D, z + 1D);

                        if (tileEntity instanceof TileEntityChest)
                        {
                            TileEntityChest chest = TileEntityChest.class.cast(tileEntity);

                            if (chest.adjacentChestZPos != null)
                            {
                                box = new AxisAlignedBB(x + 0.0625, y, z + 0.0625, x + 0.9375, y + 0.875, z + 1.9375);
                            }
                            else if (chest.adjacentChestXPos != null)
                            {
                                box = new AxisAlignedBB(x + 0.0625, y, z + 0.0625, x + 1.9375, y + 0.875, z + 0.9375);
                            }
                            else if (chest.adjacentChestZPos == null && chest.adjacentChestXPos == null && chest.adjacentChestZNeg == null && chest.adjacentChestXNeg == null)
                            {
                                box = new AxisAlignedBB(x + 0.0625, y, z + 0.0625, x + 0.9375, y + 0.875, z + 0.9375);
                            }
                            else
                            {
                                continue;
                            }
                        }
                        else if (tileEntity instanceof TileEntityEnderChest)
                        {
                            box = new AxisAlignedBB(x + 0.0625, y, z + 0.0625, x + 0.9375, y + 0.875, z + 0.9375);
                        }

                        GlStateManager.color(color[0], color[1], color[2], 0.45F);
                        boolean bobbing = minecraft.gameSettings.viewBobbing;

                        if (lines.getValue())
                        {
                            GlStateManager.pushMatrix();
                            GL11.glLineWidth(width.getValue());
                            GL11.glLoadIdentity();
                            minecraft.gameSettings.viewBobbing = false;
                            minecraft.entityRenderer.orientCamera(event.getPartialTicks());
                            GL11.glBegin(GL11.GL_LINES);
                            GL11.glVertex3d(0, minecraft.thePlayer.getEyeHeight(), 0);
                            GL11.glVertex3d(x + 0.5D, y, z + 0.5D);
                            GL11.glEnd();
                            GlStateManager.popMatrix();
                        }

                        if (outline.getValue())
                        {
                            renderOne();
                            GlStateManager.color(color[0], color[1], color[2], 0.45F);
                            RenderMethods.drawBox(box);
                            renderTwo();
                            GlStateManager.color(color[0], color[1], color[2], 0.45F);
                            RenderMethods.drawBox(box);
                            renderThree();
                            GlStateManager.color(color[0], color[1], color[2], 0.45F);
                            RenderMethods.drawBox(box);
                            renderFour();
                            GlStateManager.color(color[0], color[1], color[2], 0.45F);
                            RenderMethods.drawBox(box);
                            renderFive();
                            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                        }
                        else
                        {
                            RenderMethods.drawBox(box);
                            RenderMethods.renderCrosses(box);
                            RenderMethods.drawOutlinedBox(box);
                        }

                        minecraft.gameSettings.viewBobbing = bobbing;
                    }
                }

                for (Object object : minecraft.theWorld.loadedTileEntityList)
                {
                    TileEntity tileEntity = (TileEntity) object;

                    if (shouldDraw(tileEntity))
                    {
                        double x = tileEntity.getPos().getX() + 0.5D - minecraft.getRenderManager().renderPosX;
                        double y = tileEntity.getPos().getY() - 1D - minecraft.getRenderManager().renderPosY;
                        double z = tileEntity.getPos().getZ() + 0.5D - minecraft.getRenderManager().renderPosZ;

                        if (tags.getValue())
                        {
                            GlStateManager.pushMatrix();
                            renderTileEntityNameTag(tileEntity, x, y, z);
                            GlStateManager.popMatrix();
                        }
                    }
                }

                RenderMethods.disableGL3D();
                GlStateManager.popMatrix();
            }
        });
    }

    private boolean shouldDraw(TileEntity tileEntity)
    {
        return (tileEntity instanceof TileEntityChest && chest.getValue()) || (tileEntity instanceof TileEntityEnderChest && enderchest.getValue());
    }

    private float[] getColor(TileEntity tileEntity)
    {
        if (tileEntity instanceof TileEntityChest)
        {
            Block block = tileEntity.getBlockType();

            if (block == Blocks.chest)
            {
                return new float[] {0.8F, 0.7F, 0.22F};
            }
            else if (block == Blocks.trapped_chest)
            {
                return new float[] {0.8F, 0.22F, 0.22F};
            }
        }

        if (tileEntity instanceof TileEntityEnderChest)
        {
            return new float[] {1, 0, 1F};
        }

        return new float[] {1, 1, 1};
    }

    private void renderTileEntityNameTag(TileEntity tileEntity, double x, double y, double z)
    {
        double tempY = y;
        tempY += 0.7D;
        double distance = minecraft.getRenderViewEntity().getDistance(x + minecraft.getRenderManager().viewerPosX, y + minecraft.getRenderManager().viewerPosY,
                          z + minecraft.getRenderManager().viewerPosZ);
        int width = minecraft.fontRenderer.getStringWidth(this.getDisplayName(tileEntity)) / 2 + 1;
        double scale = 0.0018 + scaling.getValue() * distance;

        if (distance <= 8)
        {
            scale = 0.0245D;
        }

        GlStateManager.pushMatrix();
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset(1, -1500000);
        GlStateManager.disableLighting();
        GlStateManager.translate((float) x, (float) tempY + 1.4F, (float) z);
        GlStateManager.rotate(-minecraft.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(minecraft.getRenderManager().playerViewX, minecraft.gameSettings.thirdPersonView == 2 ? -1.0F : 1.0F,
                              0.0F, 0.0F);
        GlStateManager.scale(-scale, -scale, scale);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        RenderMethods.drawBorderedRectReliant(-width - 2, -(minecraft.fontRenderer.FONT_HEIGHT + 1), width, 1.5F, 1.6F, 0x77000000, 0x55000000);
        GlStateManager.enableAlpha();
        minecraft.fontRenderer.drawStringWithShadow(this.getDisplayName(tileEntity), -width,
                -(minecraft.fontRenderer.FONT_HEIGHT - 1), 0xFFFFFFFF);
        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.disablePolygonOffset();
        GlStateManager.doPolygonOffset(1, 1500000);
        GlStateManager.popMatrix();
    }

    public void renderOne()
    {
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glLineWidth(width.getValue() * 2F);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
        GL11.glClearStencil(0xF);
        GL11.glStencilFunc(GL11.GL_NEVER, 1, 0xF);
        GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
        GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_LINE);
    }

    public void renderTwo()
    {
        GL11.glStencilFunc(GL11.GL_NEVER, 0, 0xF);
        GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);
        GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_FILL);
    }

    public void renderThree()
    {
        GL11.glStencilFunc(GL11.GL_EQUAL, 1, 0xF);
        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);
        GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_LINE);
    }

    public void renderFour(Entity renderEntity)
    {
        float[] color;

        if (renderEntity instanceof EntityLivingBase)
        {
            EntityLivingBase entity = (EntityLivingBase) renderEntity;

            if (Gun.getInstance().getFriendManager().isFriend(entity.getName()))
            {
                color = new float[] {0.27F, 0.70F, 0.92F};
            }
            else
            {
                float distance = minecraft.thePlayer.getDistanceToEntity(entity);

                if (distance <= 32)
                {
                    color = new float[] {1F, distance / 32F, 0F};
                }
                else
                {
                    color = new float[] {0F, 0.9F, 0F};
                }
            }
        }
        else
        {
            float distance = minecraft.thePlayer.getDistanceToEntity(renderEntity);

            if (distance <= 32)
            {
                color = new float[] {1F, distance / 32F, 0F};
            }
            else
            {
                color = new float[] {0F, 0.9F, 0F};
            }
        }

        GlStateManager.color(color[0], color[1], color[2], 0.85F);
        renderFour();
    }

    private void renderFour()
    {
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_POLYGON_OFFSET_LINE);
        GL11.glPolygonOffset(1F, -2000000F);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
    }

    public void renderFive()
    {
        GL11.glPolygonOffset(1.0F, 2000000F);
        GL11.glDisable(GL11.GL_POLYGON_OFFSET_LINE);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_STENCIL_TEST);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_DONT_CARE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glPopAttrib();
    }

    private String getDisplayName(TileEntity tileEntity)
    {
        if (tileEntity instanceof TileEntityChest)
        {
            Block block = tileEntity.getBlockType();

            if (block == Blocks.chest)
            {
                return "Chest";
            }
            else if (block == Blocks.trapped_chest)
            {
                return "Trapped Chest";
            }
        }

        if (tileEntity instanceof TileEntityEnderChest)
        {
            return "EnderChest";
        }

        return "Unknown";
    }

}
