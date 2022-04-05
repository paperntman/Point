package main.manager;

import java.io.IOException;
import java.io.OutputStream;

public class StreamManager{
    OutputStream stream;

    public StreamManager(OutputStream stream) {
        this.stream = stream;
    }

    public void print(String s){
        try {
            stream.write(s.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void println(String s){
        try {
            stream.write((s+"\n").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void println(){
        try {
            stream.write(("\n").getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void printf(String s, Object... format){
        try {
            stream.write(String.format(s, format).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        try {
            stream.write(-200);
            stream.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
