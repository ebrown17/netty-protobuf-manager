package protocol.protomessage;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protobuf.ProtoMessages;

import java.net.InetSocketAddress;

public class MessageHandler extends SimpleChannelInboundHandler<ProtoMessages.ProtoMessage> {

  private ChannelHandlerContext ctx;
  private final Logger logger = LoggerFactory.getLogger(MessageHandler.class);
  private InetSocketAddress remoteAddress;
  private final MessageTransceiver transceiver;
  private final Long handlerId;

  public MessageHandler(Long id,MessageTransceiver transceiver){
    this.handlerId = id;
    this.transceiver = transceiver;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, ProtoMessages.ProtoMessage msg) throws Exception {
    logger.trace("channelRead0 {} sent: {}", ctx.channel().remoteAddress(), msg);
    transceiver.handleMessage(remoteAddress,msg);
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    logger.trace("channelActive remote peer: {} connected", ctx.channel().remoteAddress());
    this.ctx = ctx;
    remoteAddress = (InetSocketAddress)ctx.channel().remoteAddress();
    transceiver.handlerActive(remoteAddress,this);
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    logger.trace("channelInactive remote peer: {} disconnected", ctx.channel().remoteAddress());
    transceiver.handlerInActive(remoteAddress,handlerId);
  }

  public void sendMessage(ProtoMessages.ProtoMessage message){
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
