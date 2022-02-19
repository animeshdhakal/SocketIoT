package app.socketiot.server.api;

import java.nio.file.Files;
import java.nio.file.Path;
import app.socketiot.server.core.exceptions.ExceptionHandler;
import app.socketiot.server.core.http.handlers.StatusMsg;
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
    private HttpPostRequestDecoder decoder = null;
    private String uploadUri;
    private String uploadFolder;

    public FileUploadHandler(String uploadUri, String uploadFolder) {
        this.uploadUri = uploadUri;
        this.uploadFolder = uploadFolder.endsWith("/") ? uploadFolder : uploadFolder + "/";
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

            if (!req.uri().equals(uploadUri) || !HttpMethod.POST.equals(req.method())) {
                ctx.fireChannelRead(msg);
                return;
            }

            try {
                decoder = new HttpPostRequestDecoder(factory, req);
            } catch (ErrorDataDecoderException e) {
                return;
            }

        }

        if (decoder != null && msg instanceof HttpContent) {

            HttpContent chunk = (HttpContent) msg;
            try {
                decoder.offer(chunk);
            } catch (ErrorDataDecoderException e1) {
            }

            if (chunk instanceof LastHttpContent) {
                String path = completeUpload();
                if (path != null) {
                    afterUpload(path);
                    ctx.writeAndFlush(StatusMsg.ok("Uploaded"));
                } else {
                    ctx.writeAndFlush(StatusMsg.badRequest("Invalid Upload"));
                }
            }

        } else {
            ctx.fireChannelRead(msg);
        }

    }

    private void reset() {
        decoder.destroy();
        decoder = null;
    }

    public void afterUpload(String path) {

    }

    private String completeUpload() {
        String uploadedFile = null;
        try {
            while (decoder.hasNext()) {
                InterfaceHttpData data = decoder.next();
                if (data != null && data.getHttpDataType() == HttpDataType.FileUpload) {
                    FileUpload fileUpload = (FileUpload) data;
                    if (fileUpload.isCompleted()) {
                        try {
                            Path tempFile = fileUpload.getFile().toPath();
                            Path uploadPath = Path.of(uploadFolder);
                            if (!Files.exists(uploadPath)) {
                                Files.createDirectories(uploadPath);
                            }

                            String extension = "";

                            if (fileUpload.getFilename().contains(".")) {
                                extension = fileUpload.getFilename()
                                        .substring(fileUpload.getFilename().lastIndexOf("."));
                            }

                            String fileName = tempFile.getFileName().toString() + extension;

                            Files.move(tempFile, Path.of(uploadFolder, fileName));

                            uploadedFile = uploadFolder + fileName;

                        } catch (Exception e) {
                        }
                    }
                }
            }
        } catch (EndOfDataDecoderException e1) {

        } finally {
            reset();
        }

        return uploadedFile;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ExceptionHandler.handleException(ctx, cause);
    }
}
