package hamster_server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

public class HelloServerHandler extends UriHandlerBased {

	@Override
	public void process(ChannelHandlerContext ctx,HttpRequest request, StringBuilder buf) {
		try {
			Thread.sleep(10000);
			buf.append("Hello World!");
		} catch (InterruptedException e) {
			e.getMessage();
		}
		
		
		
	}

}
