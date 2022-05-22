package com.tinkoffinvest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class App {
    public static void main(String[] args) throws FileNotFoundException, IOException {
        Gson gsoon = new GsonBuilder().create();
        File file = new File("source.json");
        String strData = readfile(file);
        InfoJsonFile info = gsoon.fromJson(strData, InfoJsonFile.class);
        System.out.println(info);
    }

    public static String readfile(File file) throws FileNotFoundException, IOException {
        StringBuilder strData = new StringBuilder();
        String line;
        if (!file.exists()) {
            throw new FileNotFoundException();
        } else {
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                while ((line = bufferedReader.readLine()) != null) {
                    strData.append(line);
                }
            }
        }
        return strData.toString();
    }
}
