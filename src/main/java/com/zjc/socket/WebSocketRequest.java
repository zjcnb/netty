package com.zjc.socket;

import com.zjc.constant.WebSocketConstants;
import com.zjc.netty.NettyConfig;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.*;

import java.util.Date;

/**
 * create by zhaojinchao on 2019/2/1
 */
public class WebSocketRequest {

    private static WebSocketServerHandshaker webSocketServerHandshaker;

    /**
     * 客户端向服务端发起Http握手请求
     *
     * @param context
     * @param fullHttpRequest
     */
    public static void handHttpRequest(ChannelHandlerContext context, FullHttpRequest fullHttpRequest) {
        if (fullHttpRequest.getDecoderResult().isSuccess() || !WebSocketConstants.WEB_SOCKET.equals(fullHttpRequest.headers().get(WebSocketConstants.HEADER))) {
            WebSocketResponse.sendHttpResponse(context, fullHttpRequest, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }
        WebSocketServerHandshakerFactory factory = new WebSocketServerHandshakerFactory(WebSocketConstants.WEB_SOCKET_URL, null, false);
        webSocketServerHandshaker = factory.newHandshaker(fullHttpRequest);
        if (webSocketServerHandshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(context.channel());
        } else {
            webSocketServerHandshaker.handshake(context.channel(), fullHttpRequest);
        }
    }


    /**
     * 客户端向服务端发起WebSocket握手请求
     *
     * @param context
     * @param webSocketFrame
     */
    public static void handWebSocketFrame(ChannelHandlerContext context, WebSocketFrame webSocketFrame) {
        //是否关闭WebSocket指令
        if (webSocketFrame instanceof CloseWebSocketFrame) {
            webSocketServerHandshaker.close(context.channel(), ((CloseWebSocketFrame) webSocketFrame).retain());
        }
        //是否ping消息
        if (webSocketFrame instanceof PingWebSocketFrame) {
            context.channel().write(new PongWebSocketFrame(webSocketFrame.content().retain()));
            return;
        }
        //是否二进制消息
        if (!(webSocketFrame instanceof TextWebSocketFrame)) {
            throw new RuntimeException("[" + WebSocketRequest.class.getName() + "]" + "不支持消息");
        }
        //获取客户端消息
        String msg = ((TextWebSocketFrame) webSocketFrame).text();
        System.out.println("服务端收到客户端的消息 is {}" + msg);

        TextWebSocketFrame socketFrame = new TextWebSocketFrame(new Date().toString() + context.channel().id() + "=====>>>>>>" + msg);

        //群发,向连接服务端的客户端群发消息
        NettyConfig.channelGroup.writeAndFlush(socketFrame);
    }
}
