package me.eren.ipblocker;

import io.netty.channel.*;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.network.ServerConnectionListener;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

public final class IPBlocker extends JavaPlugin {

    @Override
    public void onEnable() {
        injectIpBlocker();

    }

    public void injectIpBlocker() {
        DedicatedServer nmsServer = ((CraftServer) Bukkit.getServer()).getHandle().getServer();
        ServerConnectionListener connection = nmsServer.getConnection();

        List<ChannelFuture> channels = getChannels(connection);

        synchronized (channels) {
            for (ChannelFuture future : channels) {
                ChannelPipeline pipeline = future.channel().pipeline();

                // need to intercept the ServerBootstrap's childHandler
                // a simpler way is to use a ChannelInboundHandler on the server channel itself
                pipeline.addFirst(new IPBlacklistHandler(Set.of()));
            }
        }
    }

    private static List<ChannelFuture> getChannels(ServerConnectionListener connection) {
        try {
            Field channelsField = connection.getClass().getDeclaredField("channels");
            channelsField.setAccessible(true);
            // noinspection unchecked
            return (List<ChannelFuture>) channelsField.get(connection);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
