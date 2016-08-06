package com.minemaarten.pokemodgo.persistency;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

import com.minemaarten.pokemodgo.lib.Constants;
import com.minemaarten.pokemodgo.pokedex.PokedexManager;

public class PokemodWorldData extends WorldSavedData{

    public static final String DATA_KEY = Constants.MOD_ID + "_worldData";
    public static World overworld;

    private Set<Integer> spawnedRarePokemon = new HashSet<Integer>();
    private String pokedexJson = "";
    private PokedexManager clientPokedexManager, serverPokedexManager;

    public PokemodWorldData(String name){
        super(name);
    }

    public static PokemodWorldData getInstance(){
        if(overworld != null) {
            PokemodWorldData manager = (PokemodWorldData)overworld.loadItemData(PokemodWorldData.class, DATA_KEY);
            if(manager == null) {
                manager = new PokemodWorldData(DATA_KEY);
                overworld.setItemData(DATA_KEY, manager);
            }
            return manager;
        } else {
            throw new IllegalStateException("Overworld not initialized");
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt){
        spawnedRarePokemon.clear();
        NBTTagList list = nbt.getTagList("spawnedRarePokemon", 10);
        for(int i = 0; i < list.tagCount(); i++) {
            spawnedRarePokemon.add(list.getCompoundTagAt(i).getInteger("id"));
        }

        pokedexJson = nbt.getString("pokedexes");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag){
        NBTTagList list = new NBTTagList();
        for(int id : spawnedRarePokemon) {
            NBTTagCompound t = new NBTTagCompound();
            t.setInteger("id", id);
            list.appendTag(t);
        }
        tag.setTag("spawnedRarePokemon", list);

        tag.setString("pokedexes", pokedexJson);
        return tag;
    }

    public void addRarePokemonSpawn(int id){
        if(spawnedRarePokemon.add(id)) {
            save();
        }
    }

    public void removeRarePokemonSpawn(int id){
        if(spawnedRarePokemon.remove(id)) {
            save();
        }
    }

    public boolean hasPokemonSpawnedAlready(int id){
        return spawnedRarePokemon.contains(id);
    }

    public String getPokedexJson(){
        return pokedexJson;
    }

    public void setPokedexJson(String json){
        pokedexJson = json;
        save();
    }

    private void save(){
        markDirty();
    }

    public PokedexManager getPokedexManager(boolean client){
        if(client) {
            if(clientPokedexManager == null) clientPokedexManager = new PokedexManager();
            return clientPokedexManager;
        } else {
            if(serverPokedexManager == null) serverPokedexManager = PokedexManager.create();
            return serverPokedexManager;
        }
    }
}
