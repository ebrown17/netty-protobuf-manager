package server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import protobuf.ProtobufMessage;

public class ServerDataHandler extends SimpleChannelInboundHandler<ProtobufMessage.ProtobufData>{
	
	private ChannelHandlerContext cTx;

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ProtobufMessage.ProtobufData msg) throws Exception {
		System.out.println("Server Read: \n" + msg.toString());
		
	}
	
	@Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Remote peer made connection");
    	
    }
	
	@Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
       
        cTx = ctx;        
        System.out.println("Remote peer connection closed");
    	
    }
	

}
