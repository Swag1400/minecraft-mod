package me.comu.client.gui.screens.accountmanager;

import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import me.comu.client.utils.SessionUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.Session;
import org.lwjgl.input.Keyboard;

import java.net.Proxy;

public class GuiDirectLogin extends GuiScreen
{
    private final GuiScreen parentScreen;
    private GuiTextField usernameTextField;
    private GuiPasswordField passwordTextField;
    private GuiTextField altTextField;
    private String error;
    private Minecraft minecraft = Minecraft.getMinecraft();

    public GuiDirectLogin(GuiScreen guiScreen)
    {
        parentScreen = guiScreen;
    }


    @Override
    public void updateScreen()
    {
        usernameTextField.updateCursorCounter();
        passwordTextField.updateCursorCounter();
        altTextField.updateCursorCounter();
    }

    @Override
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    protected void actionPerformed(GuiButton guiButton)
    {
        if (!guiButton.enabled)
        {
            return;
        }

        if (guiButton.id == 1)
        {
            minecraft.displayGuiScreen(parentScreen);
        }
        else if (guiButton.id == 0) {
            if (usernameTextField.getText().length() == 0 && passwordTextField.getText().length() == 0 && altTextField.getText().contains(":")) {
            usernameTextField.setText(altTextField.getText().split(":")[0]);
            passwordTextField.setText(altTextField.getText().split(":")[1]);
            altTextField.setText("");
            } try {
                SessionUtils.login(usernameTextField.getText(),passwordTextField.getText());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
                if (passwordTextField.getText().length() > 0) {
                    String usernameTextFieldText = usernameTextField.getText();
                    String passwordTextFieldText = passwordTextField.getText();

                    try {
                        String result = Minecraft.getMinecraft().processLogin(usernameTextFieldText, passwordTextFieldText).trim();

                        if (result == null || !result.contains(":")) {
                            error = result;
                            return;
                        }

                        String[] values = result.split(":");

                        if (values.length > 1) {
                            minecraft.setSession(new Session(values[2], values[4], values[3], "mojang"));
                        }

                        minecraft.displayGuiScreen(parentScreen);
                    } catch (Exception exception) {

                    }
                } else {
                    minecraft.setSession(new Session(usernameTextField.getText(), "", "", "mojang"));
                }

                minecraft.displayGuiScreen(parentScreen);

        }
    }
    private Session createSession(final String email, final String pass) {
        final YggdrasilAuthenticationService service = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
        final YggdrasilUserAuthentication authentication = (YggdrasilUserAuthentication)service.createUserAuthentication(Agent.MINECRAFT);
        authentication.setUsername(email);
        authentication.setPassword(pass);
        try {
            authentication.logIn();
            return new Session(authentication.getSelectedProfile().getName(), authentication.getSelectedProfile().getId().toString(), authentication.getAuthenticatedToken(), "legacy");
        }
        catch (AuthenticationException e) {
            return null;
        }
    }
    @Override
    protected void keyTyped(char typedChar, int keyCode)
    {
        usernameTextField.textboxKeyTyped(typedChar, keyCode);
        passwordTextField.textboxKeyTyped(typedChar, keyCode);
        altTextField.textboxKeyTyped(typedChar, keyCode);

        if (typedChar == '\t')
        {
            if (usernameTextField.isFocused())
            {
                usernameTextField.isFocused = false;
                passwordTextField.isFocused = true;
                altTextField.isFocused = false;
            }
            else if (passwordTextField.isFocused)
            {
                usernameTextField.isFocused = false;
                passwordTextField.isFocused = false;
                altTextField.isFocused = true;

            } else {
                altTextField.isFocused = false;
                passwordTextField.isFocused = false;
                usernameTextField.isFocused = true;


            }
        }

        if (typedChar == '\r')
        {
            actionPerformed((GuiButton) buttonList.get(0));
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        try
        {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }

        usernameTextField.mouseClicked(mouseX, mouseY, mouseButton);
        passwordTextField.mouseClicked(mouseX, mouseY, mouseButton);
        altTextField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        buttonList.clear();
        buttonList.add(new GuiButton(0, width / 2 - 100, height / 4 + 96 + 12, "Done"));
        buttonList.add(new GuiButton(1, width / 2 - 100, height / 4 + 120 + 12, "Cancel"));
        altTextField = new GuiTextField(6, fontRendererObj, width / 2 - 100, 156, 200, 20);
        usernameTextField = new GuiTextField(6, fontRendererObj, width / 2 - 100, 76, 200, 20);
        passwordTextField = new GuiPasswordField(fontRendererObj, width / 2 - 100, 116, 200, 20);
        usernameTextField.setMaxStringLength(512);
        altTextField.setMaxStringLength(512);
        passwordTextField.setMaxStringLength(32);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        drawDefaultBackground();
        drawCenteredString(fontRendererObj, "Login", width / 2, 12, 0xffffff);
        drawString(fontRendererObj, "Username", width / 2 - 100, 63, 0xa0a0a0);
        drawString(fontRendererObj, "Password", width / 2 - 100, 104, 0xa0a0a0);
        drawString(fontRendererObj, "email:password", width / 2 - 100, 145, 0xa0a0a0);
        usernameTextField.drawTextBox();
        passwordTextField.drawTextBox();
        altTextField.drawTextBox();
        if (GuiMainMenu.ipAddress == null) {
            Minecraft.getMinecraft().fontRenderer.drawString("Connecting...", width - Minecraft.getMinecraft().fontRenderer.getStringWidth("Connecting...") - 2, height - Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT - 2, 0xa0a0a0);
        }
        else {
            Minecraft.getMinecraft().fontRenderer.drawString(GuiMainMenu.ipAddress, width - Minecraft.getMinecraft().fontRenderer.getStringWidth(GuiMainMenu.ipAddress) - 2, height - Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT - 2, 0xa0a0a0);
        }
        if (error != null)
        {
            drawCenteredString(fontRendererObj, (new StringBuilder("\247cLogin Failed: ")).append(error).toString(), width / 2, height / 2, 0xFFFFFFFF);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
