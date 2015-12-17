package hamster_server;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;

@Sharable
public class ServerHandler extends ChannelInboundHandlerAdapter {

	private HttpRequest request;
	private Map<String, UriHandlerBased> handlers = new HashMap<String, UriHandlerBased>();
	private final StringBuilder buf = new StringBuilder();

	private String ip;
	private int sentBytes;
	private int receivedBytes;
	private double speed;

	public ServerHandler() {

		if (handlers.size() == 0) {
			handlers.put("/hello", new HelloServerHandler());
			handlers.put("/redirect", new RedirectServerHandler());
			handlers.put("/status", new StatisticServerHandler());
		}
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		ip = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();
		speed = System.currentTimeMillis();
		ServerInitializer.params.addActiveNumber();
		ServerInitializer.params.addQueryNumber();
	}

	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		receivedBytes += msg.toString().length();

		UriHandlerBased handler = null;
		if (msg instanceof HttpRequest) {
			HttpRequest request = this.request = (HttpRequest) msg;
			QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
			String context = queryStringDecoder.path();
			buf.setLength(0);
			System.out.println(context);
			ServerInitializer.params.addUniqueConnectin(context);

			handler = handlers.get(context);

			if (handler != null) {
				handler.process(ctx, request, buf);
			}

		}

		if (msg instanceof LastHttpContent) {
			FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,
					((LastHttpContent) msg).decoderResult().isSuccess() ? OK : BAD_REQUEST,
					Unpooled.copiedBuffer(buf.toString(), CharsetUtil.UTF_8));
			response.headers().set(CONTENT_TYPE,
					handler != null ? handler.getContentType() : "text/plain; charset=UTF-8");
			boolean keepAlive = HttpUtil.isKeepAlive(request);
			if (keepAlive) {
				response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
				response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
			}
			sentBytes = response.content().writerIndex();
			ctx.write(response);
		}

	}

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		speed = (System.currentTimeMillis() - speed) / 1000;
		speed = (receivedBytes + sentBytes) / speed;

		IpParameters ipParams = new IpParameters();
		ipParams.setReceivedBytes(receivedBytes);
		ipParams.setSentBytes(sentBytes);
		ipParams.setSpeed(speed);
		ipParams.setSrcIp(ip);

		ServerInitializer.params.removeActiveNumber();
		ctx.flush();
	}

}
