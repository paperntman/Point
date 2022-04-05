package main.manager;

import main.SenderInfo;

import java.io.OutputStream;
import java.util.Optional;
import java.util.Scanner;

public class CliManager implements Runnable{
    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        while(true){
            final String read = scanner.nextLine();
            if(read.equalsIgnoreCase("shutdown")) System.exit(0);
            if (!CmdManager.run(read, new OutputStream() {
                @Override
                public void write(int b) {
                    System.out.write(b);
                }
            }, Optional.empty(), new SenderInfo("cli", System.in))) {
                System.out.println("Error!");
            }
        }
    }
}
