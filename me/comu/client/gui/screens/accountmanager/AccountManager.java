package me.comu.client.gui.screens.accountmanager;

import me.comu.api.registry.ListRegistry;
import me.comu.client.config.Config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public final class AccountManager extends ListRegistry<Account>
{
    public AccountManager()
    {
        this.registry = new ArrayList<>();
        new Config("accounts.txt")
        {
            @Override
            public void load(Object... source)
            {
                try
                {
                    if (!getFile().exists())
                    {
                        getFile().createNewFile();
                    }

                    BufferedReader br = new BufferedReader(new FileReader(getFile()));
                    getRegistry().clear();
                    String readLine;

                    while ((readLine = br.readLine()) != null)
                    {
                        try
                        {
                            String[] split = readLine.split(":");
                            if (split[1].equals("Not Avaliable"))
                            register(new Account(split[0], "Not Avaliable"));
                            else
                            register(new Account(split[0], split[1]));
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }

                    br.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            @Override
            public void save(Object... destination)
            {
                try
                {
                    if (!getFile().exists())
                    {
                        getFile().createNewFile();
                    }

                    BufferedWriter bw = new BufferedWriter(new FileWriter(getFile()));

                    for (Account account : getRegistry())
                    {
                        bw.write(account.getLabel() + ":" + (account.isPremium() ? account.getPassword() :  "Not Avaliable"));
                        bw.newLine();
                    }

                    bw.close();
                }
                catch (Exception exception)
                {
                    exception.printStackTrace();
                }
            }
        };
    }
}
