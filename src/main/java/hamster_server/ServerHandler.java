package hamster_server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

public class ServerHandler implements ChannelHandler {

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();

	}

	public void handlerAdded(ChannelHandlerContext arg0) throws Exception {
		// TODO Auto-generated method stub

	}

	public void handlerRemoved(ChannelHandlerContext arg0) throws Exception {
		// TODO Auto-generated method stub

	}

}
