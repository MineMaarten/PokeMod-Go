package com.minemaarten.pokemodgo.pokemon;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;
import com.minemaarten.pokemodgo.PokeModGo;

public class PokemonCache{
    private final Map<Integer, Future<Pokemon>> cache = Maps.newHashMap();
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    public void buildCache(){
        long start = System.nanoTime();
        for(int i = 1; i <= 151; i++) {
            getPokemon(i);
        }
        try {
            executor.shutdown();
            executor.awaitTermination(60, TimeUnit.MINUTES);
            System.out.println("Successfully built cache in " + (System.nanoTime() - start) / 1000000000 + " seconds");
            Set<String> typeSet = new HashSet<String>();
            for(Future<Pokemon> pokemon : cache.values()) {
                typeSet.addAll(pokemon.get().stringTypes);
            }
            System.out.println("All types: " + StringUtils.join(typeSet, ", "));
        } catch(InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public Future<Pokemon> getPokemon(int id){
        Future<Pokemon> result = cache.get(id);
        if(result == null) {
            System.out.println("Requesting new pokemon...");
            result = executor.submit(() -> {
                try {
                    System.out.println("Requesting new pokemon 2...");
                    return new Pokemon(id);
                } catch(Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException();
                }
            });
            cache.put(id, result);
        }
        return result;
    }

    public Stream<Pokemon> getLoadedPokemon(){
        return cache.entrySet().stream().filter(x -> x.getValue().isDone()).map(x -> PokeModGo.instance.getPokemon(x.getKey()));
    }
}
