package com.minemaarten.pokemodgo.pokedex;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.minemaarten.pokemodgo.PokeModGo;
import com.minemaarten.pokemodgo.pokemon.Pokemon;

public class Pokedex{
    private final UUID playerUUID;
    private final String playerName;
    private final List<Integer> pokemonIndeces = new ArrayList<>();

    public Pokedex(){
        playerUUID = null;
        playerName = null;
    }

    public Pokedex(UUID playerUUID, String playerName){
        this.playerUUID = playerUUID;
        this.playerName = playerName;
    }

    public UUID getPlayerUUID(){
        return playerUUID;
    }

    public String getPlayerName(){
        return playerName;
    }

    public boolean hasCaught(int id){
        return pokemonIndeces.contains(id);
    }

    public void addPokemon(int id){
        pokemonIndeces.add(id);
    }

    public List<Integer> getAllPokemonIds(){
        return pokemonIndeces;
    }

    public List<Future<Pokemon>> getAllPokemon(){
        return pokemonIndeces.stream().map(x -> PokeModGo.instance.pokemonCache.getPokemon(x)).collect(Collectors.toList());
    }

    public List<Future<Pokemon>> getUncaughtPokemon(){
        return IntStream.range(1, PokeModGo.MAX_POKEMON_ID + 1).filter(x -> !pokemonIndeces.contains(x)).mapToObj(x -> PokeModGo.instance.pokemonCache.getPokemon(x)).collect(Collectors.toList());
    }

    public void load(List<Integer> pokemonIndeces){
        this.pokemonIndeces.clear();
        this.pokemonIndeces.addAll(pokemonIndeces);
    }
}
