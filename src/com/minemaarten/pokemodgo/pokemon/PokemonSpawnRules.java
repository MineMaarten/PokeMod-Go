package com.minemaarten.pokemodgo.pokemon;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;

import com.minemaarten.pokemodgo.PokeModGo;

public class PokemonSpawnRules{
    private final Map<Biome, Set<String>> biomeToHabitats = new HashMap<Biome, Set<String>>();

    public void init(){
        register(Biomes.BEACH, "waters-edge");
        register(Biomes.BIRCH_FOREST, "forest");
        register(Biomes.BIRCH_FOREST_HILLS, "forest", "mountain");
        register(Biomes.COLD_BEACH, "waters-edge");
        register(Biomes.COLD_TAIGA);
        register(Biomes.COLD_TAIGA_HILLS);
        register(Biomes.DEEP_OCEAN, "sea");
        register(Biomes.DESERT, "rough-terrain");
        register(Biomes.DESERT_HILLS, "mountain", "rough-terrain");
        register(Biomes.EXTREME_HILLS, "mountain");
        register(Biomes.EXTREME_HILLS_EDGE, "mountain");
        register(Biomes.EXTREME_HILLS_WITH_TREES, "mountain");
        register(Biomes.FOREST, "forest");
        register(Biomes.FOREST_HILLS, "forest", "mountain");
        register(Biomes.FROZEN_OCEAN, "waters-edge");
        register(Biomes.FROZEN_RIVER, "waters-edge");
        register(Biomes.HELL, "fire"); //Not a habitat
        register(Biomes.ICE_MOUNTAINS, "mountain");
        register(Biomes.ICE_PLAINS, "grassland");
        register(Biomes.JUNGLE, "forest");
        register(Biomes.JUNGLE_EDGE, "forest");
        register(Biomes.JUNGLE_HILLS, "forest");
        register(Biomes.MESA, "rough-terrain");
        register(Biomes.MESA_CLEAR_ROCK, "rough-terrain");
        register(Biomes.MESA_ROCK, "rough-terrain");
        register(Biomes.MUSHROOM_ISLAND, "grassland");
        register(Biomes.MUSHROOM_ISLAND_SHORE, "grassland", "waters-edge");
        register(Biomes.MUTATED_BIRCH_FOREST, "forest");
        register(Biomes.MUTATED_BIRCH_FOREST_HILLS, "forest", "mountain");
        register(Biomes.MUTATED_DESERT, "rough-terrain");
        register(Biomes.MUTATED_EXTREME_HILLS, "mountain");
        register(Biomes.MUTATED_EXTREME_HILLS_WITH_TREES, "mountain");
        register(Biomes.MUTATED_FOREST, "forest");
        register(Biomes.MUTATED_ICE_FLATS, "grasslands");
        register(Biomes.MUTATED_JUNGLE, "forest");
        register(Biomes.MUTATED_JUNGLE_EDGE, "forest");
        register(Biomes.MUTATED_MESA, "rough-terrain");
        register(Biomes.MUTATED_MESA_CLEAR_ROCK, "rough-terrain");
        register(Biomes.MUTATED_MESA_ROCK, "rough-terrain");
        register(Biomes.MUTATED_PLAINS, "grassland");
        register(Biomes.MUTATED_REDWOOD_TAIGA, "forest");
        register(Biomes.MUTATED_REDWOOD_TAIGA_HILLS, "forest");
        register(Biomes.MUTATED_ROOFED_FOREST, "forest");
        register(Biomes.MUTATED_SAVANNA, "grassland");
        register(Biomes.MUTATED_SAVANNA_ROCK, "grassland");
        register(Biomes.MUTATED_SWAMPLAND, "grassland");
        register(Biomes.MUTATED_TAIGA, "forest");
        register(Biomes.MUTATED_TAIGA_COLD, "forest");
        register(Biomes.OCEAN, "sea");
        register(Biomes.PLAINS, "grassland");
        register(Biomes.REDWOOD_TAIGA, "forest");
        register(Biomes.REDWOOD_TAIGA_HILLS, "forest");
        register(Biomes.RIVER, "sea", "waters-edge");
        register(Biomes.ROOFED_FOREST, "forest");
        register(Biomes.SAVANNA, "grassland");
        register(Biomes.SAVANNA_PLATEAU, "grassland");
        register(Biomes.SKY, "rare");
        register(Biomes.STONE_BEACH, "rough-terrain");
        register(Biomes.SWAMPLAND, "grassland");
        register(Biomes.TAIGA, "forest");
        register(Biomes.TAIGA_HILLS, "forest");
        register(Biomes.VOID);
    }

    private void register(Biome biome, String... habitats){
        Set<String> set = new HashSet<String>();
        Collections.addAll(set, habitats);
        biomeToHabitats.put(biome, set);
    }

    public Pokemon getRandomPokemonFor(Biome biome){
        Set<String> habitats = biomeToHabitats.get(biome);
        if(habitats != null && !habitats.isEmpty()) {
            List<Pokemon> matchingPokemon = PokeModGo.instance.pokemonCache.getLoadedPokemon().filter(x -> habitats.contains(x.habitat)).collect(Collectors.toList());
            return matchingPokemon.isEmpty() ? null : matchingPokemon.get(new Random().nextInt(matchingPokemon.size()));
        } else {
            return null;
        }
    }
}
