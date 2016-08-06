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
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import com.minemaarten.pokemodgo.PokeModGo;
import com.minemaarten.pokemodgo.client.gui.widget.WidgetTextField;
import com.minemaarten.pokemodgo.client.gui.widget.WidgetVerticalScrollbar;
import com.minemaarten.pokemodgo.lib.Constants;
import com.minemaarten.pokemodgo.persistency.PokemodWorldData;
import com.minemaarten.pokemodgo.pokedex.Pokedex;
import com.minemaarten.pokemodgo.pokemon.Pokemon;

public class GuiPokedex extends GuiBase{

    private static final int WIDTH = 256, HEIGHT = 208;
    private static final int POKEMON_START_X = 68, POKEMON_START_Y = 20, POKEMON_SIZE = 32, POKEMON_PER_ROW = 5,
            ROWS = 5;
    private static final ResourceLocation TEXTURE = new ResourceLocation(Constants.MOD_ID, "textures/gui/pokedex.png");
    //  private static int scrollRow;
    private static ShowMode showMode = ShowMode.ALL;
    private static SortMode sortMode = SortMode.ID;
    private static String visiblePokemonType = "All";
    private Pokedex pokedex = PokemodWorldData.getInstance().getPokedexManager(true).getPokedex(Minecraft.getMinecraft().thePlayer);

    private enum ShowMode{
        ALL, CAUGHT, UNCAUGHT;
    }

    private enum SortMode{
        ID, NAME, CATCH_ORDER;
    }

    private WidgetTextField searchWidget;
    private WidgetVerticalScrollbar scrollBar;
    private List<String> availablePokemonTypes = PokeModGo.instance.pokemonCache.getAllTypes();

    @Override
    public void initGui(){
        super.initGui();
        int guiLeft = width / 2 - WIDTH / 2;
        int guiTop = height / 2 - HEIGHT / 2;
        // buttonList.add(new GuiButton(0, guiLeft + 200, guiTop + 40, 20, 20, "^"));
        // buttonList.add(new GuiButton(1, guiLeft + 200, guiTop + 70, 20, 20, "V"));
        buttonList.add(new GuiButton(2, guiLeft + 5, guiTop + 66, 52, 20, showMode.name()));
        buttonList.add(new GuiButton(3, guiLeft + 5, guiTop + 88, 52, 20, visiblePokemonType));
        buttonList.add(new GuiButton(4, guiLeft + 5, guiTop + 127, 52, 20, sortMode.name()));
        searchWidget = new WidgetTextField(fontRendererObj, guiLeft + 5, guiTop + 36, 52, fontRendererObj.FONT_HEIGHT);
        addWidget(searchWidget);
        scrollBar = new WidgetVerticalScrollbar(guiLeft + 240, guiTop + 19, 170).setListening(true);
        addWidget(scrollBar);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks){
        drawDefaultBackground();
        mc.getTextureManager().bindTexture(TEXTURE);

        int guiLeft = width / 2 - WIDTH / 2;
        int guiTop = height / 2 - HEIGHT / 2;
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, WIDTH, HEIGHT);

