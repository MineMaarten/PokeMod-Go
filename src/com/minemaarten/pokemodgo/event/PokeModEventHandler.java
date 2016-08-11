package com.minemaarten.pokemodgo.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.Biome.SpawnListEntry;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.event.world.WorldEvent.PotentialSpawns;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.minemaarten.pokemodgo.entity.EntityPokemon;
import com.minemaarten.pokemodgo.entity.EntityPokemonFlying;
import com.minemaarten.pokemodgo.entity.EntityPokemonGround;
import com.minemaarten.pokemodgo.entity.EntityPokemonWater;
import com.minemaarten.pokemodgo.init.ModItems;
import com.minemaarten.pokemodgo.persistency.PokemodWorldData;
import com.minemaarten.pokemodgo.pokemon.Pokemon;

public class PokeModEventHandler{

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRender(TickEvent.RenderTickEvent event){
        /*if(Minecraft.getMinecraft().thePlayer != null) {
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
        }*/
    }

    private final SpawnListEntry POKEMON_SPAWN_GROUND_ENTRY = new SpawnListEntry(EntityPokemonGround.class, 1, 1, 1);
    private final SpawnListEntry POKEMON_SPAWN_FLY_ENTRY = new SpawnListEntry(EntityPokemonFlying.class, 1, 1, 1);
    private final SpawnListEntry POKEMON_SPAWN_WATER_ENTRY = new SpawnListEntry(EntityPokemonWater.class, 1, 1, 1);

    @SubscribeEvent
    public void onEntitySpawnListGathering(PotentialSpawns event){
        if(event.getType() == EnumCreatureType.CREATURE) {
            event.getList().add(POKEMON_SPAWN_GROUND_ENTRY);
            event.getList().add(POKEMON_SPAWN_WATER_ENTRY);
            event.getList().add(POKEMON_SPAWN_FLY_ENTRY);
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event){
        if(!event.getWorld().isRemote) {
            if(event.getWorld().provider.getDimension() == 0) {
                PokemodWorldData.overworld = event.getWorld();
                event.getWorld().loadItemData(PokemodWorldData.class, PokemodWorldData.DATA_KEY);
            }
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event){
        if(!event.getWorld().isRemote && event.getEntity() instanceof EntityPokemonGround) {
            EntityPokemonGround entity = (EntityPokemonGround)event.getEntity();
            Pokemon pokemon = entity.getPokemon();
            if(pokemon != null && pokemon.stringTypes.contains("fighting")) {
                entity.tasks.addTask(10, new EntityAIAttackMelee(entity, 1, false));
                entity.targetTasks.addTask(1, new EntityAINearestAttackableTarget<EntityPlayer>(entity, EntityPlayer.class, true));
                entity.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
                entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
            }
        }
    }

    @SubscribeEvent
    public void onEntityDrops(LivingDropsEvent event){
        EntityLivingBase entity = event.getEntityLiving();
        if(entity instanceof EntityPokemon) {
            if(entity.getRNG().nextInt(5) == 0) {
                EntityItem pokeballs = new EntityItem(entity.worldObj, entity.posX, entity.posY, entity.posZ, new ItemStack(ModItems.pokeball, entity.getRNG().nextInt(3) + 1));
                event.getDrops().add(pokeballs);
            }
        }
    }
}
