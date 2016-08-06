package com.minemaarten.pokemodgo.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.commons.lang3.text.WordUtils;
import org.lwjgl.opengl.GL11;

import com.minemaarten.pokemodgo.client.gui.widget.IGuiWidget;
import com.minemaarten.pokemodgo.client.gui.widget.IWidgetListener;

@SideOnly(Side.CLIENT)
public class GuiBase extends GuiScreen implements IWidgetListener{

    protected final List<IGuiWidget> widgets = new ArrayList<IGuiWidget>();

    public GuiBase(){}

    protected void addWidget(IGuiWidget widget){
        widgets.add(widget);
        widget.setListener(this);
    }

    protected void addWidgets(Iterable<IGuiWidget> widgets){
        for(IGuiWidget widget : widgets) {
            addWidget(widget);
        }
    }

    protected void removeWidget(IGuiWidget widget){
        widgets.remove(widget);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks){
        super.drawScreen(mouseX, mouseY, partialTicks);

        GL11.glColor4d(1, 1, 1, 1);
        GL11.glDisable(GL11.GL_LIGHTING);
        for(IGuiWidget widget : widgets) {
            widget.render(mouseX, mouseY, partialTicks);
        }
        for(IGuiWidget widget : widgets) {
            widget.postRender(mouseX, mouseY, partialTicks);
        }

        List<String> tooltip = new ArrayList<String>();

        GL11.glColor4d(1, 1, 1, 1);
        GL11.glDisable(GL11.GL_LIGHTING);
        for(IGuiWidget widget : widgets) {
            if(widget.getBounds().contains(mouseX, mouseY)) widget.addTooltip(mouseX, mouseY, tooltip, false);
        }

        if(!tooltip.isEmpty()) {
            List<String> localizedTooltip = new ArrayList<String>();
            for(String line : tooltip) {
                String localizedLine = I18n.format(line);
                String[] lines = WordUtils.wrap(localizedLine, 50).split(System.getProperty("line.separator"));
                for(String locLine : lines) {
                    localizedTooltip.add(locLine);
                }
            }
            drawHoveringText(localizedTooltip, mouseX, mouseY, fontRendererObj);
        }
    }

    @Override
    public void updateScreen(){
        super.updateScreen();

        for(IGuiWidget widget : widgets)
            widget.update();
    }

    @Override
    protected void mouseClicked(int par1, int par2, int par3) throws IOException{
        super.mouseClicked(par1, par2, par3);
        for(IGuiWidget widget : widgets) {
            if(widget.getBounds().contains(par1, par2)) widget.onMouseClicked(par1, par2, par3);
            else widget.onMouseClickedOutsideBounds(par1, par2, par3);
        }
    }

    @Override
    public void actionPerformed(IGuiWidget widget){

    }

    @Override
    public void handleMouseInput() throws IOException{
        super.handleMouseInput();
        for(IGuiWidget widget : widgets) {
            widget.handleMouseInput();
        }
    }

    @Override
    protected void keyTyped(char key, int keyCode) throws IOException{
        for(IGuiWidget widget : widgets) {
            if(widget.onKey(key, keyCode)) return;
        }
        super.keyTyped(key, keyCode);
    }

    @Override
    public void setWorldAndResolution(Minecraft par1Minecraft, int par2, int par3){
        widgets.clear();
        super.setWorldAndResolution(par1Minecraft, par2, par3);
    }

    public void refreshScreen(){
        ScaledResolution scaledresolution = new ScaledResolution(mc);
        int i = scaledresolution.getScaledWidth();
        int j = scaledresolution.getScaledHeight();
        setWorldAndResolution(mc, i, j);
    }

    public void drawHoveringString(List<String> text, int x, int y, FontRenderer fontRenderer){
        drawHoveringText(text, x, y, fontRenderer);
    }

    @Override
    public void onKeyTyped(IGuiWidget widget){

    }
}
