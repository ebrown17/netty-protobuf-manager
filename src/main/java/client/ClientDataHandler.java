package client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import protobuf.ProtobufMessage;

public class ClientDataHandler extends SimpleChannelInboundHandler<ProtobufMessage.ProtobufData>{
	
	private ChannelHandlerContext cTx;
	private ClientConnector client;
	
	public ClientDataHandler(ClientConnector client){
		this.client=client;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ProtobufMessage.ProtobufData msg) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	@Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
       
        cTx = ctx;
        System.out.println("Client connected to remote peer");
    	
    }
	
	@Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		client.connected=false;
        System.out.println("Client disconnected from remote peer");
        client.connect();
    	
    }
	
	public void sendData(int count){
		if(client.connected){
			System.out.println("sending... " + count);
			ProtobufMessage.ProtobufData data = ProtobufMessage.ProtobufData.newBuilder().setDataString("Test").setDataNumber(count).build();
			cTx.writeAndFlush(data);
		}
		
		
	}
	

}
