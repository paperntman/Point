package main;

import io.github.cdimascio.dotenv.Dotenv;

public class Config {
    static Dotenv dotenv = Dotenv.load();
    public static String get(String key){
        return dotenv.get(key.toUpperCase());
    }
}
