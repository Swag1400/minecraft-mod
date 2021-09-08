package me.comu.client.gui.screens.accountmanager;

import me.comu.client.core.Gun;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.util.Session;
import org.lwjgl.input.Keyboard;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class GuiAccountScreen extends GuiScreen implements GuiYesNoCallback
{
    private String dispErrorString = "";
    private boolean deleteMenuOpen = false;
    private GuiAccountSlot accountSlot;

    private int timer = 0;

    private Minecraft mc = Minecraft.getMinecraft();

    @Override
    public void handleMouseInput() throws IOException
    {
        super.handleMouseInput();
        accountSlot.func_178039_p();
    }

    @Override
    public void initGui()
    {
        buttonList.clear();
        buttonList.add(new GuiButton(2, width / 2 - 76, height - 48, 73, 20, "Login"));
        buttonList.add(new GuiButton(5, width / 2, height - 48, 73, 20, "Direct Login"));
        buttonList.add(new GuiButton(1, width / 2 - 154, height - 48, 73, 20, "Add"));
        buttonList.add(new GuiButton(3, width / 2 + 78, height - 48, 73, 20, "Remove"));
        buttonList.add(new GuiButton(4, width / 2 - 76, height - 26, 149, 20, "Back"));
        buttonList.add(new GuiButton(6, width / 2 - 154, height - 26, 73, 20, "Random"));
        buttonList.add(new GuiButton(7, width / 2 + 78, height - 26, 73, 20, "Import"));
        buttonList.add(new GuiButton(8, width / 2 + 154, height - 26, 73, 20, "Last"));
        buttonList.add(new GuiButton(9, width / 2 + 154, height - 48, 73, 20, "Servers"));
        accountSlot = new GuiAccountSlot(this);
    }

    @Override
    public void confirmClicked(boolean result, int id)
    {
        super.confirmClicked(result, id);

        if (deleteMenuOpen)
        {
            deleteMenuOpen = false;

            if (result)
            {
                Gun.getInstance().getAccountManager().getRegistry().remove(id);
                Gun.getInstance().getConfigManager().getRegistry().stream().filter(config -> config.getFile().getName().equals("accounts.txt")).forEach(config -> config.save());
            }

            mc.displayGuiScreen(this);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode)
    {
        if (keyCode == Keyboard.KEY_UP)
        {
            accountSlot.selected--;
        }

        if (keyCode == Keyboard.KEY_DOWN)
        {
            accountSlot.selected++;
        }
        if (keyCode == Keyboard.KEY_ESCAPE) {
        	mc.displayInGameMenu();
        }

        if (keyCode == Keyboard.KEY_RETURN)
        {
            Account account = Gun.getInstance().getAccountManager().getRegistry().get(accountSlot.selected);

            try
            {
                if (account.isPremium())
                Minecraft.getMinecraft().processLogin(account.getLabel(), account.getPassword());

            }
            catch (AccountException exception)
            {
                exception.printStackTrace();
            }
        }
    }

    @Override
    public void actionPerformed(GuiButton guiButton)
    {
        try
        {
            super.actionPerformed(guiButton);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if (guiButton.id == 1)
        {
            GuiAccountAdd gaa = new GuiAccountAdd();
            mc.displayGuiScreen(gaa);
        }

        if (guiButton.id == 2)
        {
            try
            {
                Account account = Gun.getInstance().getAccountManager().getRegistry().get(accountSlot.getSelected());

                if (account.isPremium())
                {
                    try
                    {
                        HashMap map = new HashMap(3, 1);
                        map.put("user", account.getLabel());
                        map.put("password", account.getPassword());
                        map.put("version", 13);
                        Minecraft.getMinecraft().processLogin(account.getLabel(), account.getPassword());
                    }
                    catch (Exception exception)
                    {
                        exception.printStackTrace();
                        Gun.getInstance().getAccountManager().unregister(account);
                    }
                }
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }
        }
        if (guiButton.id == 8) {

            Account a1 = Gun.getInstance().getAccountManager().getRegistry().get(Gun.getInstance().getAccountManager().getRegistry().size() - 1);

            try {
                if (a1.getPassword().equals("Not Avaliable"))
                {
                    Minecraft.getMinecraft().setSession(new Session(a1.getLabel(), "", "","mojang"));
                    return;
                }
                if (a1.isPremium()) {
                    HashMap map = new HashMap(3, 1);
                    map.put("user", a1.getLabel());
                    map.put("password", a1.getPassword());
                    map.put("version", 13);
                    Minecraft.getMinecraft().processLogin(a1.getLabel(), a1.getPassword());
                }
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        if (guiButton.id == 3)
        {
            try
            {
                String s1 = "Are you sure you want to delete the alt " + "\"" + Gun.getInstance().getAccountManager().getRegistry().get(accountSlot.getSelected()).getLabel() + "\"" + "?";
                String s3 = "Delete";
                String s4 = "Cancel";
                GuiYesNo guiyesno = new GuiYesNo(this, s1, "", s3, s4, accountSlot.getSelected());
                deleteMenuOpen = true;
                mc.displayGuiScreen(guiyesno);
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }
        }

        if (guiButton.id == 4)
        {
            mc.displayGuiScreen(new GuiMainMenu());
        }

        if (guiButton.id == 5)
        {
            GuiDirectLogin gdl = new GuiDirectLogin(this);
            mc.displayGuiScreen(gdl);
        }

        if (guiButton.id == 6)
        {
            Random random = new Random();
            Account a1 = Gun.getInstance().getAccountManager().getRegistry().get(random.nextInt(Gun.getInstance().getAccountManager().getRegistry().size()));

            try
            {
                if (a1.isPremium())
                {
                    try
                    {
                        HashMap map = new HashMap(3, 1);
                        map.put("user", a1.getLabel());
                        map.put("password", a1.getPassword());
                        map.put("version", 13);
                        Minecraft.getMinecraft().processLogin(a1.getLabel(), a1.getPassword());
                    }
                    catch (Exception exception)
                    {
                        exception.printStackTrace();
                    }
                }
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }
        }

        if (guiButton.id == 7)
        {
            this.importAlts();
        }
        if (guiButton.id == 9)
        {
            GuiMultiplayer guiMultiplayer = new GuiMultiplayer(this);
            GuiScreen parentScreen = guiMultiplayer.getParentScreen();
            this.mc.displayGuiScreen(new GuiMultiplayer(parentScreen));
        }
    }

    private void importAlts()
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setVisible(true);
        chooser.setSize(500, 400);
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new FileNameExtensionFilter("File", new String[] {"txt"}));
        JFrame frame = new JFrame("Select a file");
        chooser.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if ((e.getActionCommand().equals("ApproveSelection")) && (chooser.getSelectedFile() != null))
                {
                    try
                    {
                        Scanner scanner = new Scanner(new FileReader(chooser.getSelectedFile()));
                        scanner.useDelimiter("\n");

                        while (scanner.hasNext())
                        {
                            String[] split = scanner.next().trim().split(":");
                            Gun.getInstance().getAccountManager().getRegistry().add(new Account(split[0], split[1]));
                        }

                        scanner.close();
                    }
                    catch (FileNotFoundException e1)
                    {
                        e1.printStackTrace();
                    }

                    try
                    {
                        StringBuilder data = new StringBuilder();

                        for (Account alt : Gun.getInstance().getAccountManager().getRegistry())
                        {
                            data.append(alt.getFileLine() + "\n");
                        }

                        BufferedWriter writer = new BufferedWriter(
                            new FileWriter(Gun.getInstance().getDirectory() + "/accounts.txt"));
                        writer.write(data.toString());
                        writer.close();
                    }
                    catch (Exception localException)
                    {
                        localException.printStackTrace();
                    }

                    frame.setVisible(false);
                    frame.dispose();
                }

                if (e.getActionCommand().equals("CancelSelection"))
                {
                    frame.setVisible(false);
                    frame.dispose();
                }
            }
        });
        frame.setAlwaysOnTop(true);
        frame.add(chooser);
        frame.setVisible(true);
        frame.setSize(750, 600);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        accountSlot.drawScreen(mouseX, mouseY, partialTicks);
        mc.fontRenderer.drawStringWithShadow(Minecraft.getMinecraft().getSession().getUsername(), this.width - mc.fontRenderer.getStringWidth(Minecraft.getMinecraft().getSession().getUsername()) - 2, 2, 0xa0a0a0);
        mc.fontRenderer.drawStringWithShadow("Accounts: " + Gun.getInstance().getAccountManager().getRegistry().size(), 2, 2, 0xa0a0a0);
        if (GuiMainMenu.ipAddress == null) {
            Minecraft.getMinecraft().fontRenderer.drawString("Connecting...", width - Minecraft.getMinecraft().fontRenderer.getStringWidth("Connecting...") - 2, height - Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT - 2, 0xa0a0a0);
        }
        else {
            Minecraft.getMinecraft().fontRenderer.drawString(GuiMainMenu.ipAddress, width - Minecraft.getMinecraft().fontRenderer.getStringWidth(GuiMainMenu.ipAddress) - 2, height - Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT - 2, 0xa0a0a0);
        }
        if (dispErrorString.length() > 1)
        {
            timer += 1;

            if (timer > 100)
            {
                dispErrorString = "";
                timer = 0;
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}

