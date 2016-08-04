package com.minemaarten.pokemodgo.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import com.minemaarten.pokemodgo.client.render.entity.RenderEntityPokemon;
import com.minemaarten.pokemodgo.entity.EntityPokeball;
import com.minemaarten.pokemodgo.entity.EntityPokemon;
import com.minemaarten.pokemodgo.init.ModItems;
import com.minemaarten.pokemodgo.lib.Constants;

public class ClientProxy extends CommonProxy{
    @Override
    public void preInit(){
        super.preInit();
        RenderingRegistry.registerEntityRenderingHandler(EntityPokemon.class, (manager) -> new RenderEntityPokemon(manager));
        RenderingRegistry.registerEntityRenderingHandler(EntityPokeball.class, (manager) -> new RenderSnowball<EntityPokeball>(manager, ModItems.pokeball, Minecraft.getMinecraft().getRenderItem()));

    }

    @Override
    public void init(){
        super.init();
        ResourceLocation resLoc = new ResourceLocation(Constants.MOD_ID, "pokeball");
        ModelBakery.registerItemVariants(ModItems.pokeball, resLoc);
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(ModItems.pokeball, 0, new ModelResourceLocation(resLoc, "inventory"));

        resLoc = new ResourceLocation(Constants.MOD_ID, "pokedex");
        ModelBakery.registerItemVariants(ModItems.pokedex, resLoc);
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(ModItems.pokedex, 0, new ModelResourceLocation(resLoc, "inventory"));

    }

    @Override
    public void addScheduledTask(Runnable runnable, boolean serverSide){
        if(serverSide) {
            super.addScheduledTask(runnable, serverSide);
        } else {
            Minecraft.getMinecraft().addScheduledTask(runnable);
        }
    }

    @Override
    public EntityPlayer getPlayer(){
        return Minecraft.getMinecraft().thePlayer;
    }
}
