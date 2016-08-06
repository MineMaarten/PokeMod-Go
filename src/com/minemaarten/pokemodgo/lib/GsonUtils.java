package com.minemaarten.pokemodgo.lib;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class GsonUtils{
    public static <T> T readFromFile(Class<T> type, File file){
        if(file.exists()) {
            Gson gson = new Gson();

            FileReader reader = null;
            try {
                reader = new FileReader(file);
                return gson.fromJson(reader, type);
            } catch(JsonSyntaxException | JsonIOException | FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if(reader != null) reader.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static void writeToFile(Object o, File file){
        Gson gson = new Gson();
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            gson.toJson(o, writer);
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            if(writer != null) try {
                writer.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static <T> T readFromJson(Class<T> type, String json){
        return new Gson().fromJson(json, type);
    }

    public static String toJson(Object o){
        return new Gson().toJson(o);
    }
}
