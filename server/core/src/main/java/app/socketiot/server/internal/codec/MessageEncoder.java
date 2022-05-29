package app.socketiot.server.internal.codec;

import app.socketiot.server.core.model.message.InternalMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;

public class MessageEncoder extends MessageToByteEncoder<InternalMessage> {
    @Override
    protected void encode(ChannelHandlerContext ctx, InternalMessage msg, ByteBuf out) throws Exception {
        String joinedBody = String.join("\0", msg.body);
        out.writeShort(joinedBody.length());
        out.writeShort(msg.type);
        if (joinedBody.length() > 0) {
            out.writeCharSequence(joinedBody, CharsetUtil.UTF_8);
        }
    }
}