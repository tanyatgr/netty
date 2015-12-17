package hamster_server;

import static io.netty.handler.codec.http.HttpHeaderNames.LOCATION;
import static io.netty.handler.codec.http.HttpResponseStatus.MOVED_PERMANENTLY;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

public class RedirectServerHandler extends UriHandlerBased {

	@Override
	public void process(ChannelHandlerContext ctx,HttpRequest request, StringBuilder buf) {
		QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
		List<String> redirectUrl = queryStringDecoder.parameters().get("url");		
		FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, MOVED_PERMANENTLY);
	    response.headers().set(LOCATION, redirectUrl.get(0));
	    
	    
}

}
