package server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protobuf.ProtoMessages.ProtoMessage;
import transceiver.MessageTransceiver;

import java.net.InetSocketAddress;

public class ServerMessageHandler extends SimpleChannelInboundHandler<ProtoMessage> {

  private ChannelHandlerContext ctx;
  private final Logger logger = LoggerFactory.getLogger(ServerMessageHandler.class);
  private InetSocketAddress remoteAddress;
  private final MessageTransceiver transceiver;
  private final Long handlerId;

  ServerMessageHandler(Long id,MessageTransceiver transceiver){
    this.handlerId = id;
    this.transceiver = transceiver;
    transceiver.registerHandler(handlerId,this);
  }


  @Override
  protected void channelRead0(ChannelHandlerContext ctx, ProtoMessage msg) throws Exception {
    logger.trace("channelRead0 {} sent: {}", ctx.channel().remoteAddress(), msg);
    transceiver.handleMessage(remoteAddress,msg);
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    logger.trace("channelActive remote peer: {} connected", ctx.channel().remoteAddress());
    this.ctx = ctx;
    remoteAddress = (InetSocketAddress)ctx.channel().remoteAddress();
    transceiver.registerHandlerActive(remoteAddress,this);
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    logger.trace("channelInactive remote peer: {} disconnected", ctx.channel().remoteAddress());
    transceiver.registerHandlerInActive(remoteAddress,handlerId);
  }

  public void sendMessage(ProtoMessage message){
    if (ctx != null && ctx.channel().isActive() && ctx.channel().isWritable()) {
      ctx.writeAndFlush(message);
    }
    else{
      logger.warn("sendMessage called when channel not active or writable");
    }
  }

  public Long getId(){
    return handlerId;
  }

}
