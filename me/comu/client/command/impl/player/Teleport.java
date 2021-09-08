package me.comu.client.command.impl.player;

import me.comu.client.command.Argument;
import me.comu.client.command.Command;


public final class Teleport extends Command
{
    public Teleport()
    {
        super(new String[] {"Teleport", "tp", "telep"}, new Argument("x"), new Argument("y"), new Argument("z"));
    }
    

    @Override
    public String dispatch()
    {
    	String x = getArgument("x").getValue();
    	String y = getArgument("y").getValue();
    	String z = getArgument("z").getValue();
    	
    //    if (Minecraft.getMinecraft().thePlayer.ridingEntity == null && Minecraft.getMinecraft().thePlayer.getSleepTimer() < 0) {
      //      return null;
       // }
        //for (int i = 0; i < 20; ++i) {
         //   Minecraft.getMinecraft().playerController.attackEntity(Minecraft.getMinecraft().thePlayer, Minecraft.getMinecraft().thePlayer.ridingEntity);
        //}
  //      Minecraft.getMinecraft().thePlayer.setPosition(posX, posY, posZ);
    //    Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(posX, posY, posZ, true));
    	minecraft.thePlayer.posX = Double.parseDouble(x);
    	minecraft.thePlayer.posY = Double.parseDouble(y);
    	minecraft.thePlayer.posZ = Double.parseDouble(z);
    	
        
       return "Teleported to " + x+", " +y+" ," +z;
        
    
    }
}
