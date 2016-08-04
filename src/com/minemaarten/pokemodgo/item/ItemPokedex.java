package com.minemaarten.pokemodgo.item;

import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import com.minemaarten.pokemodgo.client.gui.GuiPokedex;
import com.minemaarten.pokemodgo.network.NetworkHandler;
import com.minemaarten.pokemodgo.network.PacketSyncPokedex;

public class ItemPokedex extends Item{
    public ItemPokedex(){
        setCreativeTab(CreativeTabs.MISC);
        setRegistryName("pokedex");
        setUnlocalizedName("pokedex");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand){
        if(worldIn.isRemote) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiPokedex());
        } else {
            NetworkHandler.sendTo(new PacketSyncPokedex(playerIn), (EntityPlayerMP)playerIn);
        }
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
    }
}
