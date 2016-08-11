package com.minemaarten.pokemodgo.pokemon;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.concurrent.ConcurrentUtils;

import com.google.common.collect.Maps;
import com.minemaarten.pokemodgo.PokeModGo;
import com.minemaarten.pokemodgo.lib.GsonUtils;

public class PokemonCache{
    private final Map<Integer, Future<Pokemon>> cache = Maps.newHashMap();
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private final String CACHE_FOLDER;

    public PokemonCache(File configFolder){
        CACHE_FOLDER = configFolder.getAbsolutePath() + "\\PokeModGo\\PokemonCache\\";
        new File(CACHE_FOLDER).mkdirs();
    }

    public void buildCache(){
        for(int i = PokeModGo.MIN_POKEMON_ID; i <= PokeModGo.MAX_POKEMON_ID; i++) {
            getPokemon(i);
        }
    }

    public Future<Pokemon> getPokemon(int id){
        Future<Pokemon> result = cache.get(id);
        if(result == null) {
            Pokemon pokemon = getPokemonFromFileCache(id);
            if(pokemon != null) {
                result = ConcurrentUtils.constantFuture(pokemon);
            } else {
                //System.out.println("Requesting new pokemon from pokeapi.co...");
                result = executor.submit(() -> {
                    try {
                        Pokemon p = new Pokemon(id);
                        savePokemonToFile(p);
                        return p;
                    } catch(Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                });
            }
            cache.put(id, result);
        }
        return result;
    }

    public Stream<Pokemon> getLoadedPokemon(){
        return cache.entrySet().stream().filter(x -> x.getValue().isDone()).map(x -> PokeModGo.instance.getPokemon(x.getKey())).filter(x -> x != null);
    }

    private Pokemon getPokemonFromFileCache(int id){
        File file = new File(CACHE_FOLDER + "Pokemon\\" + id + ".json");//config/PokeModgo/PokemonCache/pokemon/1.json
        return GsonUtils.readFromFile(Pokemon.class, file);
    }

    private void savePokemonToFile(Pokemon pokemon){
        new File(CACHE_FOLDER + "Pokemon\\").mkdirs();
        File file = new File(CACHE_FOLDER + "Pokemon\\" + pokemon.id + ".json");//config/PokeModgo/PokemonCache/pokemon/1.json
        GsonUtils.writeToFile(pokemon, file);
    }

    public void saveTextureToFile(BufferedImage image, int pokemonId){
        File file = new File(CACHE_FOLDER + "Pokemon\\" + pokemonId + ".png");
        try {
            ImageIO.write(image, "png", file);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public BufferedImage getTextureFromFileCache(int pokemonId){
        File file = new File(CACHE_FOLDER + "Pokemon\\" + pokemonId + ".png");
        if(!file.exists()) return null;
        try {
            return ImageIO.read(file);
        } catch(IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<String> getAllTypes(){
        return getLoadedPokemon().flatMap(x -> x.stringTypes.stream()).distinct().collect(Collectors.toList());
    }
}
