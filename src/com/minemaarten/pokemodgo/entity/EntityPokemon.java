package com.minemaarten.pokemodgo.entity;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import com.minemaarten.pokemodgo.PokeModGo;
import com.minemaarten.pokemodgo.persistency.PokemodWorldData;
import com.minemaarten.pokemodgo.pokemon.Pokemon;

public class EntityPokemon extends EntityCreature{
    private static final DataParameter<Integer> POKEMON_ID = EntityDataManager.<Integer> createKey(EntityPokemon.class, DataSerializers.VARINT);

    public EntityPokemon(World worldIn){
        super(worldIn);
    }

    @Override
    protected void entityInit(){
        super.entityInit();
        dataManager.register(POKEMON_ID, 1);
    }

    public EntityPokemon setPokemonId(int id){
        dataManager.set(POKEMON_ID, id);
        return this;
    }

    public int getPokemonId(){
        return dataManager.get(POKEMON_ID);
    }

    public Pokemon getPokemon(){
        return PokeModGo.instance.getPokemon(getPokemonId());
    }

    @Override
    public String getName(){
        if(this.hasCustomName()) {
            return this.getCustomNameTag();
        } else {
            Pokemon pokemon = getPokemon();
            return pokemon != null ? pokemon.name : "MissingNo.";
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag){
        super.writeEntityToNBT(tag);
        tag.setInteger("pokemonId", getPokemonId());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag){
        super.readEntityFromNBT(tag);
        setPokemonId(tag.getInteger("pokemonId"));
    }

    @Override
    public boolean getCanSpawnHere(){
        //  System.out.println("Trying to spawn pokemon..");
        //if(!super.getCanSpawnHere()) return false;
        if(worldObj.rand.nextInt(10) == 0 && worldObj instanceof WorldServer) {
            Pokemon pokemon = PokeModGo.instance.pokemonSpawnRules.getRandomPokemonFor(this instanceof EntityPokemonWater, (WorldServer)worldObj, getPosition());
            if(pokemon != null) {
                setPokemonId(pokemon.id);
                // System.out.println("Spawned " + pokemon.name + " at " + getPosition());
                return true;
            }
        }
        return false;

    }

    @Override
    protected boolean canDespawn(){
        return false;
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata){
        PokemodWorldData.getInstance().addRarePokemonSpawn(getPokemonId());
        return super.onInitialSpawn(difficulty, livingdata);
    }

    @Override
    public void onDeath(DamageSource cause){
        super.onDeath(cause);
        PokemodWorldData.getInstance().removeRarePokemonSpawn(getPokemonId());
    }
}
