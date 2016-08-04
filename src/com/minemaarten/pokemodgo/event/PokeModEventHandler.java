package com.minemaarten.pokemodgo.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraftforge.event.world.WorldEvent.PotentialSpawns;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.minemaarten.pokemodgo.PokeModGo;
import com.minemaarten.pokemodgo.entity.EntityPokemon;
import com.minemaarten.pokemodgo.pokemon.Pokemon;

public class PokeModEventHandler{

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRender(TickEvent.RenderTickEvent event){
        if(Minecraft.getMinecraft().thePlayer != null) {
            GlStateManager.enableTexture2D();
            GlStateManager.enableBlend();
            GlStateManager.disableAlpha();
            GlStateManager.color(1, 1, 1, 1);
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            for(int i = 1; i < 10; i++) {
                Pokemon p = PokeModGo.instance.getPokemon(i);
                if(p != null) {
                    p.bindTexture();
                    Gui.drawModalRectWithCustomSizedTexture(i * 32, 20, 0, 0, 32, 32, 32, 32);
                }
            }
            GlStateManager.disableTexture2D();
        }
    }

    private final SpawnListEntry POKEMON_SPAWN_ENTRY = new SpawnListEntry(EntityPokemon.class, 10, 1, 1);

    @SubscribeEvent
    public void onEntitySpawnListGathering(PotentialSpawns event){
        event.getList().add(POKEMON_SPAWN_ENTRY);
    }
}
