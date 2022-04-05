package main.cli;

import main.CmdInterface;
import main.Config;
import main.SenderInfo;
import main.manager.StreamManager;
import riot.TopenRiot;
import riot.api.Summoner_V4;

public class summoner implements CmdInterface {
    @Override
    public void run(String cmd, String[] args, StreamManager streamManager, SenderInfo info) {

        for (String arg : args) {
            arg = arg.replaceAll("%", "%20");
        }
        final String key = Config.get("key");
        if(args.length == 0){
            streamManager.println("please write option and value ([account/name/puuid]/encryptedSummonerId [encryptedAccountId/summonerName/encryptedPuuid])");
            return;
        }
        switch (args[0]) {
            case "account" -> {
                streamManager.println(Summoner_V4.summonersByAccount(key, args[1], TopenRiot.ServerRegions.KR));
            }
            case "name" -> {
                streamManager.println(Summoner_V4.summonersByName(key, args[1], TopenRiot.ServerRegions.KR));
            }
            case "puuid" -> {
                streamManager.println(Summoner_V4.summonersByPuuid(key, args[1], TopenRiot.ServerRegions.KR));
            }
            default -> {
                streamManager.println(Summoner_V4.summoners(key, args[0], TopenRiot.ServerRegions.KR));
            }
        }
    }
}
