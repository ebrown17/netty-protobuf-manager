package protocol.protomessage;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import protobuf.ProtoMessages.ProtoMessage;
import protobuf.ProtoMessages.ProtoMessage.MessageType;

import java.net.InetSocketAddress;

public class MessageHandler extends SimpleChannelInboundHandler<ProtoMessage> {

  private ChannelHandlerContext ctx;
  private final static Logger logger = LoggerFactory.getLogger(MessageHandler.class);
  private InetSocketAddress remoteAddress;
  private final MessageTransceiver transceiver;
  private final Long handlerId;

  public MessageHandler(Long id, MessageTransceiver transceiver) {
    this.handlerId = id;
    this.transceiver = transceiver;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, ProtoMessage msg) throws Exception {
    logger.trace("channelRead0 {} sent: {}", ctx.channel().remoteAddress(), msg);
    MessageType type = msg.getMessageType();
    if (MessageType.DEFAULT_MESSAGE == type) {
      transceiver.handleMessage(remoteAddress, msg);
    }
    else {
      ctx.fireChannelRead(msg);
    }
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    logger.trace("channelActive remote peer: {} connected", ctx.channel().remoteAddress());
    this.ctx = ctx;
    remoteAddress = (InetSocketAddress) ctx.channel().remoteAddress();
    transceiver.handlerActive(remoteAddress, this);
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    logger.trace("channelInactive remote peer: {} disconnected", ctx.channel().remoteAddress());
    transceiver.handlerInActive(remoteAddress);
  }

  public void sendMessage(ProtoMessage message) {
    if (ctx != null && ctx.channel().isActive() && ctx.channel().isWritable()) {
      logger.trace("sendMessage {} to {} written to wire",message.getMessageType(),remoteAddress);
      ctx.writeAndFlush(message);
    }
    else {
      logger.warn("sendMessage called when channel not active or writable");
    }
  }

  public Long getId() {
    return handlerId;
  }

}
