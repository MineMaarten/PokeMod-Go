package com.minemaarten.pokemodgo.entity;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
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
        setSize(1.5F, 1.8F);
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

    //Copied from EntityMob.attackEntityAsMob
    @Override
    public boolean attackEntityAsMob(Entity entityIn){
        float f = (float)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        int i = 0;

        if(entityIn instanceof EntityLivingBase) {
            f += EnchantmentHelper.getModifierForCreature(this.getHeldItemMainhand(), ((EntityLivingBase)entityIn).getCreatureAttribute());
            i += EnchantmentHelper.getKnockbackModifier(this);
        }

        boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), f);

        if(flag) {
            if(i > 0 && entityIn instanceof EntityLivingBase) {
                ((EntityLivingBase)entityIn).knockBack(this, i * 0.5F, MathHelper.sin(this.rotationYaw * 0.017453292F), (-MathHelper.cos(this.rotationYaw * 0.017453292F)));
                this.motionX *= 0.6D;
                this.motionZ *= 0.6D;
            }

            int j = EnchantmentHelper.getFireAspectModifier(this);

            if(j > 0) {
                entityIn.setFire(j * 4);
            }

            if(entityIn instanceof EntityPlayer) {
                EntityPlayer entityplayer = (EntityPlayer)entityIn;
                ItemStack itemstack = this.getHeldItemMainhand();
                ItemStack itemstack1 = entityplayer.isHandActive() ? entityplayer.getActiveItemStack() : null;

                if(itemstack != null && itemstack1 != null && itemstack.getItem() instanceof ItemAxe && itemstack1.getItem() == Items.SHIELD) {
                    float f1 = 0.25F + EnchantmentHelper.getEfficiencyModifier(this) * 0.05F;

                    if(this.rand.nextFloat() < f1) {
                        entityplayer.getCooldownTracker().setCooldown(Items.SHIELD, 100);
                        this.worldObj.setEntityState(entityplayer, (byte)30);
                    }
                }
            }

            this.applyEnchantments(this, entityIn);
        }

        return flag;
    }

}
