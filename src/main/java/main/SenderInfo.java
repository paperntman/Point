package main;

public class SenderInfo {
    String name;
    Object info;

    public SenderInfo(String name, Object info) {
        this.name = name;
        this.info = info;
    }

    public String getName() {
        return name;
    }

    public Object getInfo() {
        return info;
    }
}
