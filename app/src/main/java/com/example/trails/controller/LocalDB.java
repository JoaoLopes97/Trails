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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class LocalDB {

    private static Gson gson = new Gson();

    public static String createJsonFile(Trail trail) {
        String jsonTrail = gson.toJson(trail);
        return jsonTrail;
    }

    public static void saveJsonFile(Context context, String trail, String trailId) {
        try {
            String trialFile = trail;

            File file = new File(context.getFilesDir(), "trail_" + trailId);
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(trialFile);
            bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Nome do ficheiro tem que começar por "trail_"
    public static void storeTrail(Context context, Trail trail, String trailId) {
        saveJsonFile(context, createJsonFile(trail), trailId);
    }

    public static String readTrail(Context context, String trailName) {
        try {
            FileInputStream fis = context.openFileInput(trailName);
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

    public static Trail parseTrail(String response) {
        try {
            Trail trail = gson.fromJson(response, Trail.class);
            return trail;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Trail getTrail(Context context, String trailName) {
        return parseTrail(readTrail(context, trailName));
    }

    public static void deleteTrial(Context context, String trailName) {
        File file = new File(context.getFilesDir(), trailName);
        file.delete();
    }

    public static ArrayList<String> getTrailsNameFromAssets(Context context) {
        String[] files = context.fileList();
        ArrayList<String> filesNames = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            if (files[i].startsWith("trail_")) {
                filesNames.add(files[i]);
            }
        }
        return filesNames;
    }
}
