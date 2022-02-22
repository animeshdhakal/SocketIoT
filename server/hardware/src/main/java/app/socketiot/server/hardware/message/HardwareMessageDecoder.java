package app.socketiot.server.hardware.message;

import java.util.List;

import app.socketiot.server.core.metrics.QuotaLimitChecker;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;

public class HardwareMessageDecoder extends ByteToMessageDecoder {
    public final QuotaLimitChecker quotaLimitChecker;

    public HardwareMessageDecoder(int limit) {
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
            System.out.println("Quota exceeded");
            return;
        }

        if (in.readableBytes() < bodyLength) {
            in.resetReaderIndex();
            return;
        }

        out.add(new HardwareMessage(msgtype,
                ((String) in.readCharSequence(bodyLength, CharsetUtil.UTF_8)).split("\0")));

    }
}
