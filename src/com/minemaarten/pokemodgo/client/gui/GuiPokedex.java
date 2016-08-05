package com.minemaarten.pokemodgo.client.gui;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import com.minemaarten.pokemodgo.PokeModGo;
import com.minemaarten.pokemodgo.lib.Constants;
import com.minemaarten.pokemodgo.pokedex.Pokedex;
import com.minemaarten.pokemodgo.pokemon.Pokemon;

public class GuiPokedex extends GuiScreen{

    private static final int WIDTH = 225, HEIGHT = 180;
    private static final int POKEMON_START_X = 24, POKEMON_START_Y = 6, POKEMON_SIZE = 32, POKEMON_PER_ROW = 5,
            ROWS = 5;
    private static final ResourceLocation TEXTURE = new ResourceLocation(Constants.MOD_ID, "textures/gui/pokedex.png");
    private int scrollRow;
    private boolean showCaughtPokemon = true;//False means 'still to catch'

    @Override
    public void initGui(){
        super.initGui();
        int guiLeft = width / 2 - WIDTH / 2;
        int guiTop = height / 2 - HEIGHT / 2;
        buttonList.add(new GuiButton(0, guiLeft + 200, guiTop + 40, 20, 20, "^"));
        buttonList.add(new GuiButton(1, guiLeft + 200, guiTop + 70, 20, 20, "V"));
        buttonList.add(new GuiButton(2, guiLeft + 20, guiTop + 70, 20, 20, "Toggle"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks){
        drawDefaultBackground();
        mc.getTextureManager().bindTexture(TEXTURE);

        int guiLeft = width / 2 - WIDTH / 2;
        int guiTop = height / 2 - HEIGHT / 2;
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, WIDTH, HEIGHT);

        super.drawScreen(mouseX, mouseY, partialTicks);

        List<Pokemon> pokemons = getVisiblePokemon();
        for(int i = 0; i < pokemons.size(); i++) {
            Pokemon p = pokemons.get(i);
            if(p != null) {
                p.bindTexture();
                drawModalRectWithCustomSizedTexture(guiLeft + POKEMON_START_X + (POKEMON_SIZE + 2) * (i % POKEMON_PER_ROW), guiTop + POKEMON_START_Y + (POKEMON_SIZE + 2) * (i / POKEMON_PER_ROW), 0, 0, POKEMON_SIZE, POKEMON_SIZE, POKEMON_SIZE, POKEMON_SIZE);
            }
        }

        List<String> tooltip = new ArrayList<String>();
        for(int i = 0; i < pokemons.size(); i++) {
            Pokemon p = pokemons.get(i);
            if(p != null) {
                int x = guiLeft + POKEMON_START_X + (POKEMON_SIZE + 2) * (i % POKEMON_PER_ROW);
                int y = guiTop + POKEMON_START_Y + (POKEMON_SIZE + 2) * (i / POKEMON_PER_ROW);
                Rectangle rect = new Rectangle(x, y, POKEMON_SIZE, POKEMON_SIZE);
                if(rect.contains(mouseX, mouseY)) {
                    p.addTooltip(tooltip);
                }
            }
        }

        drawHoveringText(tooltip, mouseX, mouseY);
    }

    private Stream<Pokemon> getAllApplicablePokemon(){
        Pokedex pokedex = PokeModGo.instance.pokedexManager.getPokedex(Minecraft.getMinecraft().thePlayer);
        List<Future<Pokemon>> pokemons = showCaughtPokemon ? pokedex.getAllPokemon() : pokedex.getUncaughtPokemon();
        return pokemons.stream().map(x -> {
            try {
                return x.isDone() ? x.get() : null;
            } catch(InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    private List<Pokemon> getVisiblePokemon(){
        return getAllApplicablePokemon().skip(scrollRow * POKEMON_PER_ROW).limit(POKEMON_PER_ROW * ROWS).collect(Collectors.toList());
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException{
        super.actionPerformed(button);
        switch(button.id){
            case 0:
                if(scrollRow > 0) scrollRow--;
                break;
            case 1:
                if(getAllApplicablePokemon().count() > POKEMON_PER_ROW * ROWS + scrollRow * POKEMON_PER_ROW) scrollRow++;
                break;
            case 2:
                showCaughtPokemon = !showCaughtPokemon;
                break;
        }
    }

    @Override
    public boolean doesGuiPauseGame(){
        return false;
    }
}
