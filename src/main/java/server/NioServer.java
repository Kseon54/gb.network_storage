package server;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

public class NioServer {

    private ServerSocketChannel serverChannel;
    private Selector selector;

    private ByteBuffer buf;
    private Path currentDir;

    public NioServer(int port) throws IOException {
        currentDir = Paths.get("./");
        buf = ByteBuffer.allocate(10);
        serverChannel = ServerSocketChannel.open();
        selector = Selector.open();
        serverChannel.bind(new InetSocketAddress(port));
        serverChannel.configureBlocking(false);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (serverChannel.isOpen()) {
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = keys.iterator();
            try {
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isAcceptable()) {
                        handleAccept();
                    }
                    if (key.isReadable()) {
                        handleRead(key);
                    }
                    iterator.remove();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleRead(SelectionKey key) throws Exception {
        SocketChannel channel = (SocketChannel) key.channel();
        StringBuilder msg = new StringBuilder();
        while (true) {
            int read = channel.read(buf);
            if (read == -1) {
                channel.close();
                return;
            }
            if (read == 0) {
                break;
            }
            buf.flip();
            while (buf.hasRemaining()) {
                msg.append((char) buf.get());
            }
            buf.clear();
        }
        processMessage(channel, msg.toString().trim());
    }

    private void processMessage(SocketChannel channel, String msg) throws IOException {
        String[] tokens = msg.split(" +");
        TerminalCommandType type;
        try {
            type = TerminalCommandType.byCommand(tokens[0]);
            switch (type) {
                case LS:
                    sendString(channel, getFilesList());
                    break;
                case CAT:
                    processCatCommand(channel, tokens);
                    break;
                case CD:
                    processCdCommand(channel, tokens);
                    break;
                case MKDIR:
                    processMkdirCommand(channel, tokens);
                    break;
                case TOUCH:
                    processTouchCommand(channel, tokens);
            }
        } catch (RuntimeException e) {
            String response = "Command " + tokens[0] + " is not exists!";
            sendString(channel, response);
        }
        channel.write(ByteBuffer.wrap("\n\rMike -> ".getBytes(StandardCharsets.UTF_8)));

    }

    private void processTouchCommand(SocketChannel channel, String[] tokens) throws IOException {
        if (tokens == null || tokens.length != 2) {
            sendString(channel, "Command touch should have 2 args");
        } else {
            String file = tokens[1];
            if (Files.isDirectory(currentDir)) {
                currentDir = currentDir.resolve(file);
                Files.createFile(currentDir);
                currentDir = currentDir.getParent();
            } else {
                sendString(channel, "You cannot use touch command to FILE");
            }
        }
    }

    private void processMkdirCommand(SocketChannel channel, String[] tokens) throws IOException {
        if (tokens == null || tokens.length != 2) {
            sendString(channel, "Command mkdir should have 2 args");
        } else {
            String dir = tokens[1];
            if (Files.isDirectory(currentDir)) {
                currentDir = currentDir.resolve(dir);
                Files.createDirectory(currentDir);
                currentDir = currentDir.getParent();
            } else {
                sendString(channel, "You cannot use mkdir command to FILE");
            }
        }
    }


    private void processCdCommand(SocketChannel channel, String[] tokens) throws IOException {
        if (tokens == null || tokens.length != 2) {
            sendString(channel, "Command cat should have 2 args");
        } else {
            String dir = tokens[1];
            if (Files.isDirectory(currentDir.resolve(dir))) {
                currentDir = currentDir.resolve(dir);
            } else {
                sendString(channel, "You cannot use cd command to FILE");
            }
        }
    }

    private void processCatCommand(SocketChannel channel, String[] tokens) throws IOException {
        if (tokens == null || tokens.length != 2) {
            sendString(channel, "Command cat should have 2 args");
        } else {
            String fileName = tokens[1];
            Path file = currentDir.resolve(fileName);
            if (!Files.isDirectory(file)) {
                String content = new String(Files.readAllBytes(file)) + "\n\r";
                sendString(channel, content);
            } else {
                sendString(channel, "You cannot use cat command to DIR");
            }
        }
    }

    private String getFilesList() throws IOException {
        return Files.list(currentDir)
                .map(p -> p.getFileName().toString() + " " + getFileSuffix(p))
                .collect(Collectors.joining("\n")) + "\n\r";
    }

    private String getFileSuffix(Path path) {
        if (Files.isDirectory(path)) {
            return "[DIR]";
        } else {
            return "[FILE] " + path.toFile().length() + " bytes";
        }
    }

    private void sendString(SocketChannel channel, String msg) throws IOException {
        msg = "\n\r" + msg;
        channel.write(ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8)));
    }

    private void handleAccept() throws IOException {
        System.out.println("Client accepted...");
        SocketChannel socketChannel = serverChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        socketChannel.write(ByteBuffer.wrap((
                "Mike -> "
        ).getBytes(StandardCharsets.UTF_8)));
    }
}
