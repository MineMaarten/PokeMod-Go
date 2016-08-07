package com.minemaarten.pokemodgo.entity;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityPokemonWater extends EntityPokemon{

    private float randomMotionVecX;
    private float randomMotionVecY;
    private float randomMotionVecZ;

    public EntityPokemonWater(World worldIn){
        super(worldIn);
    }

    @Override
    protected void initEntityAI(){
        this.tasks.addTask(0, new EntityPokemonWater.AIMoveRandom(this));
    }

    @Override
    public boolean isNotColliding(){
        return this.worldObj.checkNoEntityCollision(this.getEntityBoundingBox(), this);
    }

    @Override
    public boolean canBreatheUnderwater(){
        return true;
    }

    @Override
    public void onLivingUpdate(){
        super.onLivingUpdate();
        if(this.inWater) {
            if(!this.worldObj.isRemote) {
                this.motionX = (this.randomMotionVecX);
                this.motionY = (this.randomMotionVecY);
                this.motionZ = (this.randomMotionVecZ);
            }
        }
    }

    public void setMovementVector(float randomMotionVecXIn, float randomMotionVecYIn, float randomMotionVecZIn){
        this.randomMotionVecX = randomMotionVecXIn;
        this.randomMotionVecY = randomMotionVecYIn;
        this.randomMotionVecZ = randomMotionVecZIn;
    }

    public boolean hasMovementVector(){
        return this.randomMotionVecX != 0.0F || this.randomMotionVecY != 0.0F || this.randomMotionVecZ != 0.0F;
    }

    static class AIMoveRandom extends EntityAIBase{
        private final EntityPokemonWater pokemon;

        public AIMoveRandom(EntityPokemonWater p_i45859_1_){
            this.pokemon = p_i45859_1_;
        }

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
        @Override
        public boolean shouldExecute(){
            return true;
        }

        /**
         * Updates the task
         */
        @Override
        public void updateTask(){
            int i = this.pokemon.getAge();

            if(i > 100) {
                this.pokemon.setMovementVector(0.0F, 0.0F, 0.0F);
            } else if(this.pokemon.getRNG().nextInt(50) == 0 || !this.pokemon.inWater || !this.pokemon.hasMovementVector()) {
                float f = this.pokemon.getRNG().nextFloat() * ((float)Math.PI * 2F);
                float f1 = MathHelper.cos(f) * 0.2F;
                float f2 = -0.1F + this.pokemon.getRNG().nextFloat() * 0.2F;
                float f3 = MathHelper.sin(f) * 0.2F;
                this.pokemon.setMovementVector(f1, f2, f3);
            }
        }
    }
}
