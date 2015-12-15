package hamster_server;

import java.util.HashMap;
import java.util.Map;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

public class ServerHandler extends ChannelInboundHandlerAdapter {

	private HttpRequest request;
	private Map<String, UriHandlerBased> handlers = new HashMap<String, UriHandlerBased>();

	public ServerHandler(){
		if(handlers.size()!=0){
			handlers.put("/hello", new HelloServerHandler() );
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

	}

}
