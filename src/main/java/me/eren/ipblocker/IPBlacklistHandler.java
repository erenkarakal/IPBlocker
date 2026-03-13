package me.eren.ipblocker;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.net.InetSocketAddress;
import java.util.Set;

public class IPBlacklistHandler extends ChannelInboundHandlerAdapter {

    private final Set<String> blacklistedIps;

    public IPBlacklistHandler(Set<String> blacklistedIps) {
        this.blacklistedIps = blacklistedIps;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof Channel channel) {
            // this is a new connection attempt
            String ip = ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress();

            if (blacklistedIps.contains(ip)) {
                return;
            }
        }
        super.channelRead(ctx, msg);
    }

}
