package main;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.*;

import static main.Main.mainGuild;

public class CurrencyManager {
    String Gid;
    static JDA jda;
    public CurrencyManager(String guildID, JDA jda){
        Gid = guildID;
        Main.setupVars(bet.jda);
        this.jda = jda;
    }

    public void setId(String id) {
        this.Gid = id;
    }

    public String getId() {
        return Gid;
    }

    public void add(Member m, long amount){
        String id = m.getId();
        TextChannel tc = create(Gid);
        Map<String, Long> map = maplize(read(tc));
        if(!map.containsKey(id)) map.put(id, 0L);
        long originalPoint = map.get(id);
        map.replace(id, originalPoint+amount);
        update(m.getGuild().getId(), map);
    }

    public Long get(Member m){
        String id = m.getId();
        TextChannel tc = create(Gid);
        Map<String, Long> map = maplize(read(tc));
        if(!map.containsKey(id)) map.put(id, 0L);
        return map.get(id);
    }

    public void set(Member m, long amount){
        String id = m.getId();
        TextChannel tc = create(Gid);
        Map<String, Long> map = maplize(read(tc));
        if(!map.containsKey(id)) map.put(id, 0L);
        map.replace(id, amount);
        update(m.getGuild().getId(), map);
    }
    public static TextChannel create(String id){
        if(mainGuild.getTextChannelsByName(id, true).size() == 0){
            System.out.println("new channel created. id : "+id+" , name : "+ Objects.requireNonNull(jda.getGuildById(id)).getName());
            mainGuild.createTextChannel(id).complete();
        }
        return mainGuild.getTextChannelsByName(id, true).get(0);
    }
    public static List<String> slicer(String text, int maxLength){
        List<String> list = new ArrayList<>();


        int textLen = text.length();
        int loopCnt = textLen / maxLength + 1;

        String rssTitles;
        for (int i = 0; i < loopCnt; i++) {

            int lastIndex = (i + 1) * maxLength;
            if(textLen > lastIndex){
                rssTitles = text.substring(i * maxLength, lastIndex);
            }else{
                rssTitles = text.substring(i * maxLength);
            }

            if(!rssTitles.isEmpty())
                list.add(rssTitles);
        }

        return list;
    }
    public static String read(TextChannel c){
        StringBuilder sb = new StringBuilder();
        List<Message> list = c.getHistory().retrievePast(100).complete();
        Collections.reverse(list);
        for(Message m : list){
            sb.append(m.getContentRaw());
        }

        return sb.toString();
    }
    public static Map<String, Long> maplize(String s){
        Map<String, Long> hashMap = new HashMap<>();
        String[] split = s.split(" ");
        for(int i = 0; i < split.length-1; i+=2){
            hashMap.put(split[i], Long.parseLong(split[i+1]));
        }
        return hashMap;
    }
    public static String stringlize(Map<String, Long> map){
        StringBuilder sb = new StringBuilder();
        for(String key : map.keySet()){
            sb.append(key);
            sb.append(" ");
            sb.append(map.get(key));
            sb.append(" ");
        }
        return  sb.toString().trim();
    }
    public static void update(String id, Map<String, Long> contents){
        TextChannel c = mainGuild.getTextChannelsByName(id, true).get(0);
        for(Message m : c.getHistory().retrievePast(100).complete()) m.delete().complete();
        for(String s : slicer(stringlize(contents), 2000)){
            c.sendMessage(s).complete();
        }
        c.getManager().setTopic(Objects.requireNonNull(jda.getGuildById(c.getName())).getName()).complete();
    }
}
