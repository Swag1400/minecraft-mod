package me.comu.client.module.impl.toggle.render;

import me.comu.api.event.Listener;
import me.comu.api.minecraft.render.RenderMethods;
import me.comu.api.stopwatch.Stopwatch;
import me.comu.client.events.PacketEvent;
import me.comu.client.events.RenderGameOverlayEvent;
import me.comu.client.module.ModuleType;
import me.comu.client.module.ToggleableModule;
import me.comu.client.module.impl.active.render.TextGUI;
import me.comu.client.properties.EnumProperty;
import me.comu.client.properties.NumberProperty;
import me.comu.client.properties.Property;
import me.comu.client.utils.GuiUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class Graph extends ToggleableModule {

    private NumberProperty<Long> delay = new NumberProperty<>(50L, 0L, 1000L, 10L, "Delay", "d");
    private NumberProperty<Float> xAxis = new NumberProperty<>(300F, 1F, 1920F, 50F, "X-Axis", "x");
    private NumberProperty<Float> yAxis = new NumberProperty<>(26F, 1F, 1080F, 25F, "Y-Axis", "y");
    private EnumProperty<Mode> mode = new EnumProperty<>(Mode.MOVEMENT, "Mode", "m");
    private Property<Boolean> gradient = new Property<>(false, "Gradient", "g");
    private final List<MovementNode> movementNodes = new CopyOnWriteArrayList<>();
    private final List<PacketNode> packetNodes = new CopyOnWriteArrayList<>();
    private final Stopwatch timer = new Stopwatch();
    private final Stopwatch stopwatch = new Stopwatch();
    float w = 150;
    float h = 50;
    private int incoming, outgoing;

    public Graph() {
        super("Graph", new String[]{"Graph"}, 0xFFDBE300, ModuleType.RENDER);
        this.offerProperties(delay, xAxis, yAxis, mode, gradient);
        this.listeners.add(new Listener<RenderGameOverlayEvent>("brightness_gamma_setting_listener") {
            @Override
            public void call(RenderGameOverlayEvent event) {
                float x = xAxis.getValue();
                float y = yAxis.getValue();
                if (event.getType() == RenderGameOverlayEvent.Type.IN_GAME) {
                    final ScaledResolution sr = event.getScaledResolution();
                    final DecimalFormat decimalFormat = new DecimalFormat("###.##");
                    if (minecraft.thePlayer != null && minecraft.theWorld != null) {
                        switch (mode.getValue()) {
                            case MOVEMENT:
                                if (movementNodes.size() > w / 2) { // overflow protection
                                    movementNodes.clear();
                                }
                                if (timer.hasCompleted(delay.getValue())) {
                                    if (movementNodes.size() > (w / 2 - 1)) {
                                        movementNodes.remove(0); // remove oldest
                                    }
                                    final double deltaX = minecraft.thePlayer.posX - minecraft.thePlayer.prevPosX;
                                    final double deltaZ = minecraft.thePlayer.posZ - minecraft.thePlayer.prevPosZ;
                                    float bps = (float) (Math.sqrt(deltaX * deltaX + deltaZ * deltaZ) / TextGUI.tpsVal) * 400;
                                    movementNodes.add(new MovementNode(bps));
                                    timer.reset();
                                }
                                // background
                                RenderMethods.drawRect(x, y, x + w, y + h, 0x75101010);
                                // create temporary hovered data string
                                String hoveredData = "";
                                // begin scissoring
                                GL11.glEnable(GL11.GL_SCISSOR_TEST);
                                glScissor(x, y, x + w, y + h, sr);
                                // movement bars
                                MovementNode lastNode = null;
                                for (int i = 0; i < movementNodes.size(); i++) {
                                    final MovementNode movementNode = movementNodes.get(i);
                                    final float mappedX = (float) map((w / 2 - 1) - i, 0, (w / 2 - 1), x + w - 1, x + 1);
                                    final float mappedY = (float) map(movementNode.speed, -2.0f, getAverageHeight(), y + h - 1, y + 1) + h / 2;
                                    // set node's mapped coordinates
                                    movementNode.mappedX = mappedX;
                                    movementNode.mappedY = mappedY;
                                    // gradient of bar
                                    if (gradient.getValue()) {
                                        RenderMethods.drawGradientRect(mappedX - movementNode.size, mappedY, mappedX + movementNode.size, y + h, movementNode.color.getRGB(), 0x00000000);
                                    }
                                    // rect on top of bar
                                    if (lastNode != null) {
                                        RenderMethods.drawLine(movementNode.mappedX, movementNode.mappedY, lastNode.mappedX, lastNode.mappedY, 1.0f);
                                    }
                                    // draw dot
                                    RenderMethods.drawRect(movementNode.mappedX - movementNode.size, movementNode.mappedY - movementNode.size, movementNode.mappedX + movementNode.size, movementNode.mappedY + movementNode.size, movementNode.color.getRGB());
                                    // draw text
                                    if (i == movementNodes.size() - 1) {
                                        final String textToDraw = decimalFormat.format(movementNode.speed) + "bps";
                                        minecraft.fontRenderer.drawStringWithShadow(textToDraw, movementNode.mappedX - minecraft.fontRenderer.getStringWidth(textToDraw), movementNode.mappedY + 3, 0xFFAAAAAA);
                                    }
                                    // draw hover
                                    // hover bar
                                    RenderMethods.drawRect(movementNode.mappedX - movementNode.size, y, movementNode.mappedX + movementNode.size, y + h, 0x40101010);
                                    // hover red dot
                                    RenderMethods.drawRect(movementNode.mappedX - movementNode.size, movementNode.mappedY - movementNode.size, movementNode.mappedX + movementNode.size, movementNode.mappedY + movementNode.size, 0xFFFF0000);
                                    // set hovered data
                                    hoveredData = String.format("Speed: %s", decimalFormat.format(movementNode.speed));
                                    lastNode = movementNode;
                                }
                                // draw delay
                                minecraft.fontRenderer.drawStringWithShadow(delay.getValue() + "ms", x + 2, y + h - minecraft.fontRenderer.FONT_HEIGHT - 1, 0xFFAAAAAA);
                                // draw hovered data
                                if (!hoveredData.equals("")) {
                                    minecraft.fontRenderer.drawStringWithShadow(hoveredData, x + 2, y + h - minecraft.fontRenderer.FONT_HEIGHT * 2 - 1, 0xFFAAAAAA);
                                }
                                // disable scissor
                                GL11.glDisable(GL11.GL_SCISSOR_TEST);
                                // border
                                RenderMethods.drawBorderedRectBlurred(x, y, x + w, y + h, 2.0f, 0x00000000, 0x90101010);
                                break;
                            case PACKET:
                                String hoveredData2 = "";
                                String hoveredData3 = "";
                                if (stopwatch.hasCompleted(1000)) { // overflow protection
                                    packetNodes.clear();
                                    incoming = 0;
                                    outgoing = 0;
                                    stopwatch.reset();
                                }

                                if (timer.hasCompleted(delay.getValue())) {
                                    if (packetNodes.size() > (w / 2 - 1)) {
                                        packetNodes.remove(0); // remove oldest
                                    }
                                    timer.reset();
                                }

                                // background
                                RenderMethods.drawRect(x, y, x + w, y + h, 0x75101010);
                                // create temporary hovered data string
                                // begin scissoring
                                GL11.glEnable(GL11.GL_SCISSOR_TEST);
                                glScissor(x, y, x + w, y + h, sr);
                                // movement bars
                                PacketNode lastPacketNode = null;
                                for (int i = 0; i < packetNodes.size(); i++) {
                                    final PacketNode packetNode = packetNodes.get(i);
                                    final float mappedX = (float) map((w / 2 - 1) - i, 0, (w / 2 - 1), x + w - 1, x + 1);
                                    final float mappedY = (float) map(packetNodes.size() / 1000f, -2.0f, getAverageHeight(), y + h - 1, y + 1) + h / 2;
                                    // set node's mapped coordinates
                                    packetNode.mappedX = mappedX;
                                    packetNode.mappedY = mappedY;
                                    // gradient of bar
                                    if (gradient.getValue()) {
                                        RenderMethods.drawGradientRect(mappedX - packetNode.size, mappedY, mappedX + packetNode.size, y + h, packetNode.color.getRGB(), 0x00000000);
                                    }
                                    // rect on top of bar
                                    if (lastPacketNode != null) {
                                        RenderMethods.drawLine(packetNode.mappedX, packetNode.mappedY, lastPacketNode.mappedX, lastPacketNode.mappedY, 1.0f);
                                    }
                                    // draw dot
                                    RenderMethods.drawRect(packetNode.mappedX - packetNode.size, packetNode.mappedY - packetNode.size, packetNode.mappedX + packetNode.size, packetNode.mappedY + packetNode.size, packetNode.color.getRGB());
                                    // draw text
                                    if (i == packetNodes.size() - 1) {
                                        final String textToDraw = delay.getValue() + "ms";
                                        minecraft.fontRenderer.drawStringWithShadow(textToDraw, packetNode.mappedX - minecraft.fontRenderer.getStringWidth(textToDraw), packetNode.mappedY + 3, 0xFFAAAAAA);
                                    }
                                    // draw hover
                                    // hover bar
                                    RenderMethods.drawRect(packetNode.mappedX - packetNode.size, y, packetNode.mappedX + packetNode.size, y + h, 0x40101010);
                                    // hover red dot
                                    RenderMethods.drawRect(packetNode.mappedX - packetNode.size, packetNode.mappedY - packetNode.size, packetNode.mappedX + packetNode.size, packetNode.mappedY + packetNode.size, 0xFFFF0000);
                                    RenderMethods.drawRect(packetNode.mappedX - packetNode.size - 10, packetNode.mappedY - packetNode.size - 10, packetNode.mappedX + packetNode.size - 10, packetNode.mappedY + packetNode.size - 10, 0xFFFF0000);
                                    // set hovered data
                                    hoveredData2 = String.format("Incoming: %s", incoming);
                                    hoveredData3 = String.format("Outgoing: %s", outgoing);

                                    lastPacketNode = packetNode;
                                }
                                // draw hovered data
                                if (!hoveredData2.equals("")) {
                                    minecraft.fontRenderer.drawStringWithShadow(hoveredData2, x + 2, y + h - minecraft.fontRenderer.FONT_HEIGHT * 2 - 1, 0xFFAAAAAA);
                                }
                                if (!hoveredData3.equals("")) {
                                    minecraft.fontRenderer.drawStringWithShadow(hoveredData3, x + 2, y + h - minecraft.fontRenderer.FONT_HEIGHT - 1, 0xFFAAAAAA);
                                }
                                // disable scissor
                                GL11.glDisable(GL11.GL_SCISSOR_TEST);
                                // border
                                RenderMethods.drawBorderedRectBlurred(x, y, x + w, y + h, 2.0f, 0x00000000, GuiUtils.rainbow(3));
                                break;
                        }
                    }
                }
            }
        });
        this.listeners.add(new Listener<PacketEvent>("pussy") {
            @Override
            public void call(PacketEvent event) {
                if (event.getStatus() == PacketEvent.Status.INCOMING) {
                    packetNodes.add(new PacketNode("incoming"));
                    incoming++;
                }
                if (event.getStatus() == PacketEvent.Status.OUTGOING) {
                    packetNodes.add(new PacketNode("outgoing"));
                    outgoing++;
                }


            }
        });
    }

    public static void glScissor(float x, float y, float x1, float y1, final ScaledResolution sr) {
        GL11.glScissor((int) (x * sr.getScaleFactor()), (int) (Minecraft.getMinecraft().displayHeight - (y1 * sr.getScaleFactor())), (int) ((x1 - x) * sr.getScaleFactor()), (int) ((y1 - y) * sr.getScaleFactor()));
    }

    public float getAverageHeight() {
        float totalSpeed = 0;

        for (int i = this.movementNodes.size() - 1; i > 0; i--) {
            final MovementNode movementNode = this.movementNodes.get(i);
            if (this.movementNodes.size() > 11) {
                if (movementNode != null && (i > this.movementNodes.size() - 10)) {
                    totalSpeed += movementNode.speed;
                }
            }
        }

        return totalSpeed / 10;
    }

    private static class MovementNode {

        public float size = 0.5f;
        public float speed = 0.0f;
        public Color color;

        public float mappedX, mappedY;

        public MovementNode(float speed) {
            this.speed = speed;
            this.color = new Color(255, 255, 255);
        }
    }

    private static class PacketNode {

        public float size = 1.0f;

        public float mappedX, mappedY;
        public Color color;
        private String status;

        public PacketNode(String status) {
            if (status.equalsIgnoreCase("incoming")) {
                this.status = status;
            } else if (status.equalsIgnoreCase("outgoing")) {
                this.status = status;
            }
            color = new Color(255, 255, 255);
        }

        private String getStatus() {
            return status;
        }

    }

    public static double map(double value, double a, double b, double c, double d) {
        // first map value from (a..b) to (0..1)
        value = (value - a) / (b - a);
        // then map it from (0..1) to (c..d) and return it
        return c + value * (d - c);
    }

    private enum Mode {
        MOVEMENT, PACKET
    }
}
