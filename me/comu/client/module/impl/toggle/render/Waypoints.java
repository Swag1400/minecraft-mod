package me.comu.client.module.impl.toggle.render;

import me.comu.api.event.Listener;
import me.comu.api.minecraft.render.RenderMethods;
import me.comu.client.command.Argument;
import me.comu.client.command.Command;
import me.comu.client.core.Gun;
import me.comu.client.events.RenderEvent;
import me.comu.client.events.RenderGameOverlayEvent;
import me.comu.client.events.onDeathEvent;
import me.comu.client.logging.Logger;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.module.impl.active.render.TextGUI;
import me.comu.client.properties.NumberProperty;
import me.comu.client.properties.Property;
import me.comu.client.waypoints.Point;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.DamageSource;
import org.lwjgl.opengl.GL11;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class Waypoints extends ToggleableModule {
    private final NumberProperty<Float> width = new NumberProperty<>(1.8F, 1F, 5F, 0.1F, "Width", "w","thickness","thick","t"), scaling = new NumberProperty<>(0.0030F, 0.001F, 0.0100F, 0.001F, "Scaling", "scale", "s");
    private final Property<Boolean> death = new Property<>(true, "Log Death", "death", "d", "log", "logdeath"), textDisplay = new Property<>(true, "Text-Display", "display", "text", "t", "td", "word", "words", "textdisplay", "display-text", "displaytext"), lines = new Property<>(true, "Lines", "Line", "l");
    public static final List<Point> points = new ArrayList<>();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a");
    private final SimpleDateFormat dayFormat = new SimpleDateFormat("MM/dd/yyyy");
    public static boolean shouldRender = true;

    // TODO: list waypoints name command
    public Waypoints() {
        super("Waypoints", new String[]{"waypoints", "waypoint", "points", "point", "wp"}, ModuleType.RENDER);
        this.offerProperties(width, scaling, death, textDisplay, lines);
        Gun.getInstance().getCommandManager().register(new Command(new String[]{"waypointsadd", "waypointadd", "pointadd", "pointsadd", "wpadd", "wadd", "padd"}, new Argument("label"), new Argument("x"), new Argument("y"), new Argument("z")) {
            @Override
            public String dispatch() {
                String name = getArgument("label").getValue().replaceAll(":", "|");
                int x = Integer.parseInt(getArgument("x").getValue());
                int y = Integer.parseInt(getArgument("y").getValue());
                int z = Integer.parseInt(getArgument("z").getValue());
                Point point = new Point(name, x, y, z);

                if (!isValidPoint(point)) {
                    points.add(point);
                }

                return String.format("Added waypoint &e%s&7.", point.getLabel());
            }
        });
        Gun.getInstance().getCommandManager().register(new Command(new String[]{"waypointsremove", "wrem", "wpdel", "waypointsdel", "wprem", "waypointremove", "pointremove", "pointsremove", "wpremove", "wremove", "premove", "waypointsrem"}, new Argument("label")) {
            @Override
            public String dispatch() {
                String name = getArgument("label").getValue().replaceAll(":", "|");
                Point point = getPoint(name);


                if (point == null) {
                    return "Invalid waypoint entered.";
                }

                if (isValidPoint(point)) {
                    points.remove(point);
                }
                return String.format("Removed waypoint &e%s&7.", point.getLabel());
            }
        });
        Gun.getInstance().getCommandManager().register(new Command(new String[]{"waypointhere", "pointhere", "waypointshere", "wphere"}, new Argument("label")) {
            @Override
            public String dispatch() {
                String name = getArgument("label").getValue().replaceAll(":", "|");
                String x = Integer.toString((int)minecraft.getRenderManager().viewerPosX);
                String y = Integer.toString((int)minecraft.getRenderManager().viewerPosY);
                String z = Integer.toString((int)minecraft.getRenderManager().viewerPosZ);

                Point point = new Point(name, Integer.parseInt(x), Integer.parseInt(y), Integer.parseInt(z));
                if (!isValidPoint(point)) {
                    points.add(point);
                }
                return String.format("Added waypoint &e%s&7.", point.getLabel());
            }
        });

        this.listeners.add(new Listener<onDeathEvent>("waypoints_render_listener") {
            @Override
            public void call(onDeathEvent event) {
                if (death.getValue()) {

                    final DamageSource[] dmgSrc = new DamageSource[]{DamageSource.anvil, DamageSource.cactus, DamageSource.drown, DamageSource.fall, DamageSource.fallingBlock, DamageSource.field_180137_b, DamageSource.generic, DamageSource.inFire, DamageSource.inWall, DamageSource.magic, DamageSource.onFire, DamageSource.lava, DamageSource.wither, DamageSource.starve, DamageSource.outOfWorld};

                    String coordinatesFormat = String.format("%s, %s, %s", (int) minecraft.thePlayer.posX, (int) minecraft.thePlayer.posY, (int) minecraft.thePlayer.posZ);
                    String time = String.format("\2477%s", dateFormat.format(new Date()));
                    String date = String.format("\2477%s", dayFormat.format(new Date()));
                    if (minecraft.isSingleplayer()) {
                        Logger.getLogger().printToChat("You died at " + coordinatesFormat + " at " + time + " on " + date + ". (Singeplayer)");
                    }
                    if (!minecraft.isSingleplayer()) {
                        Logger.getLogger().printToChat("You died at " + coordinatesFormat + " at " + time + " on " + date + " on " + minecraft.getCurrentServerData().serverIP + ". (" + minecraft.theWorld.provider.getDimensionName() + ")");
                    }
                }
            }
        });
        this.listeners.add(new Listener<RenderEvent>("waypoints_render_listener") {
            @Override
            public void call(RenderEvent event) {
                    GlStateManager.pushMatrix();
                    RenderMethods.enableGL3D();
                if (lines.getValue()) {
                    for (Point point : points) {
                        double x = point.getX() - RenderManager.renderPosX;
                        double y = point.getY() - RenderManager.renderPosY;
                        double z = point.getZ() - RenderManager.renderPosZ;
                        GlStateManager.color(point.getColor()[0], point.getColor()[1], point.getColor()[2], 0.8F);
                        boolean bobbing = minecraft.gameSettings.viewBobbing;
                        GL11.glLineWidth(width.getValue());
                        GL11.glLoadIdentity();
                        minecraft.gameSettings.viewBobbing = false;
                        minecraft.entityRenderer.orientCamera(event.getPartialTicks());
                        GL11.glBegin(GL11.GL_LINES);
                        GL11.glVertex3d(0, minecraft.thePlayer.getEyeHeight(), 0);
                        GL11.glVertex3d(x, y, z);
                        GL11.glVertex3d(x, y, z);
                        GL11.glVertex3d(x, y + 2D, z);
                        GL11.glEnd();
                        minecraft.gameSettings.viewBobbing = bobbing;
                    }
                }

                for (Point point : points) {
                    double x = point.getX() - RenderManager.renderPosX;
                    double y = point.getY() - RenderManager.renderPosY;
                    double z = point.getZ() - RenderManager.renderPosZ;
                    GlStateManager.pushMatrix();
                    renderPointNameTag(point, x, y, z);
                    GlStateManager.popMatrix();
                }

                RenderMethods.disableGL3D();
                GlStateManager.popMatrix();
            }
        });
        this.listeners.add(new Listener<RenderGameOverlayEvent>("i_love_men") {
            @Override
            public void call(RenderGameOverlayEvent event) {
                if (textDisplay.getValue() && shouldRender) {

                    int tempY = TextGUI.shown ? 98 : 89;
                    for (Point point : points) {
                        int x = (int) (point.getX() - minecraft.getRenderManager().renderPosX);
                        int y = (int) (point.getY() - minecraft.getRenderManager().renderPosY);
                        int z = (int) (point.getZ() - minecraft.getRenderManager().renderPosZ);
                        minecraft.fontRenderer.drawStringWithShadow(String.format("\2477%s: \247f" + "%s, " + "%s, " + "%s", point.getLabel(), x, y, z), 2, tempY, 0xFFFFFFFF);
                        tempY += 9;

                    }

                }
            }
        });
    }

    private boolean isValidPoint(Point point) {
        for (Point p : points) {
            if (p.getX() == point.getX() && p.getY() == point.getY() && p.getZ() == point.getZ()) {
                return true;
            }
        }

        return false;
    }

    private Point getPoint(String name) {
        for (Point point : points) {
            if (point.getLabel().equalsIgnoreCase(name)) {
                return point;
            }
        }

        return null;
    }

    private void renderPointNameTag(Point point, double x, double y, double z) {
        double tempY = y;
        tempY += 0.7D;
        double distance = minecraft.getRenderViewEntity().getDistance(x + minecraft.getRenderManager().viewerPosX, y + minecraft.getRenderManager().viewerPosY, z + minecraft.getRenderManager().viewerPosZ);
        int width = minecraft.fontRenderer.getStringWidth(point.getLabel()) / 2 + 1;
        double scale = 0.0018 + scaling.getValue() * distance;

        if (distance <= 8) {
            scale = 0.0245D;
        }

        GlStateManager.pushMatrix();
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset(1, -1500000);
        GlStateManager.disableLighting();
        GlStateManager.translate((float) x, (float) tempY + 1.4F, (float) z);
        GlStateManager.rotate(-RenderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(RenderManager.playerViewX, minecraft.gameSettings.thirdPersonView == 2 ? -1.0F : 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-scale, -scale, scale);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        RenderMethods.drawBorderedRectReliant(-width - 2, -(minecraft.fontRenderer.FONT_HEIGHT + 1), width, 1.5F, 1.6F, 0x77000000, 0xAA701020);
        GlStateManager.enableAlpha();
        minecraft.fontRenderer.drawStringWithShadow(point.getLabel(), -width, -(minecraft.fontRenderer.FONT_HEIGHT - 1), 0xFFAAAAAA);
        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.disablePolygonOffset();
        GlStateManager.doPolygonOffset(1, 1500000);
        GlStateManager.popMatrix();
    }

}
