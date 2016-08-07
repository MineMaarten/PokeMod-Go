package com.minemaarten.pokemodgo.pokemon;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.minemaarten.pokemodgo.lib.Constants;

public class Pokemon{

    private transient static final String BASE_URL = "http://pokeapi.co/api/v2/pokemon/%s/";
    private transient static final String SPECIES_URL = "http://pokeapi.co/api/v2/pokemon-species/%s/";
    private transient static final String SPRITE_URL = "http://pokeapi.co/media/sprites/pokemon/%s.png";
    private transient static final ExecutorService TEXTURE_GETTER = Executors.newSingleThreadExecutor();
    private transient static final ResourceLocation MISSING_TEXTURE = new ResourceLocation(Constants.MOD_ID, "misc/missing_texture.png");
    private transient static final ResourceLocation LOADING_TEXTURE = new ResourceLocation(Constants.MOD_ID, "misc/loading_texture.png");
    public transient static final ResourceLocation MISSING_NO = new ResourceLocation(Constants.MOD_ID, "misc/missing_no.png");

    private transient Future<BufferedImage> texture;
    private transient int textureId = -1;

    public final int id;
    public final String name;
    public final Set<String> stringTypes = new HashSet<String>();
    public final String habitat;
    public final String description;

    /**
     * Gson constructor
     */
    protected Pokemon(){
        id = 0;
        name = "";
        habitat = null;
        description = "";
    }

    protected Pokemon(int id) throws IOException{
        InputStream stream = null;
        String tempName = "MissingNo.";
        try {
            stream = getInputStream(String.format(BASE_URL, id));
            String jsonResponse = IOUtils.toString(stream);
            JsonObject root = new Gson().fromJson(jsonResponse, JsonObject.class);

            this.id = id;
            tempName = root.get("name").getAsString();

            for(JsonElement element : root.get("types").getAsJsonArray()) {
                String typeName = element.getAsJsonObject().get("type").getAsJsonObject().get("name").getAsString();
                stringTypes.add(typeName);
            }
        } finally {
            if(stream != null) stream.close();
        }
        try {
            stream = getInputStream(String.format(SPECIES_URL, id));
            String jsonResponse = IOUtils.toString(stream);
            JsonObject root = new Gson().fromJson(jsonResponse, JsonObject.class);

            habitat = root.get("habitat").getAsJsonObject().get("name").getAsString();

            for(JsonElement element : root.get("names").getAsJsonArray()) {
                JsonObject o = element.getAsJsonObject();
                if(o.get("language").getAsJsonObject().get("name").getAsString().equals("en")) {
                    tempName = o.get("name").getAsString();
                }
            }
            name = tempName;

            String tempDescription = "";
            for(JsonElement element : root.get("flavor_text_entries").getAsJsonArray()) {
                JsonObject o = element.getAsJsonObject();
                if(o.get("language").getAsJsonObject().get("name").getAsString().equals("en")) {
                    tempDescription = o.get("flavor_text").getAsString();
                }
            }

            description = tempDescription;
        } finally {
            if(stream != null) stream.close();
        }

        //System.out.println("Loaded " + name + ":" + id + ", types: " + StringUtils.join(stringTypes, ","));

    }

    private InputStream getInputStream(String url) throws IOException{
        URLConnection connection = new URL(url).openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        connection.connect();
        return connection.getInputStream();
    }

    public void addTooltip(List<String> tooltip){
        tooltip.add(TextFormatting.GOLD + "#" + id + " " + name);
        tooltip.addAll(Arrays.asList(description.split("\\f|\\n")).stream().map(x -> TextFormatting.BLUE + x).collect(Collectors.toList()));
        tooltip.add(TextFormatting.DARK_PURPLE + "Type: " + StringUtils.join(stringTypes, ", "));
        tooltip.add(TextFormatting.DARK_GREEN + "Habitat: " + habitat);
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
