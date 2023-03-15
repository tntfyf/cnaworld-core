package cn.cnaworld.framework.infrastructure.utils.http.netty;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Lucifer
 * @date 2023/3/12
 * @since 1.0.0
 */
@Slf4j
@Getter
@Setter
@ChannelHandler.Sharable
public class NettyHttpClientHandler extends ChannelInboundHandlerAdapter {

    private final ConcurrentHashMap<String, NettyHttpClientHold> clientHoldMap = new ConcurrentHashMap<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        if(msg instanceof FullHttpResponse){
            FullHttpResponse response = (FullHttpResponse)msg;
            ByteBuf buf = response.content();
            try {
                String result = buf.toString(CharsetUtil.UTF_8);
                ChannelPromise channelPromise = ctx.newPromise();
                channelPromise.setSuccess();
                AttributeKey<String> key = AttributeKey.valueOf("channelId");
                if (ctx.channel().hasAttr(key)) {
                    String channelId = ctx.channel().attr(key).get();
                    if (ObjectUtils.isNotEmpty(channelId)) {
                        if(clientHoldMap.containsKey(channelId)){
                            NettyHttpClientHold nettyHttpClientHold = clientHoldMap.get(channelId);
                            nettyHttpClientHold.setResult(result);
                            nettyHttpClientHold.setChannelPromise(channelPromise);
                            clientHoldMap.remove(channelId);
                        }
                    }
                }
            } finally {
                buf.release();
            }
        }
    }

}
