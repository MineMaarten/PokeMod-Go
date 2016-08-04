package com.minemaarten.pokemodgo.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class CommonProxy{

    public void preInit(){}

    public void init(){}

    public void postInit(){}

    public void addScheduledTask(Runnable runnable, boolean serverSide){
        FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(runnable);
    }

    public EntityPlayer getPlayer(){
        return null;
    }

}
