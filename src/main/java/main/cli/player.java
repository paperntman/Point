package main.cli;

import main.*;
import main.manager.CmdManager;
import main.manager.CurrencyManager;
import main.manager.StreamManager;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class player implements CmdInterface {
    @Override
    public void run(String cmd, String[] args, StreamManager output, SenderInfo info) throws IOException {
        FileManager manager = new FileManager(new File("D:\\BigData\\UpdateList.json"));
        JSONArray array = new JSONArray(manager.read());
        switch (args[0]) {
            case "list" -> {
                for (int i = 0; i < array.length(); i++) {
                    final JSONObject jsonObject = array.getJSONObject(i);
                    output.println(jsonObject.toString());
                }
            }
            case "add" -> {
                JSONObject object = new JSONObject();
                final Stream<JSONObject> jsonObjectStream = IntStream.range(0, array.length()).mapToObj(array::getJSONObject);
                if (jsonObjectStream.anyMatch(jsonObject -> jsonObject.get("name").equals(args[1]))) {
                    output.println("that nickname already exists : " + args[1]);
                    return;
                }
                object.put("name", args[1]).put("lastUpdated", String.valueOf(LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond())).put("id", args[2]);
                array.put(object);
                output.println("successfully added player named " + args[1]);
                manager.write(array.toString(), true);
            }
            case "remove" -> {
                final Stream<JSONObject> jsonObjectStream = IntStream.range(0, array.length()).mapToObj(array::getJSONObject);
                if (jsonObjectStream.noneMatch(jsonObject -> jsonObject.get("name").equals(args[1]))) {
                    output.println("that nickname doesn't exists : " + args[1]);
                }
                final Iterator<Object> iterator = array.iterator();
                while (iterator.hasNext()) {
                    JSONObject object = (JSONObject) iterator.next();
                    if (object.get("name").equals(args[1])) {
                        output.println("successfully removed player named " + args[1]);
                        iterator.remove();
                    }
                }
                manager.write(array.toString(), true);
            }
            case "update" -> {
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    final outputTool outputTool = new outputTool();
                    CmdManager.run(String.format("check lol -showtotalpoints %s -time %s", object.getString("name"), object.getString("lastUpdated")), outputTool, Optional.empty(), new SenderInfo("command", null));
                    final String read = outputTool.read;
                    final int point = Integer.parseInt(read.split("\n")[0].split(" : ")[1]);
                    CurrencyManager currencyManager = new CurrencyManager(Config.get("dobak"), Main.jda);
                    currencyManager.add(object.getString("id"), point);
                    CmdManager.run("unix", outputTool, Optional.empty(), new SenderInfo("command", null));
                    array.getJSONObject(i).put("lastUpdated", outputTool.read.replaceAll("\n", ""));
                }
                output.println("successfully updated!");
                manager.write(array.toString(), true);
            }
        }
    }

    static class outputTool extends OutputStream{
        List<Byte> list = new ArrayList<>();
        String read = "";

        @Override
        public void write(int b) {
            if(b != 200) list.add((byte) b);
            Byte[] bytelist = new Byte[list.size()];
            bytelist = list.toArray(bytelist);
            byte[] bytes = new byte[bytelist.length];
            for (int i = 0; i < bytelist.length; i++) {
                bytes[i] = bytelist[i];
            }
            if (b == -200) {
                read = new String(bytes);
                read = read.substring(0, read.length()-1);
                list.clear();
            }
        }
    }
}
