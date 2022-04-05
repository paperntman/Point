package main.cli;

import main.CmdInterface;
import main.PointCalc;
import main.SenderInfo;
import main.manager.StreamManager;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class check implements CmdInterface {
    @Override
    public void run(String cmd, String[] args, StreamManager streamManager, SenderInfo info) {
        if(args.length == 0){
            streamManager.println("please write lol or tft");
            return;
        }
        if(!Objects.equals(args[0], "tft") && !Objects.equals(args[0], "lol")) {
            streamManager.println("please write lol or tft");
            return;
        }
        //options check
        ArrayList<String> contains = new ArrayList<>(); contains.add("");
        boolean showFilename = false;
        boolean showFileContents = false;
        boolean showTotalPoint = false;
        boolean checkEpoch = false;
        long epoch = 0;
        int argChecked = 1;
        String checkPlayer = "";
        while(args.length > argChecked){
            switch (args[argChecked].replaceAll("/", " ")) {
                case "-contains" -> {
                    contains.add(args[argChecked + 1].replaceAll("/", " "));
                    argChecked += 2;
                }
                case "-showfilename" -> {
                    showFilename = true;
                    argChecked++;
                }
                case "-showfilecontents" -> {
                    showFileContents = true;
                    argChecked++;
                }
                case "-showtotalpoints" -> {
                    showTotalPoint = true;
                    checkPlayer = args[argChecked + 1].replaceAll("/", " ");
                    contains.add(args[argChecked + 1].replaceAll("/", " "));
                    argChecked += 2;
                }
                case "-time" -> {
                    checkEpoch = true;
                    epoch = Long.parseLong(args[argChecked + 1]);
                    argChecked += 2;
                }
                default -> {
                    streamManager.println("invalid arg!");
                    return;
                }
            }
        }
        //read files & filter
        final File path = new File("D:\\BigData\\" + (args[0].equals("lol") ? "League of Legends" : "Teamfight Tactics"));
        List<File> list = new ArrayList<>();
        for (File file1 : Objects.requireNonNull(path.listFiles())) {
            boolean isGood;
            if (file1.getName().startsWith("KR_")) {
                isGood = contains.stream().allMatch(s -> read(file1).contains(s));
            }else continue;
            if(!isGood) continue;
            if(checkEpoch){
                JSONObject object = new JSONObject(read(file1));
                if(object.isEmpty()) continue;
                final long gameEpoch = Long.parseLong((String.valueOf(object.getJSONObject("info").getLong("gameStartTimestamp")).substring(0, 10)));
                isGood = (epoch < gameEpoch);
            }
            if(isGood) list.add(file1);
        }
        int totalPoint = 0;
        for (File file : list) {
            final String read = read(file);
            if(showFilename) streamManager.print(file.getName()+" ");
            if(showFilename && showFileContents) streamManager.print(": ");
            if(showFileContents) streamManager.print(read);
            if(showFileContents || showFilename)streamManager.println();
            if(showTotalPoint) totalPoint+=PointCalc.main(read, checkPlayer);
        }
        if(showTotalPoint) streamManager.println(checkPlayer+"'s total point : "+totalPoint);
        streamManager.printf("%d개의 파일을 불러왔습니다.\n", list.size());
    }

    String read(File f){
        try {
            Scanner scanner = new Scanner(f);
            StringBuilder ret = new StringBuilder();
            while (scanner.hasNext()) {
                ret.append(scanner.next()).append(" ");
            }
            return ret.toString().trim();
        } catch (IOException e) {
            return "";
        }
    }
}
