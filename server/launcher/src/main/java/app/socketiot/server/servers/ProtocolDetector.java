package app.socketiot.server.servers;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;

public abstract class ProtocolDetector extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 5) {
            return;
        }

        long headerBytes = in.getUnsignedInt(0);
        short lastByte = in.getUnsignedByte(4);

        buildPipeline(ctx.pipeline(), headerBytes, lastByte).remove(this);
    }


    private static boolean isHttp(long headerBytes) {
        return
        headerBytes == 1195725856L || // 'GET '
        headerBytes == 1347375956L || // 'POST'
        headerBytes == 1347769376L || // 'PUT '
        headerBytes == 1212498244L || // 'HEAD'
        headerBytes == 1330664521L || // 'OPTI'
        headerBytes == 1346458691L || // 'PATC'
        headerBytes == 1145392197L || // 'DELE'
        headerBytes == 1414676803L || // 'TRAC'
        headerBytes == 1129270862L;   // 'CONN'
    }


    private ChannelPipeline buildPipeline(ChannelPipeline pipeline, long headerBytes, short lastByte) {
        if(isHttp(headerBytes)){
            return buildHttpPipeline(pipeline);
        }else{
            return buildHardwarePipeline(pipeline);
        }
    }

    public abstract ChannelPipeline buildHttpPipeline(ChannelPipeline pipeline);

    public abstract ChannelPipeline buildHardwarePipeline(ChannelPipeline pipeline);
}
