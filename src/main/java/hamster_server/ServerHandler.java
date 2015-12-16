package hamster_server;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.util.HashMap;
import java.util.Map;

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

@Sharable
public class ServerHandler extends ChannelInboundHandlerAdapter {

	private HttpRequest request;
	private Map<String, UriHandlerBased> handlers = new HashMap<String, UriHandlerBased>();

	public ServerHandler() {
		if (handlers.size() != 0) {
			handlers.put("/hello", new HelloServerHandler());
		}
	}

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		UriHandlerBased handler = null;
		if (msg instanceof HttpRequest) {
			HttpRequest request = this.request = (HttpRequest) msg;
			QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
			String context = queryStringDecoder.path();
			System.out.println(context);
			handler = handlers.get(context);
			if (handler != null) {
				handler.process(request);
			}
		}

		if (msg instanceof LastHttpContent) {
			FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,
					((LastHttpContent) msg).decoderResult().isSuccess() ? OK : BAD_REQUEST);
			;
			response.headers().set(CONTENT_TYPE,
					handler != null ? handler.getContentType() : "text/plain; charset=UTF-8");
			boolean keepAlive = HttpUtil.isKeepAlive(request);
			if(keepAlive){
				response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
				response.headers().set(CONTENT_LENGTH,response.content().readableBytes());	
			}
			ctx.write(response);
		}

	}

}
