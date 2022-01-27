package app.socketiot.server.hardware;

import app.socketiot.server.core.Holder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class HardwareHandler extends ChannelInboundHandlerAdapter{
    private final Holder holder;

    public HardwareHandler(Holder holder) {
        this.holder = holder;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf rcvd = (ByteBuf) msg;

        short msg_len = rcvd.readShort();
        short msg_type = rcvd.readShort();

        byte[] buf = new byte[msg_len];

        rcvd.readBytes(buf);

        String[] msg_args = new String(buf).split("\0"); 

    

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("HardwareHandler: New Client Connected");
    }

}
