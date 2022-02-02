package app.socketiot.server.servers;

import java.net.InetSocketAddress;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ipfilter.AbstractRemoteAddressFilter;

public class IpFilter extends AbstractRemoteAddressFilter<InetSocketAddress> {


    @Override
    public boolean accept(ChannelHandlerContext ctx, InetSocketAddress inetSocketAddress) {
        String ip = inetSocketAddress.getAddress().getHostAddress();
        return true;
    }
}
