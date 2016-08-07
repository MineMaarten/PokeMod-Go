package com.minemaarten.pokemodgo.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkHandler{

    private static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("PokeModGo");
    private static int discriminant;

    public static void init(){

        INSTANCE.registerMessage(PacketSyncPokedex.class, PacketSyncPokedex.class, discriminant++, Side.CLIENT);
        INSTANCE.registerMessage(PacketSpawnPokemon.class, PacketSpawnPokemon.class, discriminant++, Side.SERVER);

    }

    public static void sendToAll(IMessage message){

        INSTANCE.sendToAll(message);
    }

    public static void sendTo(IMessage message, EntityPlayerMP player){

        INSTANCE.sendTo(message, player);
    }

    public static void sendToAllAround(IMessage message, NetworkRegistry.TargetPoint point){

        INSTANCE.sendToAllAround(message, point);
    }

    public static void sendToDimension(IMessage message, int dimensionId){

        INSTANCE.sendToDimension(message, dimensionId);
    }

    public static void sendToServer(IMessage message){

        INSTANCE.sendToServer(message);
    }
}
