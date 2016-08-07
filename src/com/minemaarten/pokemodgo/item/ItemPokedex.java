package com.minemaarten.pokemodgo.item;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
            displayPokedexGUI();
        } else {
            NetworkHandler.sendTo(new PacketSyncPokedex(playerIn), (EntityPlayerMP)playerIn);
        }
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
    }

    @SideOnly(Side.CLIENT)
    private void displayPokedexGUI(){
        Minecraft.getMinecraft().displayGuiScreen(new GuiPokedex());
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced){
        super.addInformation(stack, playerIn, tooltip, advanced);
        tooltip.add(I18n.format("item.pokedex.tooltip"));
    }
}
