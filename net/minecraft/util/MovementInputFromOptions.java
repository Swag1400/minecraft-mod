package net.minecraft.util;

import me.comu.client.core.Gun;
import me.comu.client.module.impl.toggle.miscellaneous.InventoryWalk;
import me.comu.client.module.impl.toggle.movement.AutoWalk;
import me.comu.client.module.impl.toggle.movement.StackUp;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.GameSettings;
import org.lwjgl.input.Keyboard;

public class MovementInputFromOptions extends MovementInput {
    private final GameSettings gameSettings;

    public MovementInputFromOptions(GameSettings gameSettingsIn) {
        this.gameSettings = gameSettingsIn;
    }

    public void updatePlayerMoveState() {
        InventoryWalk invWalk = (InventoryWalk) Gun.getInstance().getModuleManager().getModuleByAlias("inventorywalk");
        AutoWalk autoWalk = (AutoWalk) Gun.getInstance().getModuleManager().getModuleByAlias("autowalk");
        StackUp stackUp = (StackUp) Gun.getInstance().getModuleManager().getModuleByAlias("stackup");
        if (invWalk.isRunning() && !stackUp.isRunning() && !autoWalk.isRunning() && !(Minecraft.getMinecraft().currentScreen instanceof GuiChat)) {
            this.moveStrafe = 0.0F;
            this.moveForward = 0.0F;
            if (Keyboard.isKeyDown(this.gameSettings.keyBindForward.getKeyCode())) {
                this.moveForward += 1.0F;
            }
            if (Keyboard.isKeyDown(this.gameSettings.keyBindBack.getKeyCode())) {
                this.moveForward -= 1.0F;
            }
            if (Keyboard.isKeyDown(this.gameSettings.keyBindLeft.getKeyCode())) {
                this.moveStrafe += 1.0F;
            }
            if (Keyboard.isKeyDown(this.gameSettings.keyBindRight.getKeyCode())) {
                this.moveStrafe -= 1.0F;
            }
            this.jump = Keyboard.isKeyDown(this.gameSettings.keyBindJump.getKeyCode());
            this.sneak = this.gameSettings.keyBindSneak.isKeyDown();
            if (this.sneak) {
                this.moveStrafe = ((float) (this.moveStrafe * 0.3D));
                this.moveForward = ((float) (this.moveForward * 0.3D));
            }
        } else {
            this.moveStrafe = 0.0F;
            this.moveForward = 0.0F;

            if (this.gameSettings.keyBindForward.isKeyDown()) {
                ++this.moveForward;
            }

            if (this.gameSettings.keyBindBack.isKeyDown()) {
                --this.moveForward;
            }

            if (this.gameSettings.keyBindLeft.isKeyDown()) {
                ++this.moveStrafe;
            }

            if (this.gameSettings.keyBindRight.isKeyDown()) {
                --this.moveStrafe;
            }

            this.jump = this.gameSettings.keyBindJump.isKeyDown();
            this.sneak = this.gameSettings.keyBindSneak.isKeyDown();

            if (this.sneak) {
                this.moveStrafe = (float) ((double) this.moveStrafe * 0.3D);
                this.moveForward = (float) ((double) this.moveForward * 0.3D);
            }
        }

    }
}