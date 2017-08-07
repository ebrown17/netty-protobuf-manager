package client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import protobuf.ClientMessageData;

public class ClientDataHandler extends SimpleChannelInboundHandler<ClientMessageData>{

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ClientMessageData msg) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
