package com.zjc.socket;

import com.zjc.constant.WebSocketConstants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.CharsetUtil;


/**
 * create by zhaojinchao on 2019/2/1
 */
public class WebSocketResponse {

    /**
     * 服务端向客户端发送消息
     *
     * @param context
     * @param httpRequest
     * @param response
     */
    public static void sendHttpResponse(ChannelHandlerContext context, FullHttpRequest httpRequest, DefaultFullHttpResponse response) {
        if (response.getStatus().code() != WebSocketConstants.SUCCESS_CODE) {
            ByteBuf byteBuf = Unpooled.copiedBuffer(response.getStatus().toString(), CharsetUtil.UTF_8);
            response.content().writeBytes(byteBuf);
            byteBuf.release();
        }
        //向客户端发送数据
        ChannelFuture channelFuture = context.channel().writeAndFlush(response);
        if (response.getStatus().code() != WebSocketConstants.SUCCESS_CODE) {
            channelFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }
}
