package main.server.comands;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import main.server.db.User;
import main.server.messages.*;
import main.server.messages.helpMessage.HelpFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;

@Slf4j
public final class Command {

    private static final String PART_TO_FILES = "src/resources/file";

    public static void sendMessage(ChannelHandlerContext ctx, IMessage abstractMessage) {
        log.debug("Received: {}", abstractMessage);
        ctx.writeAndFlush(abstractMessage);
    }

    public static void authCommand(ChannelHandlerContext ctx, String[] tokens) {
        if (tokens.length != 3) {
            sendError(ctx, "Invalid username or password");
            return;
        }
        Optional<User> user = User.findUserByLoginAndPassword(tokens[1], tokens[2]);
        if (user.isPresent()) {
            Path path = Paths.get(PART_TO_FILES + File.separator + user.get().getId());

            if (!Files.exists(path)) {
                try {
                    Files.createDirectory(path);
                } catch (IOException e) {
                    log.error("e = ", e);
                }
            }

            AuthMessage authMessage = new AuthMessage(user.get(), path);
            sendMessage(ctx, authMessage);
        } else {
            sendError(ctx, "Invalid username or password");
        }
    }

    public static void mkDigCommand(ChannelHandlerContext ctx, Path path, String name) {
        Path newDir = getVerifiedFileName(path, name);

        try {
            Files.createDirectory(newDir);
        } catch (IOException e) {
            log.error("e = ", e);
            sendError(ctx, "Error. Не удалось создать папу");
        }
    }

    private static Path getVerifiedFileName(Path path, String fileName) {
        String extension = "";
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            extension = fileName.substring(fileName.lastIndexOf("."));

        Path newName = Paths.get(path + File.separator + fileName);
        String way = newName.toString().substring(0, newName.toString().length() - extension.length());

        if (Files.exists(newName)) {
            for (int i = 1; Files.exists(newName); i++) {
                newName = Paths.get(way + String.format("(%d)%s", i, extension));
            }
        }
        return newName;
    }

    public static void renameCommand(ChannelHandlerContext ctx, Path path, String name) {
        boolean isRename = new File(String.valueOf(path)).renameTo(new File(path.getParent() + File.separator + name));
        if (!isRename) {
            sendError(ctx, "Ошибка. Преименовать элемент не удалось");
        }
    }

    public static void getFiles(ChannelHandlerContext ctx, Path path) {
        ArrayList<HelpFile> list = new ArrayList<>();
        try {
            File dir = new File(path.toString());
            File[] arrFiles = dir.listFiles();
            if (arrFiles.length != 0) {
                for (File file : arrFiles) {
                    list.add(new HelpFile(file.getName(), file.isFile()));
                }
            }
            sendMessage(ctx, new ListFilesMessage(list));
        } catch (Exception e) {
            log.error("e=", e);
            sendError(ctx, "Что-то пошло не так");
        }
    }

    public static void deleteCommand(ChannelHandlerContext ctx, Path path, String fileName) {
        Path way = Paths.get(path + File.separator + fileName);
        File file = way.toFile();
        if (!file.exists())
            return;
        if (!file.isFile()) recursiveDelete(file);
        else file.delete();
    }

    private static void recursiveDelete(File file) {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                recursiveDelete(f);
            }
        }
        file.delete();
    }

    private static void sendError(ChannelHandlerContext ctx, String s) {
        StringMessage message = new StringMessage(s);
        sendMessage(ctx, message);
    }

    public static void downland(ChannelHandlerContext ctx, FileMessage message) {
        Path path = getVerifiedFileName(Paths.get(message.getPath()), message.getName());
        try (FileOutputStream fos = new FileOutputStream(path.toString())) {
            fos.write(message.getFileByte());
        } catch (IOException e) {
            log.error("e=", e);
            sendError(ctx, "Ошибка загрузки файла");
        }
    }

    public static void getFileToClient(ChannelHandlerContext ctx, String path, String name) {
        Path pathFile = Paths.get(path + File.separator + name);
        if (Files.exists(pathFile)) {
            try {
                FileMessage message = new FileMessage(
                        path,
                        Files.readAllBytes(pathFile),
                        name
                );
                sendMessage(ctx, message);
            } catch (IOException e) {
                log.error("e=", e);
                sendError(ctx, "Ошибка загрузки файла");
            }

        } else sendError(ctx, "Файл не найден");

    }
}
