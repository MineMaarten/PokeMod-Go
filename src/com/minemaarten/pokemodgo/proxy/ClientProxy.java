package com.minemaarten.pokemodgo.proxy;

import net.minecraftforge.fml.client.registry.RenderingRegistry;

import com.minemaarten.pokemodgo.client.render.entity.RenderEntityPokemon;
import com.minemaarten.pokemodgo.entity.EntityPokemon;

public class ClientProxy extends CommonProxy{
    @Override
    public void preInit(){
        super.preInit();
        RenderingRegistry.registerEntityRenderingHandler(EntityPokemon.class, (manager) -> new RenderEntityPokemon(manager));
    }
}
