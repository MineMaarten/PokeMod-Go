package com.minemaarten.pokemodgo;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import net.minecraft.entity.EntityLiving.SpawnPlacementType;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

import com.minemaarten.pokemodgo.entity.EntityPokeball;
import com.minemaarten.pokemodgo.entity.EntityPokemon;
import com.minemaarten.pokemodgo.entity.EntityPokemonFlying;
import com.minemaarten.pokemodgo.entity.EntityPokemonGround;
import com.minemaarten.pokemodgo.entity.EntityPokemonWater;
import com.minemaarten.pokemodgo.event.PokeModEventHandler;
import com.minemaarten.pokemodgo.init.ModItems;
import com.minemaarten.pokemodgo.lib.Constants;
import com.minemaarten.pokemodgo.network.NetworkHandler;
import com.minemaarten.pokemodgo.pokemon.Pokemon;
import com.minemaarten.pokemodgo.pokemon.PokemonCache;
import com.minemaarten.pokemodgo.pokemon.PokemonSpawnRules;
import com.minemaarten.pokemodgo.proxy.CommonProxy;

@Mod(modid = Constants.MOD_ID, name = "PokeMod Go", acceptedMinecraftVersions = "[1.9.4,]")
public class PokeModGo{
    public PokemonCache pokemonCache;
    public final PokemonSpawnRules pokemonSpawnRules = new PokemonSpawnRules();

    @SidedProxy(clientSide = "com.minemaarten.pokemodgo.proxy.ClientProxy", serverSide = "com.minemaarten.pokemodgo.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Instance(Constants.MOD_ID)
    public static PokeModGo instance;

    public static int MIN_POKEMON_ID;
    public static int MAX_POKEMON_ID;

    @EventHandler
    public void PreInit(FMLPreInitializationEvent event){

        Configuration config = new Configuration(new File(event.getModConfigurationDirectory().getAbsolutePath() + File.separator + "PokeModGo" + File.separator + "pokemodgo.cfg"));
        MIN_POKEMON_ID = config.getInt("min_pokemon_id", Configuration.CATEGORY_GENERAL, 1, 1, Integer.MAX_VALUE, "Lower bound of the Pokemon id range used in this instance.");
        MAX_POKEMON_ID = config.getInt("max_pokemon_id", Configuration.CATEGORY_GENERAL, 151, 1, Integer.MAX_VALUE, "Higher bound of the Pokemon id range used in this instance (inclusive).");
        if(config.hasChanged()) config.save();

        pokemonCache = new PokemonCache(event.getModConfigurationDirectory());
        pokemonCache.buildCache();

        ModItems.init();

        EntityRegistry.registerModEntity(EntityPokemonFlying.class, "pokemonFlying", 0, instance, 64, 1, true);
        EntityRegistry.registerModEntity(EntityPokemonGround.class, "pokemonGround", 1, instance, 64, 1, true);
        EntityRegistry.registerModEntity(EntityPokemonWater.class, "pokemonWater", 2, instance, 64, 1, true);
        EntityRegistry.registerModEntity(EntityPokemon.class, "pokemon", 2, instance, 64, 1, true, 0xFFFFFFFF, 0xFFFFFFFF);
        EntityRegistry.registerModEntity(EntityPokeball.class, "pokeball", 3, instance, 64, 1, true);
        EntitySpawnPlacementRegistry.setPlacementType(EntityPokemonFlying.class, SpawnPlacementType.IN_AIR);
        EntitySpawnPlacementRegistry.setPlacementType(EntityPokemonGround.class, SpawnPlacementType.ON_GROUND);
        EntitySpawnPlacementRegistry.setPlacementType(EntityPokemonWater.class, SpawnPlacementType.IN_WATER);

        GameRegistry.addRecipe(new ShapedOreRecipe(ModItems.pokedex, "ppp", "pdp", "ppp", 'p', ModItems.pokeball, 'd', "gemDiamond"));

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
