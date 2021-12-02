package com.geekbrains.chat.client;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class IoNet implements Closeable {

    private final Callback callback;
    private final Socket socket;
    private final DataInputStream is;
    private final DataOutputStream os;
    private final byte[] buf;

    public IoNet(Callback callback,
                 Socket socket) throws IOException {
        this.callback = callback;
        this.socket = socket;
        is = new DataInputStream(socket.getInputStream());
        os = new DataOutputStream(socket.getOutputStream());
        buf = new byte[8192];
        Thread readThread = new Thread(this::readMessages);
        readThread.setDaemon(true);
        readThread.start();
    }

    public void sendMsg(String msg) throws IOException {
        os.write(msg.getBytes(StandardCharsets.UTF_8));
        os.flush();
    }

    public void writeUtf(String msg) throws IOException {
        os.writeUTF(msg);
        os.flush();
    }

    public void writeLong(long msg) throws IOException {
        os.writeLong(msg);
        os.flush();
    }

    public void writeByte(byte[] msg,int off,int cnt) throws IOException {
        os.write(msg,off,cnt);
        os.flush();
    }

    private void readMessages() {
        try {
            while (true) {
                String msg = is.readUTF();
                callback.onReceive(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
        os.close();
        is.close();
        socket.close();
    }
}
