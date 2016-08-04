package com.minemaarten.pokemodgo.init;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.minemaarten.pokemodgo.item.ItemPokeball;

public class ModItems{
    public static Item pokeball;

    public static void init(){
        pokeball = new ItemPokeball();

        GameRegistry.register(pokeball);
    }
}
