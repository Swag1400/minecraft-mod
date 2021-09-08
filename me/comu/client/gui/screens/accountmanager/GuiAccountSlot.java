package me.comu.client.gui.screens.accountmanager;

import me.comu.client.core.Gun;
import me.comu.client.logging.Logger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiSlot;

public class GuiAccountSlot extends GuiSlot {
    private GuiAccountScreen guiAccountScreen;
    int selected;

    public GuiAccountSlot(GuiAccountScreen aList) {
        super(Minecraft.getMinecraft(), aList.width, aList.height, 32, aList.height - 60, 27);
        this.guiAccountScreen = aList;
        this.selected = 0;
    }

    @Override
    protected int getContentHeight() {
        return this.getSize() * 27;
    }

    @Override
    protected int getSize() {
        return Gun.getInstance().getAccountManager().getRegistry().size();
    }

    @Override
    protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
        this.selected = slotIndex;

        if (isDoubleClick) {
            Account account = Gun.getInstance().getAccountManager().getRegistry().get(slotIndex);

            try {
                if (account.isPremium())
                    Minecraft.getMinecraft().processLogin(account.getLabel(), account.getPassword());
            } catch (AccountException exception) {
                Logger.getLogger().print("Account Exception for account: " + account.getLabel() + " " + account.getLabel());
            }
        }
    }

    @Override
    protected boolean isSelected(int slotIndex) {
        return this.selected == slotIndex;
    }

    protected int getSelected() {
        return this.selected;
    }

    @Override
    protected void drawBackground() {
        guiAccountScreen.drawDefaultBackground();
    }

    @Override
    protected void drawSlot(int selectedIndex, int x, int y, int var5, int var6, int var7) {
        try {
            Account account = Gun.getInstance().getAccountManager().getRegistry().get(selectedIndex);
            mc.fontRenderer.drawCenteredString(Gun.getInstance().getAccountManager().getRegistry().get(selectedIndex).getLabel(), mc.displayWidth / 2, y + 2, 0xFFAAAAAA, true);
            mc.fontRenderer.drawCenteredString((!account.isPremium() || account.getPassword().equals("Not Avaliable")) ? "Not Avaliable" : account.getPassword().replaceAll("(?s).", "*"), mc.displayWidth / 2, y + 15, 0xFFAAAAAA, true);
            mc.fontRenderer.drawCenteredString(selectedIndex+1 + ".", mc.displayWidth / 2 - 240, y + 15, 0xFFAAAAAA, true);
        } catch (AccountException exception) {
            exception.printStackTrace();
        }
    }
}
