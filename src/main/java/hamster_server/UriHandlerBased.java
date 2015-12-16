package hamster_server;

import io.netty.handler.codec.http.HttpRequest;

public abstract class UriHandlerBased {

	public abstract void process(HttpRequest request, StringBuilder buf);

	public String getContentType() {
		return "text/plain; charset=UTF-8";
	}

	
}
