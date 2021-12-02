package com.geekbrains.chat.server;

import jdk.nashorn.internal.ir.SplitReturn;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Handler implements Runnable {

    private static final int SIZE = 8192;

    private Path serverDir;
    private boolean running;
    private final byte[] buf;
    private final DataInputStream is;
    private final DataOutputStream os;
    private final Socket socket;

    public Handler(Socket socket) throws IOException {
        running = true;
        buf = new byte[8192];
        this.socket = socket;
        is = new DataInputStream(socket.getInputStream());
        os = new DataOutputStream(socket.getOutputStream());
        serverDir = Paths.get("src/main/java/com/geekbrains/chat/server/file");
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        try {
            while (running) {
                // вкрутить логику с получением файла от клиента
                String command = is.readUTF();
                if (command.equals("quit")) {
                    os.writeUTF("Client disconnected");
                    close();
                    break;
                } else if (command.equals("#file")) {
                    String fileName = is.readUTF();
                    long size = is.readLong();
                    try (FileOutputStream fileOutputStream =
                                 new FileOutputStream(serverDir.resolve(fileName).toFile())) {
                        os.writeUTF("File " + fileName + " was created");
                        for (int i = 0; i < (size + SIZE - 1) / SIZE; i++) {
                            int read = is.read(buf);
                            fileOutputStream.write(buf, 0, read);
                            os.writeUTF("Uploaded " + (i + 1) + " batch");
                        }
                    }
                    os.writeUTF("File successfully uploaded");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void close() throws IOException {
        os.close();
        is.close();
        socket.close();
    }
}
