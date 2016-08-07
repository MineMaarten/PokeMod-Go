package com.minemaarten.pokemodgo.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

import com.minemaarten.pokemodgo.PokeModGo;
import com.minemaarten.pokemodgo.entity.EntityPokemon;
import com.minemaarten.pokemodgo.entity.EntityPokemonGround;
import com.minemaarten.pokemodgo.entity.EntityPokemonWater;
import com.minemaarten.pokemodgo.pokemon.Pokemon;

public class PacketSpawnPokemon extends AbstractPacket<PacketSpawnPokemon>{

    private int spawnedId;

    public PacketSpawnPokemon(){}

    public PacketSpawnPokemon(int spawnedId){
        this.spawnedId = spawnedId;
    }

    @Override
    public void fromBytes(ByteBuf buf){
        spawnedId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf){
        buf.writeInt(spawnedId);
    }

    @Override
    public void handleClientSide(EntityPlayer player){}

    @Override
    public void handleServerSide(EntityPlayer player){
        if(player.isCreative()) {
            Pokemon pokemon = PokeModGo.instance.getPokemon(spawnedId);
            EntityPokemon entity;
            if(pokemon == null || !pokemon.stringTypes.contains("water")) {
                entity = new EntityPokemonGround(player.worldObj);
            } else {
                entity = new EntityPokemonWater(player.worldObj);
            }
            entity.setPokemonId(spawnedId);
            entity.setPosition(player.posX, player.posY, player.posZ);
            player.worldObj.spawnEntityInWorld(entity);
        }
    }

}
