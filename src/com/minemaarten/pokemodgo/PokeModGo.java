package com.minemaarten.pokemodgo;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import net.minecraft.entity.EntityLiving.SpawnPlacementType;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import com.minemaarten.pokemodgo.entity.EntityPokeball;
import com.minemaarten.pokemodgo.entity.EntityPokemonGround;
import com.minemaarten.pokemodgo.entity.EntityPokemonWater;
import com.minemaarten.pokemodgo.event.PokeModEventHandler;
import com.minemaarten.pokemodgo.init.ModItems;
import com.minemaarten.pokemodgo.lib.Constants;
import com.minemaarten.pokemodgo.network.NetworkHandler;
import com.minemaarten.pokemodgo.pokedex.PokedexManager;
import com.minemaarten.pokemodgo.pokemon.Pokemon;
import com.minemaarten.pokemodgo.pokemon.PokemonCache;
import com.minemaarten.pokemodgo.pokemon.PokemonSpawnRules;
import com.minemaarten.pokemodgo.proxy.CommonProxy;

@Mod(modid = Constants.MOD_ID, name = "PokeMod Go", acceptedMinecraftVersions = "[1.9.4,]")
public class PokeModGo{
    public PokemonCache pokemonCache;
    public PokedexManager pokedexManager;
    public final PokemonSpawnRules pokemonSpawnRules = new PokemonSpawnRules();

    @SidedProxy(clientSide = "com.minemaarten.pokemodgo.proxy.ClientProxy", serverSide = "com.minemaarten.pokemodgo.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Instance(Constants.MOD_ID)
    public static PokeModGo instance;

    @EventHandler
    public void PreInit(FMLPreInitializationEvent event){

        pokemonCache = new PokemonCache(event.getModConfigurationDirectory());
        pokemonCache.buildCache();
        pokedexManager = PokedexManager.create(event.getModConfigurationDirectory());

        ModItems.init();

        //EntityRegistry.registerModEntity(EntityPokemonFlying.class, "pokemon", 0, instance, 64, 1, true, 0xFFFFFFFF, 0xFFFFFFFF);
        EntityRegistry.registerModEntity(EntityPokemonGround.class, "pokemonGround", 1, instance, 64, 1, true, 0xFFFFFFFF, 0xFFFFFFFF);
        EntityRegistry.registerModEntity(EntityPokemonWater.class, "pokemonWater", 2, instance, 64, 1, true, 0xFFFFFFFF, 0xFFFFFFFF);
        EntityRegistry.registerModEntity(EntityPokeball.class, "pokeball", 3, instance, 64, 1, true, 0xFFFFFFFF, 0xFFFFFFFF);
        EntitySpawnPlacementRegistry.setPlacementType(EntityPokemonGround.class, SpawnPlacementType.ON_GROUND);
        EntitySpawnPlacementRegistry.setPlacementType(EntityPokemonWater.class, SpawnPlacementType.IN_WATER);

        MinecraftForge.EVENT_BUS.register(new PokeModEventHandler());
        proxy.preInit();

    }

    @EventHandler
    public void load(FMLInitializationEvent event){
        proxy.init();
        NetworkHandler.init();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event){
        proxy.postInit();
        pokemonSpawnRules.init();
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
