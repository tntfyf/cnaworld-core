package cn.cnaworld.framework.infrastructure.utils.http.netty;


import cn.cnaworld.framework.infrastructure.common.statics.enums.RestFulType;
import cn.cnaworld.framework.infrastructure.utils.code.CnaCodeUtil;
import cn.cnaworld.framework.infrastructure.utils.http.CnaHttpClientUtil;
import com.alibaba.fastjson.JSON;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import javax.net.ssl.SSLException;
import java.net.*;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Lucifer
 * @date 2023/3/12
 * @since 1.0.0
 */
@Slf4j
public class CnaNettyHttpClientUtil {

    private static volatile Bootstrap bootstrapSsl = null;

    private static volatile Bootstrap bootstrap = null;

    private static final HttpClientHandler HTTP_CLIENT_HANDLER = new HttpClientHandler();

    /**
     * 发送 RestFul 支持不支持请求体的请求
     * 支持 DELETE GET ，默认 GET
     * @author Lucifer
     * @date 2023/3/9
     * @since 1.0.0
     * @param url 请求URL
     * @return 响应报文
     */
    public static String send(String url)  {
        return sendHttpRequest(url,null,null,null, RestFulType.GET);
    }

    /**
     * 发送 RestFul 支持不支持请求体的请求
     * 支持 DELETE GET ，默认 GET
     * @author Lucifer
     * @date 2023/3/9
     * @since 1.0.0
     * @param url 请求URL
     * @param paramsMap 请求参数
     * @param restFulType restFul协议类型
     * @return 响应报文
     */
    public static String send(String url , Map<String,Object> paramsMap ,RestFulType restFulType)  {
        return sendHttpRequest(url,null,paramsMap,null, restFulType);
    }

    /**
     * 发送 RestFul 支持不支持请求体的请求
     * 支持 DELETE GET ，默认 GET
     * @author Lucifer
     * @date 2023/3/9
     * @since 1.0.0
     * @param url 请求URL
     * @param restFulType restFul协议类型
     * @return 响应报文
     */
    public static String send(String url ,RestFulType restFulType)  {
        return sendHttpRequest(url,null,null,null, restFulType);
    }

    /**
     * 发送 RestFul 支持不支持请求体的请求
     * 支持 DELETE GET ，默认 GET
     * @author Lucifer
     * @date 2023/3/9
     * @since 1.0.0
     * @param url 请求URL
     * @param paramsMap 请求参数
     * @return 响应报文
     */
    public static String send(String url, Map<String,Object> paramsMap)  {
        return sendHttpRequest(url,null,paramsMap,null, RestFulType.GET);
    }

    /**
     * 发送 RestFul 支持不支持请求体的请求
     * 支持 DELETE GET ，默认 GET
     * @author Lucifer
     * @date 2023/3/9
     * @since 1.0.0
     * @param url 请求URL
     * @param paramsMap 请求参数
     * @param headerMap 请求头
     * @return 响应报文
     */
    public static String send(String url, Map<String,Object> paramsMap , Map<String,String> headerMap)  {
        return sendHttpRequest(url,null,paramsMap,headerMap, RestFulType.GET);
    }

    /**
     * 发送 RestFul 支持不支持请求体的请求
     * 支持 DELETE GET ，默认 GET
     * @author Lucifer
     * @date 2023/3/9
     * @since 1.0.0
     * @param url 请求URL
     * @param paramsMap 请求参数
     * @param headerMap 请求头
     * @param restFulType restFul协议类型
     * @return 响应报文
     */
    public static String send(String url, Map<String,Object> paramsMap , Map<String,String> headerMap,RestFulType restFulType)  {
        return sendHttpRequest(url,null,paramsMap,headerMap, restFulType);
    }

    /**
     * 发送RestFul 支持请求体的请求，忽略Ssl认证
     * 支持 POST PUT PATCH ，默认POST
     * @author Lucifer
     * @date 2023/3/9
     * @since 1.0.0
     * @param url 请求URL
     * @param dto 请求参数体
     * @return 响应报文
     */
    public static String sendEntity(String url,Object dto)  {
        return sendHttpRequest(url,dto,null,null, RestFulType.POST);
    }

