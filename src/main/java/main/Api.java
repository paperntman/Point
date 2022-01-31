package main;



import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static main.Point.*;


enum Point{
    NO_DEATH(20),
    DOUBLE(10),
    TRIPLE(30),
    QUADRA(50),
    PENTA(100),
    TEN_DEATH(10),
    TWENTY_DEATH(50);


    int i;
    Point(int i) {
        this.i = i;
    }

    public int getValue() {
        return i;
    }
}

public class Api {

    public static int main(String name) throws IOException, ParseException {
        if(name.equalsIgnoreCase("hide-on-bush")){
            return -1;
        }
        name = name.replaceAll("-", "%20");
        String user = getHttp("https://kr.api.riotgames.com/lol/summoner/v4/summoners/by-name/"+name+"?api_key=RGAPI-afafbb61-a4a7-401a-8d09-520b620aafbf");
        String puuid = (String) ((JSONObject) getJson(user)).get("puuid");
        System.out.println(puuid);
        String matches = getHttp("https://asia.api.riotgames.com/lol/match/v5/matches/by-puuid/"+puuid+"/ids?start=0&count=20&api_key=RGAPI-afafbb61-a4a7-401a-8d09-520b620aafbf");
        JSONArray array = (JSONArray) getJson(matches);
        ArrayList<String> list = new ArrayList<>();
        array.forEach(a -> {
            list.add(a.toString());
        });
        AtomicInteger fin = new AtomicInteger();
        for(String s : list){
            JSONObject match = (JSONObject) getJson(getHttp("https://asia.api.riotgames.com/lol/match/v5/matches/"+s+"?api_key=RGAPI-afafbb61-a4a7-401a-8d09-520b620aafbf"));

            String finalName = name.replaceAll("%20", " ").toLowerCase();
            ((JSONArray) ((JSONObject)
                    match.get("info"))
                    .get("participants"))
                    .forEach(object ->{
                        int point = 0;
                        if(((JSONObject) object).get("summonerName").toString().toLowerCase().equals(finalName)){
                            JSONObject jsonObject = (JSONObject) object;
                            Long deaths = (Long) jsonObject.get("deaths");
                            boolean win = (Boolean) jsonObject.get("win");
                            boolean doublekills = ((Long) jsonObject.get("doubleKills")) > 0;
                            boolean triplekills = ((Long) jsonObject.get("tripleKills")) > 0;
                            boolean quadrakills = ((Long) jsonObject.get("quadraKills")) > 0;
                            boolean pentakills = ((Long) jsonObject.get("pentaKills")) > 0;
                            if(deaths == 0 && win){
                                point += NO_DEATH.getValue();
                            }else if(deaths > 19){
                                point -= TWENTY_DEATH.getValue();
                            }else if(deaths > 9){
                                point -= TEN_DEATH.getValue();
                            }
                            if(pentakills){
                                point += PENTA.getValue();
                            } else if(quadrakills){
                                point += QUADRA.getValue();
                            } else if(triplekills){
                                point += TRIPLE.getValue();
                            } else if(doublekills){
                                point += DOUBLE.getValue();
                            }
                            fin.addAndGet(point);
                        }
                    });
        }
        return fin.get();
    }

    public static String getHttp(String s) throws IOException {
        URL url = new URL(s);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder builder = new StringBuilder();
        String input;
        while((input = in.readLine()) != null){
            builder.append(input);
        }
        return builder.toString();
    }

    public static Object getJson(String s) throws ParseException {
        JSONParser parser = new JSONParser();
        Object json = parser.parse(s);
        return json;
    }
}
