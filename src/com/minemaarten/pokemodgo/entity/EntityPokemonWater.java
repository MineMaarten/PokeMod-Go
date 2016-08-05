package com.minemaarten.pokemodgo.entity;

import net.minecraft.world.World;

public class EntityPokemonWater extends EntityPokemon{

    public EntityPokemonWater(World worldIn){
        super(worldIn);
    }

    @Override
    public boolean isNotColliding(){
        return this.worldObj.checkNoEntityCollision(this.getEntityBoundingBox(), this);
    }

    @Override
    public boolean canBreatheUnderwater(){
        return true;
    }

}
