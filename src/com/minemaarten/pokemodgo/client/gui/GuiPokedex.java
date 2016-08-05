package com.minemaarten.pokemodgo.client.gui;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import com.minemaarten.pokemodgo.PokeModGo;
import com.minemaarten.pokemodgo.lib.Constants;
import com.minemaarten.pokemodgo.pokedex.Pokedex;
import com.minemaarten.pokemodgo.pokemon.Pokemon;

public class GuiPokedex extends GuiScreen{

    private static final int WIDTH = 225, HEIGHT = 180;
    private static final int POKEMON_START_X = 24, POKEMON_START_Y = 6, POKEMON_SIZE = 32, POKEMON_PER_ROW = 5;
    private static final ResourceLocation TEXTURE = new ResourceLocation(Constants.MOD_ID, "textures/gui/pokedex.png");

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks){
        drawDefaultBackground();
        mc.getTextureManager().bindTexture(TEXTURE);

        int guiLeft = width / 2 - WIDTH / 2;
        int guiTop = height / 2 - HEIGHT / 2;
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, WIDTH, HEIGHT);

        super.drawScreen(mouseX, mouseY, partialTicks);

        Pokedex pokedex = PokeModGo.instance.pokedexManager.getPokedex(Minecraft.getMinecraft().thePlayer);
        List<Future<Pokemon>> pokemons = pokedex.getAllPokemon();
        for(int i = 0; i < pokemons.size(); i++) {
            Future<Pokemon> pokemon = pokemons.get(i);
            if(pokemon.isDone()) {
                try {
                    Pokemon p = pokemon.get();
                    p.bindTexture();
                    drawModalRectWithCustomSizedTexture(guiLeft + POKEMON_START_X + (POKEMON_SIZE + 2) * (i % POKEMON_PER_ROW), guiTop + POKEMON_START_Y + (POKEMON_SIZE + 2) * (i / POKEMON_PER_ROW), 0, 0, POKEMON_SIZE, POKEMON_SIZE, POKEMON_SIZE, POKEMON_SIZE);
                } catch(InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }

        List<String> tooltip = new ArrayList<String>();
        for(int i = 0; i < pokemons.size(); i++) {
            Future<Pokemon> pokemon = pokemons.get(i);
            if(pokemon.isDone()) {
                try {
                    Pokemon p = pokemon.get();
                    int x = guiLeft + POKEMON_START_X + (POKEMON_SIZE + 2) * (i % POKEMON_PER_ROW);
                    int y = guiTop + POKEMON_START_Y + (POKEMON_SIZE + 2) * (i / POKEMON_PER_ROW);
                    Rectangle rect = new Rectangle(x, y, POKEMON_SIZE, POKEMON_SIZE);
                    if(rect.contains(mouseX, mouseY)) {
                        p.addTooltip(tooltip);
                    }
                } catch(InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }

        drawHoveringText(tooltip, mouseX, mouseY);
    }

    @Override
    public boolean doesGuiPauseGame(){
        return false;
    }
}
