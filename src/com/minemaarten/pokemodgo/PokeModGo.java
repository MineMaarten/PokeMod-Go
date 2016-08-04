package com.minemaarten.pokemodgo;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import com.minemaarten.pokemodgo.event.PokeModEventHandler;
import com.minemaarten.pokemodgo.lib.Constants;
import com.minemaarten.pokemodgo.pokemon.Pokemon;
import com.minemaarten.pokemodgo.pokemon.PokemonCache;
import com.minemaarten.pokemodgo.proxy.CommonProxy;

@Mod(modid = Constants.MOD_ID, name = "PokeMod Go", acceptedMinecraftVersions = "[1.9.4,]")
public class PokeModGo{
    private final PokemonCache pokemonCache = new PokemonCache();

    @SidedProxy(clientSide = "com.minemaarten.pokemodgo.proxy.ClientProxy", serverSide = "com.minemaarten.pokemodgo.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Instance(Constants.MOD_ID)
    public static PokeModGo instance;

    @EventHandler
    public void PreInit(FMLPreInitializationEvent event){

        proxy.preInit();
        MinecraftForge.EVENT_BUS.register(new PokeModEventHandler());
    }

    @EventHandler
    public void load(FMLInitializationEvent event){
        proxy.init();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event){
        proxy.postInit();
    }

    public Pokemon getPokemon(int id){
        Future<Pokemon> future = pokemonCache.getPokemon(id);
        try {
            return future.isDone() ? future.get() : null;
        } catch(InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

}
