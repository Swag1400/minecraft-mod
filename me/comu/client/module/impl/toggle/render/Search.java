package me.comu.client.module.impl.toggle.render;

import com.sun.javafx.geom.Vec3d;
import me.comu.api.event.Listener;
import me.comu.api.minecraft.helper.WorldHelper;
import me.comu.api.minecraft.render.RenderMethods;
import me.comu.client.command.Argument;
import me.comu.client.command.Command;
import me.comu.client.core.Gun;
import me.comu.client.events.BlockRendererEvent;
import me.comu.client.events.RenderEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.properties.NumberProperty;
import me.comu.client.properties.Property;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class Search extends ToggleableModule
{
    private final NumberProperty<Integer> range = new NumberProperty<>(64, 1, 128,8, "Range", "reach", "r");
    private final Property<Boolean> lines = new Property<>(false, "Lines", "line", "l");
    private final NumberProperty<Float> width = new NumberProperty<>(1.8F, 1F, 5F, 0.1F, "Width", "w");

    private final List<Block> blocks = new ArrayList<>();
    private final List<TileEntitySkull> skull = new ArrayList<>();
    private final List<Vec3d> vec3ds = new CopyOnWriteArrayList<>();

    public Search()
    {
        super("Search", new String[] {"search"}, 0xFFB790D4, ModuleType.RENDER);
        this.offerProperties(range, width, lines);
        blocks.add(Block.getBlockById(52));
        this.listeners.add(new Listener<RenderEvent>("search_render_listener")
        {
            @Override
            public void call(RenderEvent event)
            {
                GlStateManager.pushMatrix();
                RenderMethods.enableGL3D();
                setTag(String.format("%s \2477%s", getLabel(), blocks.size()));

                for (Vec3d vec3d : vec3ds)
                {
                    if (minecraft.thePlayer.getDistance(vec3d.x, vec3d.y, vec3d.z) > range.getValue())
                    {
                        vec3ds.remove(vec3d);
                        continue;
                    }

                    double x = vec3d.x - minecraft.getRenderManager().renderPosX;
                    double y = vec3d.y - minecraft.getRenderManager().renderPosY;
                    double z = vec3d.z - minecraft.getRenderManager().renderPosZ;
                    AxisAlignedBB box = new AxisAlignedBB(x, y, z, x + 1D, y + 1D, z + 1D);
                    float color[] = getColor(WorldHelper.getBlock(vec3d.x, vec3d.y, vec3d.z));
                    GlStateManager.color(color[0], color[1], color[2], 0.25F);
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

                    RenderMethods.drawBox(box);
                    GlStateManager.color(color[0], color[1], color[2], 0.7F);
                    RenderMethods.drawOutlinedBox(box);
                    minecraft.gameSettings.viewBobbing = bobbing;
                }

                RenderMethods.disableGL3D();
                GlStateManager.popMatrix();
            }
        });
        this.listeners.add(new Listener<BlockRendererEvent>("search_block_renderer_listener")
        {
            @Override
            public void call(BlockRendererEvent event)
            {
                Vec3d blockPos = new Vec3d(event.getBlockPos().getX(), event.getBlockPos().getY(), event.getBlockPos().getZ());

                if (blocks.contains(event.getBlock()) && !isValid(blockPos))
                {
                    vec3ds.add(blockPos);
                }
            }
        });
//        this.listeners.add(new Listener<RenderSkullEvent>("search_block_renderer_listener")
//        {
//            @Override
//            public void call(RenderSkullEvent event)
//            {
//                Vec3d blockPos = new Vec3d(event.b().getX(), event.getBlockPos().getY(), event.getBlockPos().getZ());
//
//                if (blocks.contains(event.getBlock()) && !isValid(blockPos))
//                {
//                    vec3ds.add(blockPos);
//                }
//            }
//        });

        Gun.getInstance().getCommandManager().register(new Command(new String[] {"search", "s"}, new Argument("block|clear"))
        {
            @Override
            public String dispatch()
            {
                String argument = getArgument("block|clear").getValue();

                if (argument.equalsIgnoreCase("clear"))
                {
                    blocks.clear();
                    vec3ds.clear();
                    return "Cleared &eSearch&7 list.";
                }

                Block block = Block.getBlockFromName((argument));

                if (block == null)
                {
                    return "That block could not be found.";
                }

                if (blocks.contains(block))
                {
                    blocks.remove(block);
                    vec3ds.clear();
                    minecraft.renderGlobal.loadRenderers();
                    return String.format("Removed &e%s&7 from the &eSearch&7 list.", block.getLocalizedName());
                }
                else
                {
                    blocks.add(block);
                    vec3ds.clear();
                    minecraft.renderGlobal.loadRenderers();
                    return String.format("Added &e%s&7 to the &eSearch&7 list.", block.getLocalizedName());
                }
            }
        });
    }

    @Override
    protected void onEnable()
    {
        super.onEnable();
        minecraft.renderGlobal.loadRenderers();
    }

    private float[] getColor(Block block)
    {
        switch (Block.getIdFromBlock(block))
        {
            case 56:
            case 57:
                return new float[] {0.27F, 0.70F, 0.92F};
            case 41:
            case 14:
                return new float[] {0.8F, 0.7F, 0.3F};
            case 42:
            case 15:
                return new float[] {0.4F, 0.4F, 0.4F};
            case 133:
            case 129:
                return new float[] {0.1F, 0.5F, 0.1F};
            case 152:
            case 73:
            case 74:
                return new float[] {0.6F, 0.1F, 0.1F};
            case 21:
            case 22:
                return new float[] {0.2F, 0.2F, 0.8F};
            case 16:
                return new float[] {0F, 0F, 0F};
            case 52:
                return new float[] {0.75F,0.3F,0.9F};
        }

        return new float[] {1, 1, 1};
    }

    private boolean isValid(Vec3d block)
    {
        for (Vec3d vec3d : vec3ds)
        {
            if (vec3d.x == block.x && vec3d.y == block.y && vec3d.z == block.z)
            {
                return true;
            }
        }

        return false;
    }
}
