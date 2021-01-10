package com.example.trails.controller;

import android.content.Context;
import com.example.trails.model.Trail;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class LocalDB {

    private Context context;
    private Gson gson = new Gson();

    public LocalDB(Context context){
        this.context = context;
    }

    public String createJsonFile(Trail trail){
        String jsonTrail = gson.toJson(trail);
        return jsonTrail;
    }

    public void saveJsonFile(String trail, String trailName){
        try {
            String trialFile = trail;
            File file = new File(context.getFilesDir(), trailName);
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(trialFile);
            bufferedWriter.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    // Nome do ficheiro tem que começar por "trail_"
    public void storeTrail(Trail trail, String trailName){
        saveJsonFile(createJsonFile(trail), trailName);
    }

    public String readTrail(String nameTrail) {
        try {
            FileInputStream fis = context.openFileInput(nameTrail);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (FileNotFoundException e) {
            return "";
        } catch (UnsupportedEncodingException e) {
            return "";
        } catch (IOException e) {
            return "";
        }
    }

    public Trail parseTrail(String response) {
        try {
            Trail trail = gson.fromJson(response, Trail.class);
            return trail;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public Trail getTrail(String trailName){
        return parseTrail(readTrail(trailName));
    }

    public void deleteTrial(String trailName){
        File file = new File(context.getFilesDir(), trailName);
        file.delete();
    }

    public String getTrailsNameFromAssets() {
        String[] files = context.fileList();
        ArrayList<String> filesNames = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            if(files[i].startsWith("trail_")){
                filesNames.add(files[i]);
            }
        }
        return filesNames.toString();
    }
}
