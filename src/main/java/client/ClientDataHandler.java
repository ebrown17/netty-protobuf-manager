package client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import protobuf.ClientMessageData;

public class ClientDataHandler extends SimpleChannelInboundHandler<ClientMessageData>{
	
	private ChannelHandlerContext cTx;

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ClientMessageData msg) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	@Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
       
        cTx = ctx;
        
        System.out.println("Client channel connection activated");
    	
    }
	
	public void sendData(int count){
		System.out.println("sending... ");
		ClientMessageData.MessageData test = ClientMessageData.MessageData.newBuilder().setDataStr("test").setDataNum(count).build();
		cTx.channel().writeAndFlush(test);
		
	}

}