    /**
     * 发送RestFul 支持请求体的请求，忽略Ssl认证
     * 支持 POST PUT PATCH ，默认POST
     * @author Lucifer
     * @date 2023/3/9
     * @since 1.0.0
     * @param url 请求URL
     * @return 响应报文
     */
    public static String sendEntity(String url )  {
        return sendHttpRequest(url,null,null,null, RestFulType.POST);
    }

    /**
     * 发送RestFul 支持请求体的请求，忽略Ssl认证
     * 支持 POST PUT PATCH ，默认POST
     * @author Lucifer
     * @date 2023/3/9
     * @since 1.0.0
     * @param url 请求URL
     * @param restFulType restFul协议类型
     * @return 响应报文
     */
    public static String sendEntity(String url , Map<String,Object> paramsMap ,RestFulType restFulType)  {
        return sendHttpRequest(url,null,paramsMap,null, restFulType);
    }

    /**
     * 发送RestFul 支持请求体的请求，忽略Ssl认证
     * 支持 POST PUT PATCH ，默认POST
     * @author Lucifer
     * @date 2023/3/9
     * @since 1.0.0
     * @param url 请求URL
     * @param restFulType restFul协议类型
     * @return 响应报文
     */
    public static String sendEntity(String url ,RestFulType restFulType)  {
        return sendHttpRequest(url,null,null,null, restFulType);
    }

    /**
     * 发送RestFul 支持请求体的请求，忽略Ssl认证
     * 支持 POST PUT PATCH ，默认POST
     * @author Lucifer
     * @date 2023/3/9
     * @since 1.0.0
     * @param url 请求URL
     * @param dto 请求参数体
     * @param restFulType restFul协议类型
     * @return 响应报文
     */
    public static String sendEntity(String url , Object dto ,RestFulType restFulType)  {
        return sendHttpRequest(url,dto,null,null, restFulType);
    }

    /**
     * 发送RestFul 支持请求体的请求，忽略Ssl认证
     * 支持 POST PUT PATCH ，默认POST
     * @author Lucifer
     * @date 2023/3/9
     * @since 1.0.0
     * @param url 请求URL
     * @param paramsMap 请求参数
     * @param headerMap 请求头
     * @param restFulType restFul协议类型
     * @return 响应报文
     */
    public static String sendEntity(String url , Map<String,Object> paramsMap , Map<String,String> headerMap,RestFulType restFulType)  {
        return sendHttpRequest(url,null,paramsMap,headerMap, restFulType);
    }

    /**
     * 发送RestFul 支持请求体的请求，忽略Ssl认证
     * 支持 POST PUT PATCH ，默认POST
     * @author Lucifer
     * @date 2023/3/9
     * @since 1.0.0
     * @param url 请求URL
     * @param dto 请求参数体
     * @param headerMap 请求头
     * @param restFulType restFul协议类型
     * @return 响应报文
     */
    public static String sendEntity(String url , Object dto , Map<String,String> headerMap,RestFulType restFulType)  {
        return sendHttpRequest(url,dto,null,headerMap, restFulType);
    }

    /**
     * 发送RestFul 支持请求体的请求
     * 支持 POST PUT PATCH ，默认POST
     * @author Lucifer
     * @date 2023/3/9
     * @since 1.0.0
     * @param url 请求URL
     * @param paramsMap 请求参数
     * @return 响应报文
     */
    public static String sendEntity(String url, Map<String,Object> paramsMap)  {
        return sendHttpRequest(url,null,paramsMap,null, RestFulType.POST);
    }

    /**
     * 发送RestFul 支持请求体的请求
     * 支持 POST PUT PATCH ，默认POST
     * @author Lucifer
     * @date 2023/3/9
     * @since 1.0.0
     * @param url 请求URL
     * @param paramsMap 请求参数
     * @param headerMap 请求头
     * @return 响应报文
     */
    public static String sendEntity(String url , Map<String,Object> paramsMap , Map<String,String> headerMap)  {
        return sendHttpRequest(url,null,paramsMap,headerMap, RestFulType.POST);
    }

