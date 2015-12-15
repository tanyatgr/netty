package hamster_server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class Server {

	public static final int SERVER_PORT = Integer.parseInt(System.getProperty("port", "8080"));

	public void start() throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		
		try{
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup,workerGroup)
			.channel(NioServerSocketChannel.class)
			.handler(new LoggingHandler(LogLevel.INFO))
			.childHandler(new ServerInitializer());
			
			Channel ch = b.bind(SERVER_PORT).sync().channel();
		}finally{
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	public static void main(String[] args) throws Exception {
		new Server().start();
	}
}