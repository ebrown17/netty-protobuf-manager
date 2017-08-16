package client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import client.ClientDataHandler;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class ClientHeartBeatHandler extends ChannelDuplexHandler {
	
	private ClientDataHandler handler;
	private final Logger logger = LoggerFactory.getLogger("client.ClientHeartBeatHandler");
	private int maxTimeouts,timeoutCount=0;
	
	/**
     *      
     * @param maxTimeouts
     *        Max amount of timeouts allowed between channel reads. Time limit is specified in {@code IdleStateHandler}. 
     *        If limit is reached; connection will be closed and retry logic in {@code ClientDataHandler} channel inactive method is called.
     */
	
	public ClientHeartBeatHandler(int maxTimeouts){
		this.maxTimeouts = maxTimeouts;
	}
		
	@Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE) {
            	
            	if(timeoutCount >= maxTimeouts){
            		logger.info("userEventTriggered No heartbeat reponse for {} seconds. Closing Connection.",maxTimeouts * 10);
            		ctx.close();
            	}
            	else {
            		handler =  ctx.channel().pipeline().get(ClientDataHandler.class);
                	handler.sendheartBeat();
                	timeoutCount++;
            	}
            } else if (e.state() == IdleState.WRITER_IDLE) {
               
            }
        }
    }
	
	public void resetTimeoutCounter(){
		timeoutCount = 0;
	}
}