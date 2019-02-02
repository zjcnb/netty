package com.zjc.channel;

import com.zjc.constant.WebSocketConstants;
import com.zjc.socket.WebSocketHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * create by zhaojinchao on 2019/2/2
 */
public class WebSocketChannelHandle extends ChannelInitializer<SocketChannel> {
    /**
     * 初始化连接时各个组件
     *
     * @param socketChannel
     * @throws Exception
     */
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline().addLast(WebSocketConstants.HTTP_CODEC, new HttpServerCodec());
        socketChannel.pipeline().addLast(WebSocketConstants.AGGREGATOR, new HttpObjectAggregator(WebSocketConstants.MAX_SIZE));
        socketChannel.pipeline().addLast(WebSocketConstants.HTTP_CHUNKED, new ChunkedWriteHandler());
        socketChannel.pipeline().addLast(WebSocketConstants.HANDLER, new WebSocketHandler());
    }

}
