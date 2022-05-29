package app.socketiot.server.internal.codec;

import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;

import app.socketiot.server.core.model.message.InternalMessage;
import app.socketiot.server.metrics.QuotaLimitChecker;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.CharsetUtil;

public class MessageDecoder extends ByteToMessageDecoder {

    public final QuotaLimitChecker quotaLimitChecker;

    public MessageDecoder(int limit) {
        this.quotaLimitChecker = new QuotaLimitChecker(limit);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 4) {
            return;
        }

        int bodyLength = in.readUnsignedShort();
        int msgtype = in.readUnsignedShort();

        if (quotaLimitChecker.quotaExceeded()) {
            ctx.close();
            return;
        }

        if (in.readableBytes() < bodyLength) {
            in.resetReaderIndex();
            return;
        }

        out.add(new InternalMessage(msgtype,
                ((String) in.readCharSequence(bodyLength, CharsetUtil.UTF_8)).split("\0")));
    }
}
