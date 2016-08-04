package com.minemaarten.pokemodgo.init;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.minemaarten.pokemodgo.item.ItemPokeball;
import com.minemaarten.pokemodgo.item.ItemPokedex;

public class ModItems{
    public static Item pokeball;
    public static Item pokedex;

    public static void init(){
        pokeball = new ItemPokeball();
        pokedex = new ItemPokedex();

        GameRegistry.register(pokeball);
        GameRegistry.register(pokedex);
    }
}
