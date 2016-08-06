package com.minemaarten.pokemodgo.pokedex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;

import com.minemaarten.pokemodgo.lib.GsonUtils;
import com.minemaarten.pokemodgo.persistency.PokemodWorldData;
import com.minemaarten.pokemodgo.pokemon.Pokemon;

public class PokedexManager{
    private List<Pokedex> pokedexes = new ArrayList<Pokedex>();
    private transient Map<UUID, Pokedex> playerToPokedex = new HashMap<UUID, Pokedex>();

    public void init(){
        for(Pokedex pokedex : pokedexes) {
            playerToPokedex.put(pokedex.getPlayerUUID(), pokedex);
        }
    }

    public void addPokemon(EntityPlayer player, int pokemonId){
        getPokedex(player).addPokemon(pokemonId);
        save();
    }

    public void addPokemon(EntityPlayer player, Pokemon pokemon){
        addPokemon(player, pokemon.id);
    }

    public Pokedex getPokedex(EntityPlayer player){
        return getPokedex(player.getGameProfile().getId(), player.getGameProfile().getName());
    }

    public Pokedex getPokedex(UUID uuid, String playerName){
        Pokedex pokedex = playerToPokedex.get(uuid);
        if(pokedex == null) {
            pokedex = new Pokedex(uuid, playerName);
            playerToPokedex.put(uuid, pokedex);
            pokedexes.add(pokedex);
        }
        return pokedex;
    }

    private void save(){
        PokemodWorldData.getInstance().setPokedexJson(GsonUtils.toJson(this));
    }

    public static PokedexManager create(){
        String json = PokemodWorldData.getInstance().getPokedexJson();
        PokedexManager pokedexManager;
        if(json.equals("")) {
            pokedexManager = new PokedexManager();
        } else {
            pokedexManager = GsonUtils.readFromJson(PokedexManager.class, json);
        }
        pokedexManager.init();
        return pokedexManager;
    }
}
