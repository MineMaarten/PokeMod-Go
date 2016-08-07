package com.minemaarten.pokemodgo.entity;

import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.world.World;

public class EntityPokemonGround extends EntityPokemon{

    public EntityPokemonGround(World worldIn){
        super(worldIn);
    }

    @Override
    protected void initEntityAI(){
        super.initEntityAI();
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(11, new EntityAIWander(this, 0.8D));
    }

}
