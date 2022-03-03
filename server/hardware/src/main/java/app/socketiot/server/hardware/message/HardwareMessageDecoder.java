package app.socketiot.server.hardware.message;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import app.socketiot.server.core.metrics.QuotaLimitChecker;
import app.socketiot.server.core.model.HardwareMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.CharsetUtil;

public class HardwareMessageDecoder extends ByteToMessageDecoder {
    public final static Logger log = LogManager.getLogger(HardwareMessageDecoder.class);
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
            log.trace("Quota Exceeded {}", quotaLimitChecker.getRequestsPerSecond());
            ctx.close();
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
