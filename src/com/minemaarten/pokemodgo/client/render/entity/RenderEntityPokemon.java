package com.minemaarten.pokemodgo.client.render.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

import com.minemaarten.pokemodgo.entity.EntityPokemon;
import com.minemaarten.pokemodgo.pokemon.Pokemon;

public class RenderEntityPokemon extends Render<EntityPokemon>{

    public RenderEntityPokemon(RenderManager renderManager){
        super(renderManager);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityPokemon entity){
        return null;
    }

    @Override
    protected boolean bindEntityTexture(EntityPokemon entity){
        Pokemon pokemon = entity.getPokemon();
        if(pokemon != null) {
            pokemon.bindTexture();
            return true;
        } else {
            Minecraft.getMinecraft().getTextureManager().bindTexture(Pokemon.MISSING_NO);
            return true;
        }
    }

    @Override
    protected boolean canRenderName(EntityPokemon entity){
        return true;
    }

    @Override
    public void doRender(EntityPokemon entity, double x, double y, double z, float entityYaw, float partialTicks){
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        bindEntityTexture(entity);

        GlStateManager.rotate(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((this.renderManager.options.thirdPersonView == 2 ? -1 : 1) * -this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);

        float scale = 3F;
        GlStateManager.scale(scale, scale, scale);
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
        vertexbuffer.pos(-0.5D, -0.25D, 0.0D).tex(0, 1).color(255, 255, 255, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
        vertexbuffer.pos(0.5D, -0.25D, 0.0D).tex(1, 1).color(255, 255, 255, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
        vertexbuffer.pos(0.5D, 0.75D, 0.0D).tex(1, 0).color(255, 255, 255, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
        vertexbuffer.pos(-0.5D, 0.75D, 0.0D).tex(0, 0).color(255, 255, 255, 128).normal(0.0F, 1.0F, 0.0F).endVertex();
        tessellator.draw();

        GlStateManager.popMatrix();
    }
}
