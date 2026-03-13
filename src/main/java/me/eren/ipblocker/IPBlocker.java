package me.eren.ipblocker;

import io.netty.channel.*;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.network.ServerConnectionListener;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class IPBlocker extends JavaPlugin {

    private final Set<String> blockedIps = new HashSet<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        blockedIps.addAll(getConfig().getStringList("blocked-ips"));
        getLogger().info("Loaded " + blockedIps.size() + " blocked IPs");

        PluginCommand pluginCommand = getCommand("ipblocker");
        assert pluginCommand != null : "The build is broken.";

        IPBlockerCommand ipBlockerCommand = new IPBlockerCommand(blockedIps);
        pluginCommand.setExecutor(ipBlockerCommand);
        pluginCommand.setTabCompleter(ipBlockerCommand);

        injectIpBlocker(blockedIps);
    }

    @Override
    public void onDisable() {
        getConfig().set("blocked-ips", List.copyOf(blockedIps));
        saveConfig();
        getLogger().info("Saved " + blockedIps.size() + " blocked IPs.");
    }

    public void injectIpBlocker(Set<String> blockedIps) {
        DedicatedServer nms = ((CraftServer) Bukkit.getServer()).getHandle().getServer();
        ServerConnectionListener connection = nms.getConnection();

        List<ChannelFuture> channels = getChannels(connection);

        synchronized (channels) {
            for (ChannelFuture future : channels) {
                ChannelPipeline pipeline = future.channel().pipeline();

                // need to intercept the ServerBootstrap's childHandler
                // a simpler way is to use a ChannelInboundHandler on the server channel itself
                pipeline.addFirst(new IPBlacklistHandler(blockedIps));
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
