package client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import protobuf.ProtobufMessage;

public class ClientDataHandler extends SimpleChannelInboundHandler<ProtobufMessage.ProtobufData>{
	
	private ChannelHandlerContext cTx;

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ProtobufMessage.ProtobufData msg) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	@Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
       
        cTx = ctx;        
        System.out.println("Client channel activated");
    	
    }
	
	public void sendData(int count){
		System.out.println("sending... " + count);
		ProtobufMessage.ProtobufData data = ProtobufMessage.ProtobufData.newBuilder().setDataString("Test").setDataNumber(count).build();
		cTx.channel().writeAndFlush(data);
		
	}

}