        int states = ((int)(getAllApplicablePokemon().count()) - POKEMON_PER_ROW * ROWS + POKEMON_PER_ROW - 1) / POKEMON_PER_ROW;
        if(states > 1) {
            scrollBar.setStates(states);
            scrollBar.setEnabled(true);
        } else {
            scrollBar.setStates(1);
            scrollBar.setCurrentState(0);
            scrollBar.setEnabled(false);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        List<Pokemon> pokemons = getVisiblePokemon();
        for(int i = 0; i < pokemons.size(); i++) {
            Pokemon p = pokemons.get(i);
            if(p != null) {
                p.bindTexture();
                if(pokedex.hasCaught(p.id)) {
                    GlStateManager.color(1, 1, 1, 1);
                } else {
                    GlStateManager.color(1, 1, 1, 0.5F);
                }
                drawModalRectWithCustomSizedTexture(guiLeft + POKEMON_START_X + (POKEMON_SIZE + 2) * (i % POKEMON_PER_ROW), guiTop + POKEMON_START_Y + (POKEMON_SIZE + 2) * (i / POKEMON_PER_ROW), 0, 0, POKEMON_SIZE, POKEMON_SIZE, POKEMON_SIZE, POKEMON_SIZE);
            }
        }
        GlStateManager.disableBlend();

        fontRendererObj.drawString("Pokedex", guiLeft + 82, guiTop + 5, 0xFFFFFF);
        fontRendererObj.drawString("Search: ", guiLeft + 5, guiTop + 25, 0xFFFFFF);
        fontRendererObj.drawString("Show: ", guiLeft + 5, guiTop + 55, 0xFFFFFF);
        fontRendererObj.drawString("Sort by: ", guiLeft + 5, guiTop + 116, 0xFFFFFF);

        fontRendererObj.drawString("Results: " + getAllApplicablePokemon().count(), guiLeft + 83, guiTop + 196, 0xFFFFFF);

        //Tooltips
        List<String> tooltip = new ArrayList<String>();
        for(int i = 0; i < pokemons.size(); i++) {
            Pokemon p = pokemons.get(i);
            if(p != null) {
                int x = guiLeft + POKEMON_START_X + (POKEMON_SIZE + 2) * (i % POKEMON_PER_ROW);
                int y = guiTop + POKEMON_START_Y + (POKEMON_SIZE + 2) * (i / POKEMON_PER_ROW);
                Rectangle rect = new Rectangle(x, y, POKEMON_SIZE, POKEMON_SIZE);
                if(rect.contains(mouseX, mouseY)) {
                    int firstRow = tooltip.size();
                    p.addTooltip(tooltip);
                    if(pokedex.hasCaught(p.id)) {
                        tooltip.set(firstRow, tooltip.get(firstRow) + " (Caught)");
                    }
                }
            }
        }

        drawHoveringText(tooltip, mouseX, mouseY);
    }

    private Stream<Pokemon> getAllApplicablePokemon(){
        Stream<Future<Pokemon>> pokemons;
        switch(showMode){
            case ALL:
                pokemons = Stream.concat(pokedex.getAllPokemon().stream().distinct(), pokedex.getUncaughtPokemon().stream());
                break;
            case CAUGHT:
                pokemons = pokedex.getAllPokemon().stream();
                break;
            case UNCAUGHT:
            default:
                pokemons = pokedex.getUncaughtPokemon().stream();
                break;
        }
        Stream<Pokemon> result = pokemons.map(x -> {
            try {
                return x.isDone() ? x.get() : null;
            } catch(InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return null;
            }
        });
        if(!visiblePokemonType.equals("All")) {
            result = result.filter(x -> x.stringTypes.contains(visiblePokemonType));
        }
        if(!searchWidget.getText().equals("")) {
            result = result.filter(x -> x != null && x.name.toLowerCase().contains(searchWidget.getText().toLowerCase()));
        }
        return result;
    }

    private List<Pokemon> getVisiblePokemon(){
        Stream<Pokemon> pokemons = getAllApplicablePokemon();
        if(sortMode == SortMode.ID) pokemons = pokemons.sorted((x, y) -> Integer.compare(x.id, y.id));
        if(sortMode == SortMode.NAME) pokemons = pokemons.sorted((x, y) -> x.name.compareTo(y.name));
        return pokemons.skip(scrollBar.getState() * POKEMON_PER_ROW).limit(POKEMON_PER_ROW * ROWS).collect(Collectors.toList());
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException{
        super.actionPerformed(button);
        switch(button.id){
        /* case 0:
             if(scrollRow > 0) scrollRow--;
             break;
         case 1:
             if(getAllApplicablePokemon().count() > POKEMON_PER_ROW * ROWS + scrollRow * POKEMON_PER_ROW) scrollRow++;
             break;*/
            case 2:
                showMode = ShowMode.values()[(showMode.ordinal() + 1) % ShowMode.values().length];
                button.displayString = showMode.name();
                scrollBar.setCurrentState(0);
                break;
            case 3:
                int index = availablePokemonTypes.indexOf(visiblePokemonType) + 1;
                if(index >= availablePokemonTypes.size()) {
                    visiblePokemonType = "All";
                } else {
                    visiblePokemonType = availablePokemonTypes.get(index);
                }
                button.displayString = visiblePokemonType;
                scrollBar.setCurrentState(0);
                break;
            case 4:
                sortMode = SortMode.values()[(sortMode.ordinal() + 1) % SortMode.values().length];
                button.displayString = sortMode.name();
                scrollBar.setCurrentState(0);
                break;
        }
    }

    @Override
    public boolean doesGuiPauseGame(){
        return false;
    }
}
