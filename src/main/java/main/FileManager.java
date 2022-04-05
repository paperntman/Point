package main;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;

public class FileManager{
    @NotNull File file;

    public FileManager(@NotNull File file) {
        this.file = file;
    }

    public String read(){
        try {
            FileReader reader = new FileReader(file);
            StringBuilder stringBuilder = new StringBuilder();
            int ch;
            while ((ch = reader.read()) != -1) {
                stringBuilder.append((char) ch);
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }



    public void write(String s, boolean reset){
        try {
            if(reset)
                Files.write(file.toPath(), s.getBytes());
            else
                Files.write(file.toPath(), (read()+s).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public @NotNull File getFile() {
        return file;
    }
}
