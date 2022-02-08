package app.socketiot.server.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder.EndOfDataDecoderException;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder.ErrorDataDecoderException;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;
import io.netty.channel.ChannelHandler;

@ChannelHandler.Sharable
public class FileUploadHandler extends SimpleChannelInboundHandler<HttpObject> {

    private static final HttpDataFactory factory = new DefaultHttpDataFactory(true);
    private HttpPostRequestDecoder decoder;
    private String uploadUri;
    private String uploadPath;

    public FileUploadHandler(String uploadUri, String uploadPath) {
        this.uploadUri = uploadUri;
        this.uploadPath = uploadPath;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (decoder != null) {
            decoder.cleanFiles();
        }
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest req = (HttpRequest) msg;

            if (!req.uri().equals(uploadUri) && !HttpMethod.POST.equals(req.method())) {
                ctx.fireChannelRead(msg);
                return;
            }

            try {
                decoder = new HttpPostRequestDecoder(factory, req);
            } catch (ErrorDataDecoderException e) {
                e.printStackTrace();
                return;
            }
        }

        if (decoder != null) {
            if (msg instanceof HttpContent) {
                HttpContent chunk = (HttpContent) msg;
                try {
                    decoder.offer(chunk);
                } catch (ErrorDataDecoderException e1) {
                    e1.printStackTrace();
                    return;
                }
                readHttpDataChunkByChunk();
                if (chunk instanceof LastHttpContent) {
                    reset();
                }
            }
        }
    }

    private void reset() {
        decoder.destroy();
        decoder = null;
    }

    private void readHttpDataChunkByChunk() {
        try {
            while (decoder.hasNext()) {
                InterfaceHttpData data = decoder.next();
                if (data != null) {
                    writeHttpData(data);
                }
            }
        } catch (EndOfDataDecoderException e1) {

        }
    }

    private void writeHttpData(InterfaceHttpData data) {
        if (data.getHttpDataType() == HttpDataType.FileUpload) {
            FileUpload fileUpload = (FileUpload) data;
            if (fileUpload.isCompleted()) {
                try {
                    File file = new File(fileUpload.getFilename());
                    if (!file.exists()) {
                        file.createNewFile();
                    }

                    // To do
                    // FileChannel inputChannel = new
                    // FileInputStream(fileUpload.getFile()).getChannel();
                    // FileChannel outputChannel = new FileOutputStream(file).getChannel();

                    // outputChannel.transferFrom(inputChannel, 0, inputChannel.size());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.channel().close();
    }
}