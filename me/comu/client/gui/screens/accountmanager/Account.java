package me.comu.client.gui.screens.accountmanager;

import me.comu.api.interfaces.Labeled;

public class Account implements Labeled
{
    private final String label, password;
    private final boolean premium;

    public Account(String label, String password)
    {
        this.premium = true;
        this.label = label;
        this.password = password;
    }

    public Account(String label)
    {
        this.premium = false;
        this.label = label;
        this.password = "N/A";
    }

    public String getFileLine()
    {
        return premium ? label.concat(":").concat(password) : label;
    }

    @Override
    public String getLabel()
    {
        return label;
    }

    public String getPassword() throws AccountException
    {
        if (premium)
        {
            return password;
        }
        else
        {
            throw new AccountException("Non-Premium accounts do not have passwords!");
        }
    }

    public boolean isPremium()
    {
        return premium;
    }
}