    /**
     * 发送RestFul 支持请求体的请求
     * 支持 POST PUT PATCH ，默认POST
     * @author Lucifer
     * @date 2023/3/9
     * @since 1.0.0
     * @param url 请求URL
     * @param dto 请求参数体
     * @param headerMap 请求头
     * @return 响应报文
     */
    public static String sendEntity(String url , Object dto , Map<String,String> headerMap)  {
        return sendHttpRequest(url,dto,null,headerMap, RestFulType.POST);
    }

    /**
     * 发送RestFul 支持请求体的请求
     * 支持 POST PUT PATCH ，默认POST
     * @author Lucifer
     * @date 2023/3/9
     * @since 1.0.0
     * @param url 请求URL
     * @param dto 请求参数体
     * @param paramsMap 请求参数
     * @param headerMap 请求头
     * @return 响应报文
     */
    public static String sendEntity(String url , Object dto , Map<String,Object> paramsMap , Map<String,String> headerMap)  {
        return sendHttpRequest(url,dto,paramsMap,headerMap, RestFulType.POST);
    }

    /**
     * 发送RestFul 支持请求体的请求
     * 支持 POST PUT PATCH ，默认POST
     * @author Lucifer
     * @date 2023/3/9
     * @since 1.0.0
     * @param url 请求URL
     * @param dto 请求参数体
     * @param paramsMap 请求参数
     * @param headerMap 请求头
     * @param restFulType restFul协议类型
     * @return 响应报文
     */
    public static String sendEntity(String url , Object dto , Map<String,Object> paramsMap , Map<String,String> headerMap,RestFulType restFulType)  {
        return sendHttpRequest(url,dto,paramsMap,headerMap, restFulType);
    }

