package com.minemaarten.pokemodgo.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import com.minemaarten.pokemodgo.PokeModGo;

public class EntityPokeball extends EntityThrowable{
    public EntityPokeball(World worldIn){
        super(worldIn);
    }

    public EntityPokeball(World worldIn, EntityLivingBase throwerIn){
        super(worldIn, throwerIn);
    }

    public EntityPokeball(World worldIn, double x, double y, double z){
        super(worldIn, x, y, z);
    }

    /**
     * Called when this EntityThrowable hits a block or entity.
     */
    @Override
    protected void onImpact(RayTraceResult result){
        if(result.entityHit != null) {
            if(result.entityHit instanceof EntityPokemon && getThrower() instanceof EntityPlayer) {
                if(!worldObj.isRemote) {
                    int pokemonId = ((EntityPokemon)result.entityHit).getPokemonId();
                    PokeModGo.instance.pokedexManager.addPokemon((EntityPlayer)getThrower(), pokemonId);
                    result.entityHit.setDead();
                }
            } else {
                result.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), 0);
            }
        }

        for(int j = 0; j < 8; ++j) {
            this.worldObj.spawnParticle(EnumParticleTypes.SNOWBALL, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D, new int[0]);
        }

        if(!this.worldObj.isRemote) {
            this.setDead();
        }
    }
}
