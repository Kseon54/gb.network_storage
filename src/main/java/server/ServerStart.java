package server;

import java.io.IOException;

public class ServerStart {
    public static void main(String[] args) throws IOException {
        new  NioServer(8812);
    }
}