    private static String sendHttpRequest(String url , Object dto, Map<String,Object> paramsMap , Map<String,String> headerMap, RestFulType restFulType){
        ClientHold clientHold;
        try {
            clientHold = initClientHold(url,paramsMap);
            log.debug("准备发送{}请求，url : {} , dto : {}, paramsMap : {} , headerMap : {}",restFulType,url,dto,paramsMap,headerMap);
            sendHttpRequestByNetty(clientHold,dto,headerMap,restFulType);
            long stime = System.currentTimeMillis();
            while (clientHold.getChannelPromise() == null) {
                if ( System.currentTimeMillis()-stime < 20000) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        log.error("请求超时，无法得到服务端响应，请重试 ： {}",url);
                        throw new RuntimeException("请求超时，无法得到服务端响应，请重试 ：" + url);
                    }
                }else {
                    log.error("请求超时，无法得到服务端响应，请重试 ： {}",url);
                    throw new RuntimeException("请求超时，无法得到服务端响应，请重试 ：" + url);
                }
            }
            ChannelPromise promise = clientHold.getChannelPromise();
            promise.await(3, TimeUnit.SECONDS);
            String response = clientHold.getResult();
            log.debug("{}请求发送完毕，url : {} , dto : {}, paramsMap : {} , headerMap : {} , response : {}",restFulType,url,dto,paramsMap,headerMap,response);
            return response;
        } catch (MalformedURLException | InterruptedException | URISyntaxException | UnknownHostException e) {
            log.error("CnaNettyHttpClientUtil 发送异常：{}",e.getMessage(),e);
            throw new RuntimeException(e);
        }
    }

    private static ClientHold initClientHold(String urlStr,Map<String,Object> paramsMap) throws MalformedURLException, UnknownHostException, URISyntaxException {
        ClientHold clientHold = new ClientHold();
        InetSocketAddress inetAddress;
        boolean isSsl = urlStr.contains("https");
        URL url = new URL(urlStr);
        String host = url.getHost();
        InetAddress address = InetAddress.getByName(host);
        if (!host.equalsIgnoreCase(address.getHostAddress())) {
            //域名连接,https默认端口是443，http默认端口是80
            inetAddress = new InetSocketAddress(address, isSsl ? 443 : 80);
        } else {
            //ip+端口连接
            int port = url.getPort();
            inetAddress = InetSocketAddress.createUnresolved(host, port);
        }
        clientHold.setInetAddress(inetAddress);
        urlStr = CnaHttpClientUtil.processedUrl(urlStr,paramsMap);
        for(int i = 0; i < 3; i++){
            urlStr = urlStr.substring(urlStr.indexOf("/")+1);
        }
        URI uri = new URI("/"+urlStr);
        clientHold.setUri(uri);
        if (!isSsl) {
            if(bootstrap == null) {
                //防止并发创建多个对象
                synchronized (CnaNettyHttpClientUtil.class) {
                    //防止加锁之前并发的线程过了第一个判断导致出现多实例
                    if(bootstrap == null) {
                        bootstrap =  initBootstrap(false);
                    }
                }
            }
            clientHold.setBootstrap(bootstrap);
        }else {
            if(bootstrapSsl == null) {
                //防止并发创建多个对象
                synchronized (CnaNettyHttpClientUtil.class) {
                    //防止加锁之前并发的线程过了第一个判断导致出现多实例
                    if(bootstrapSsl == null) {
                        bootstrapSsl = initBootstrap(true);
                    }
                }
                clientHold.setBootstrap(bootstrapSsl);
            }

        }
        long snowflakeId = CnaCodeUtil.getSnowflakeId();
        clientHold.setSnowflakeId(snowflakeId);
        HTTP_CLIENT_HANDLER.getClientHoldMap().put(snowflakeId,clientHold);
        return clientHold;
    }

    private  static Bootstrap initBootstrap(boolean isSsl){
        NioEventLoopGroup group = new NioEventLoopGroup();
        return new Bootstrap().group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new LoggingHandler(LogLevel.ERROR))
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) throws SSLException {
                        SocketChannel socketChannel = (SocketChannel) channel;
                        socketChannel.config().setKeepAlive(true);
                        socketChannel.config().setTcpNoDelay(true);
                        if (isSsl) {
                            SslContext context = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
                            channel.pipeline().addLast(context.newHandler(channel.alloc()));
                        }
                        channel.pipeline().addLast(new HttpClientCodec());
                        channel.pipeline().addLast(new HttpObjectAggregator(65536));
                        channel.pipeline().addLast(new HttpContentDecompressor());
                        channel.pipeline().addLast(CnaNettyHttpClientUtil.HTTP_CLIENT_HANDLER);
                    }
                });
    }

    private static void sendHttpRequestByNetty(ClientHold clientHold, Object dto ,Map<String,String> headerMap, RestFulType restFulType) throws InterruptedException {
        ChannelFuture future = clientHold.getBootstrap().connect(clientHold.getInetAddress()).sync();
        Channel client = future.channel();
        AttributeKey<Long> key = AttributeKey.valueOf("snowflakeId");
        client.attr(key).set(clientHold.getSnowflakeId());
        HttpMethod httpMethod;
        boolean entityType=false;
        switch (restFulType) {
            case POST:
                httpMethod= HttpMethod.POST;
                entityType=true;
                break;
            case PUT:
                httpMethod= HttpMethod.PUT;
                entityType=true;
                break;
            case PATCH:
                httpMethod= HttpMethod.PATCH;
                entityType=true;
                break;
            case DELETE:
                httpMethod= HttpMethod.DELETE;
                break;
            case GET:
            default:
                httpMethod= HttpMethod.GET;
                break;
        }
        FullHttpRequest request;
        if (entityType && ObjectUtils.isNotEmpty(dto)) {
            ByteBuf byteBuf = Unpooled.wrappedBuffer(JSON.toJSONString(dto).getBytes());
            request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_0, httpMethod, clientHold.getUri().toASCIIString(),byteBuf);
        }else {
            request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_0, httpMethod, clientHold.getUri().toASCIIString());
        }
        request.headers().add(HttpHeaderNames.CONNECTION,HttpHeaderValues.KEEP_ALIVE);
        request.headers().add(HttpHeaderNames.CONTENT_LENGTH,request.content().readableBytes());
        request.headers().add(HttpHeaderNames.CONTENT_TYPE,"application/json");
        if(headerMap != null && headerMap.size()>0) {
            for(Map.Entry<String,String> entry : headerMap.entrySet()){
                request.headers().add(entry.getKey(),entry.getValue());
            }
        }
        client.writeAndFlush(request).sync();
        client.closeFuture().sync();
    }
}
