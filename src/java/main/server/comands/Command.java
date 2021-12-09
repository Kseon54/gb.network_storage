package main.server.comands;

import io.netty.channel.ChannelHandlerContext;
import main.server.db.User;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class Command {

    public static boolean auth(ChannelHandlerContext ctx, String login, String pass) {
        List<User> listUsers = Arrays.stream(User.users)
                .filter(user -> user.getLogin().equals(login) && user.getPassword().equals(pass))
                .collect(Collectors.toList());
        return listUsers.size() == 1;
    }
}
