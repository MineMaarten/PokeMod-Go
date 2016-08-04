package com.minemaarten.pokemodgo.pokemon;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.minemaarten.pokemodgo.lib.Constants;

public class Pokemon{
    public enum Type{
        GRASS
    }

    private static final String BASE_URL = "http://pokeapi.co/api/v2/pokemon/%s/";
    private static final String SPRITE_URL = "http://pokeapi.co/media/sprites/pokemon/%s.png";
    public final int id;
    public final String name;
    public final EnumSet<Type> types;
    public final Set<String> stringTypes = new HashSet<String>();
    public final String habitat;

    private Future<BufferedImage> texture;
    private int textureId = -1;
    private static final ExecutorService TEXTURE_GETTER = Executors.newSingleThreadExecutor();
    private static final ResourceLocation MISSING_TEXTURE = new ResourceLocation(Constants.MOD_ID, "misc/missing_texture.png");
    private static final ResourceLocation LOADING_TEXTURE = new ResourceLocation(Constants.MOD_ID, "misc/loading_texture.png");
    public static final ResourceLocation MISSING_NO = new ResourceLocation(Constants.MOD_ID, "misc/missing_no.png");

    protected Pokemon(int id) throws IOException{
        InputStream stream = getInputStream(String.format(BASE_URL, id));
        String jsonResponse = IOUtils.toString(stream);
        JsonObject root = new Gson().fromJson(jsonResponse, JsonObject.class);

        this.id = id;
        name = root.get("name").getAsString();
        types = EnumSet.noneOf(Type.class);
        for(JsonElement element : root.get("types").getAsJsonArray()) {
            String typeName = element.getAsJsonObject().get("type").getAsJsonObject().get("name").getAsString();
            stringTypes.add(typeName);
            try {
                Type type = Type.valueOf(typeName.toUpperCase());
                types.add(type);
            } catch(IllegalArgumentException e) {}
        }
        System.out.println("Loaded " + name + ":" + id + ", types: " + StringUtils.join(stringTypes, ","));
        habitat = "grassland";
    }

    private InputStream getInputStream(String url) throws IOException{
        URLConnection connection = new URL(url).openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        connection.connect();
        return connection.getInputStream();
    }

    @SideOnly(Side.CLIENT)
    public void bindTexture(){
        try {
            if(texture == null) {
                texture = TEXTURE_GETTER.submit(() -> {
                    try {
                        return ImageIO.read(getInputStream(String.format(SPRITE_URL, id)));
                    } catch(Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                });
            } else if(!texture.isDone()) {
                Minecraft.getMinecraft().getTextureManager().bindTexture(LOADING_TEXTURE);
            } else if(texture.get() == null) {
                Minecraft.getMinecraft().getTextureManager().bindTexture(MISSING_TEXTURE);
            } else {
                if(textureId == -1) textureId = new DynamicTexture(texture.get()).getGlTextureId();
                GlStateManager.bindTexture(textureId);
            }
        } catch(InterruptedException | ExecutionException e) { //thrown by Future#get(), Should not be possible as we wait on it via .isDone()
            e.printStackTrace();
        }
    }
}
