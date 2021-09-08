package me.comu.client.gui.screens.accountmanager;

import me.comu.client.core.Gun;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class GuiAccountAdd extends GuiScreen {
    private GuiTextField usernameBox;
    private GuiPasswordField passwordBox;
    private GuiTextField altInfoBox;

    private String errorMessage = "";
    private int errorTime = 0;

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        buttonList.add(new GuiButton(1, width / 2 - 100, height / 4 + 96 + 12, "Add"));
        buttonList.add(new GuiButton(2, width / 2 - 100, height / 4 + 96 + 36, "Back"));
        usernameBox = new GuiTextField(6, fontRendererObj, width / 2 - 100, 76, 200, 20);
        passwordBox = new GuiPasswordField(fontRendererObj, width / 2 - 100, 116, 200, 20);
        altInfoBox = new GuiTextField(6, fontRendererObj, width / 2 - 100, 156, 200, 20);
        usernameBox.setMaxStringLength(120);
        passwordBox.setMaxStringLength(100);
        altInfoBox.setMaxStringLength(420);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void updateScreen() {
        usernameBox.updateCursorCounter();
        passwordBox.updateCursorCounter();
        altInfoBox.updateCursorCounter();
    }

    @Override
    public void mouseClicked(int x, int y, int b) {
        usernameBox.mouseClicked(x, y, b);
        passwordBox.mouseClicked(x, y, b);
        altInfoBox.mouseClicked(x, y, b);

        try {
            super.mouseClicked(x, y, b);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(GuiButton button) {
        if (button.id == 1) {
            if (!usernameBox.getText().trim().isEmpty()) {
                if (passwordBox.getText().trim().isEmpty()) {
                    Account account = new Account(usernameBox.getText().trim());

                    if (!Gun.getInstance().getAccountManager().getRegistry().contains(account)) {
                        Gun.getInstance().getAccountManager().register(account);
                    }
                } else {
                    Account account = new Account(usernameBox.getText().trim(), passwordBox.getText().trim());

                    if (!Gun.getInstance().getAccountManager().getRegistry().contains(account)) {
                        Gun.getInstance().getAccountManager().register(account);
                    }
                }
            } else if (usernameBox.getText().isEmpty() && passwordBox.getText().isEmpty() && altInfoBox.getText().contains(":")) {
                usernameBox.setText(altInfoBox.getText().split(":")[0]);
                passwordBox.setText(altInfoBox.getText().split(":")[1]);
                altInfoBox.setText("");
                if (!usernameBox.getText().trim().isEmpty()) {
                    if (passwordBox.getText().trim().isEmpty()) {
                        Account account = new Account(usernameBox.getText().trim());

                        if (!Gun.getInstance().getAccountManager().getRegistry().contains(account)) {
                            Gun.getInstance().getAccountManager().register(account);
                        }
                    } else {
                        Account account = new Account(usernameBox.getText().trim(), passwordBox.getText().trim());

                        if (!Gun.getInstance().getAccountManager().getRegistry().contains(account)) {
                            Gun.getInstance().getAccountManager().register(account);
                        }
                    }
                }
            }
            Gun.getInstance().getConfigManager().getRegistry().stream().filter(config -> config.getFile().getName().equals("accounts.txt")).forEach(config -> config.save());
            mc.displayGuiScreen(new GuiAccountScreen());
            } else if (button.id == 2) {
                mc.displayGuiScreen(new GuiAccountScreen());
            }
        }


        @Override
        protected void keyTyped (char typedChar, int keyCode) throws IOException
        {
            usernameBox.textboxKeyTyped(typedChar, keyCode);
            passwordBox.textboxKeyTyped(typedChar, keyCode);
            altInfoBox.textboxKeyTyped(typedChar, keyCode);

            if (typedChar == '\t') {
                if (usernameBox.isFocused()) {
                    usernameBox.isFocused = false;
                    passwordBox.isFocused = true;
                    altInfoBox.isFocused = false;
                } else if (passwordBox.isFocused) {
                    usernameBox.isFocused = false;
                    passwordBox.isFocused = false;
                    altInfoBox.isFocused = true;

                } else {
                    altInfoBox.isFocused = false;
                    passwordBox.isFocused = false;
                    usernameBox.isFocused = true;


                }
            }

            if (typedChar == '\r') {
                actionPerformed((GuiButton) buttonList.get(0));
            }
        }

        @Override
        public void drawScreen ( int mouseX, int mouseY, float partialTicks)
        {
            drawDefaultBackground();
            mc.fontRenderer.drawStringWithShadow(String.format("%s* \2477Username", usernameBox.getText().length() > 1 ? EnumChatFormatting.GREEN : EnumChatFormatting.RED), width / 2 - 109, 63, 0xa0a0a0);
            mc.fontRenderer.drawStringWithShadow("Password", width / 2 - 100, 103, 0xa0a0a0);
            mc.fontRenderer.drawString("email:password", width / 2 - 100, 145, 0xa0a0a0);
            mc.fontRenderer.drawStringWithShadow(errorMessage, width / 2 - fontRendererObj.getStringWidth(errorMessage), 13, 0xa0a0a0);

            if (errorMessage.length() > 1) {
                errorTime += 1;

                if (errorTime > 1700) {
                    errorMessage = "";
                    errorTime = 0;
                }
            }

            try {
                usernameBox.drawTextBox();
                passwordBox.drawTextBox();
                altInfoBox.drawTextBox();
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            super.drawScreen(mouseX, mouseY, partialTicks);
        }
    }


