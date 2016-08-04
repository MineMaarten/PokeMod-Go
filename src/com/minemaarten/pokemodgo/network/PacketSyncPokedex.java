package com.minemaarten.pokemodgo.network;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;

import com.minemaarten.pokemodgo.PokeModGo;

public class PacketSyncPokedex extends AbstractPacket<PacketSyncPokedex>{

    private List<Integer> pokemonIds;

    public PacketSyncPokedex(){}

    public PacketSyncPokedex(EntityPlayer player){
        pokemonIds = PokeModGo.instance.pokedexManager.getPokedex(player).getAllPokemonIds();
    }

    @Override
    public void fromBytes(ByteBuf buf){
        pokemonIds = new ArrayList<Integer>();
        int size = buf.readInt();
        for(int i = 0; i < size; i++) {
            pokemonIds.add(buf.readInt());
        }
    }

    @Override
    public void toBytes(ByteBuf buf){
        buf.writeInt(pokemonIds.size());
        for(int id : pokemonIds) {
            buf.writeInt(id);
        }
    }

    @Override
    public void handleClientSide(EntityPlayer player){
        PokeModGo.instance.pokedexManager.getPokedex(player).load(pokemonIds);
    }

    @Override
    public void handleServerSide(EntityPlayer player){

    }

}
