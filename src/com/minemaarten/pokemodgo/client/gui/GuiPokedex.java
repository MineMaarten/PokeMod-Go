package com.minemaarten.pokemodgo.client.gui;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import com.minemaarten.pokemodgo.PokeModGo;
import com.minemaarten.pokemodgo.pokedex.Pokedex;
import com.minemaarten.pokemodgo.pokemon.Pokemon;

public class GuiPokedex extends GuiScreen{

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks){
        super.drawScreen(mouseX, mouseY, partialTicks);

        Pokedex pokedex = PokeModGo.instance.pokedexManager.getPokedex(Minecraft.getMinecraft().thePlayer);
        List<Future<Pokemon>> pokemons = pokedex.getAllPokemon();
        for(int i = 0; i < pokemons.size(); i++) {
            Future<Pokemon> pokemon = pokemons.get(i);
            if(pokemon.isDone()) {
                try {
                    Pokemon p = pokemon.get();
                    p.bindTexture();
                    int size = 32;
                    int pokemonPerRow = 6;
                    drawModalRectWithCustomSizedTexture(50 + size * (i %pokemonPerRow), 40 + size * (i / pokemonPerRow), 0, 0, size, size, size, size);
                } catch(InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
