package me.comu.client.waypoints;

import me.comu.api.registry.ListRegistry;
import me.comu.client.config.Config;
import me.comu.client.module.impl.toggle.render.Waypoints;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public final class WaypointManager extends ListRegistry<Point>
{
    public WaypointManager()
    {
        this.registry = new ArrayList<>();
        new Config("waypoints.json")
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
                            Waypoints.points.add(new Point(split[0].trim(), Integer.parseInt(split[1].trim()), Integer.parseInt(split[2].trim()), Integer.parseInt(split[3].trim()), Float.parseFloat(split[4].trim()), Float.parseFloat(split[5].trim()), Float.parseFloat(split[6].trim())));
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

                    for (Point point : Waypoints.points)
                    {
                        bw.write(point.getLabel() + ":" + point.getX() + ":" + point.getY() + ":" + point.getZ() + " : " + point.getColor()[0] + ":" + point.getColor()[1] + ":" + point.getColor()[2]);
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
