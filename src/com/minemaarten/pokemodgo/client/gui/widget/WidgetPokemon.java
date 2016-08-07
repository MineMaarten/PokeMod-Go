package com.minemaarten.pokemodgo.client.gui.widget;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;

import com.minemaarten.pokemodgo.network.NetworkHandler;
import com.minemaarten.pokemodgo.network.PacketSpawnPokemon;
import com.minemaarten.pokemodgo.pokedex.Pokedex;
import com.minemaarten.pokemodgo.pokemon.Pokemon;

public class WidgetPokemon extends WidgetBase{

    private Pokedex pokedex;
    private Pokemon pokemon;

    public WidgetPokemon(Pokedex pokedex, int x, int y){
        super(0, x, y, 32, 32);
        this.pokedex = pokedex;
    }

    public void setPokemon(Pokemon pokemon){
        this.pokemon = pokemon;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTick){
        super.render(mouseX, mouseY, partialTick);
        if(pokemon != null) {
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            pokemon.bindTexture();
            if(pokedex.hasCaught(pokemon.id)) {
                GlStateManager.color(1, 1, 1, 1);
            } else {
                GlStateManager.color(1, 1, 1, 0.5F);
            }
            Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, 32, 32, 32, 32);
        }
        GlStateManager.disableBlend();
    }

    @Override
    public void addTooltip(int mouseX, int mouseY, List<String> tooltip, boolean shiftPressed){
        super.addTooltip(mouseX, mouseY, tooltip, shiftPressed);
        if(pokemon != null) {
            int firstRow = tooltip.size();
            pokemon.addTooltip(tooltip);
            if(pokedex.hasCaught(pokemon.id)) {
                tooltip.set(firstRow, tooltip.get(firstRow) + " (Caught)");
            }
            if(Minecraft.getMinecraft().thePlayer.isCreative()) {
                tooltip.add(TextFormatting.GREEN + "Left-click to spawn (Creative-only)");
            }
        }
    }

    @Override
    public void onMouseClicked(int mouseX, int mouseY, int button){
        super.onMouseClicked(mouseX, mouseY, button);
        if(button == 0 && pokemon != null && Minecraft.getMinecraft().thePlayer.isCreative()) {
            NetworkHandler.sendToServer(new PacketSpawnPokemon(pokemon.id));
        }
    }
}
