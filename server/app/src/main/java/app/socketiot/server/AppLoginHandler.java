package app.socketiot.server;

import app.socketiot.server.core.model.DashBoard;
import app.socketiot.server.core.model.auth.User;
import app.socketiot.server.core.model.enums.MsgType;
import app.socketiot.server.core.model.json.JsonParser;
import app.socketiot.server.core.model.message.InternalMessage;
import app.socketiot.utils.Sha256Util;
import app.socketiot.utils.ValidatorUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

@ChannelHandler.Sharable
public class AppLoginHandler extends ChannelInboundHandlerAdapter {

    public final Holder holder;

    public AppLoginHandler(Holder holder) {
        this.holder = holder;
    }

    void handleRegister(ChannelHandlerContext ctx, String email, String password) {
        if (holder.userDao.ifUserExists(email)) {
            ctx.writeAndFlush(new InternalMessage(MsgType.FAILED, "User already exists"));
            return;
        }

        User user = new User(email, Sha256Util.createHash(password, email), System.currentTimeMillis(),
                new DashBoard());

        holder.userDao.addUser(user);

        ctx.writeAndFlush(new InternalMessage(MsgType.REGISTER, "User registered Successfully"));
    }

    void handleLogin(ChannelHandlerContext ctx, String email, String password) {
        User user = holder.userDao.getUser(email);

        if (user == null) {
            ctx.writeAndFlush(new InternalMessage(MsgType.FAILED, "Invalid Email or Password"));
            return;
        }

        if (user.password.equals(password) || user.password.equals(Sha256Util.createHash(password, email))) {
            user.dash.addAppChannel(ctx.channel());
            ctx.pipeline().replace(this, "AppHandler", new AppHandler(holder, user));
            ctx.writeAndFlush(new InternalMessage(MsgType.AUTH, JsonParser.toPrivateJson(user)));
        } else {
            ctx.writeAndFlush(new InternalMessage(MsgType.FAILED, "Invalid Email or Password"));
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        InternalMessage internalMessage = (InternalMessage) msg;

        String email = internalMessage.body[0];
        String password = internalMessage.body[1];

        if (email == null || password == null || !ValidatorUtil.validateEmail(email) || password.length() < 8) {
            ctx.writeAndFlush(new InternalMessage(MsgType.FAILED, "Invalid email or password"));
            return;
        }

        switch (internalMessage.type) {
            case MsgType.REGISTER:
                handleRegister(ctx, email, password);
                break;
            case MsgType.AUTH:
                handleLogin(ctx, email, password);
                break;
        }

    }

}
