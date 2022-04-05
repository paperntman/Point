package main.cli;

import main.CmdInterface;
import main.SenderInfo;
import main.manager.StreamManager;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class unix implements CmdInterface {
    @Override
    public void run(String cmd, String[] args, StreamManager stream, SenderInfo info) throws IOException {
        LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        stream.println(String.valueOf(localDateTime.atZone(ZoneId.systemDefault()).toEpochSecond()));
    }
}
