package me.comu.client.command.impl.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import me.comu.api.stopwatch.Stopwatch;
import me.comu.client.command.Command;
import me.comu.client.logging.Logger;
import net.minecraft.util.ScreenShotHelper;
import org.apache.commons.codec.binary.Base64;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


public final class ScreenShot extends Command
{
    public ScreenShot()
    {
        super(new String[] {"screenshot","ss","screenie","screen","shot"});
    }
    private final Stopwatch stopwatch = new Stopwatch();

    @Override
    public String dispatch()
    {
        ScreenShotHelper.saveScreenshot(minecraft.mcDataDir, minecraft.displayWidth, minecraft.displayHeight, minecraft.getFramebuffer());
        File screenshots = new File("screenshots");
        File[] files = screenshots.listFiles(File::isFile);
        long timeModified = -9223372036854775808L;
        File lastModified = null;

        for (File file : files)
        {
            if (file.lastModified() > timeModified)
            {
                lastModified = file;
                timeModified = file.lastModified();
            }
        }
        

        if (lastModified != null)
        {
            uploadImage(lastModified);
        }
        else
        {
            Logger.getLogger().printToChat("Unable to locate screenshot.");
        }

        return "Uploading screenshot!";
    }
    public void uploadImage(File file)
    {
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    BufferedImage image = ImageIO.read(new File(file.getAbsolutePath()));
                    ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
                    ImageIO.write(image, "png", byteArray);
                    byte[] fileByes = byteArray.toByteArray();
                    String base64File = Base64.encodeBase64String(fileByes);
                    String postData = URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(base64File, "UTF-8");
                    URL imgurApi = new URL("https://api.imgur.com/3/image");
                    HttpURLConnection connection = (HttpURLConnection) imgurApi.openConnection();
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Authorization", "Client-ID 57e0280fe5e3a5e");
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.connect();
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
                    outputStreamWriter.write(postData);
                    outputStreamWriter.flush();
                    outputStreamWriter.close();
                    StringBuilder stringBuilder = new StringBuilder();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;

                    while ((line = rd.readLine()) != null)
                    {
                        stringBuilder.append(line).append(System.lineSeparator());
                    }

                    rd.close();
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    JsonObject json = gson.fromJson(stringBuilder.toString(), JsonObject.class);
                    String url = "http://i.imgur.com/" + json.get("data").getAsJsonObject().get("id").getAsString() + ".png";
                    StringSelection contents = new StringSelection(url);
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(contents, null);
                    Logger.getLogger().printToChat("Screenshot URL copied to clipboard.");
                }
                catch (IOException e)
                {
                    Logger.getLogger().printToChat("Unable to upload screenshot.");
                    e.printStackTrace();
                }

                if (!file.delete())
                {
                    Logger.getLogger().printToChat("Unable to delete screenshot.");
                }
            }
        });
        thread.setName("Screenshot Upload Thread");
        thread.start();
    }
}
