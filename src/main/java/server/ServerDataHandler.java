package server;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import protobuf.JdssAuditor;
import protobuf.ProtobufMessage;

public class ServerDataHandler extends SimpleChannelInboundHandler<JdssAuditor.DisplayData> {

  private ChannelHandlerContext ctx;
  private Server server;
  private ServerDataHandler handler;
  private String clientAddress;
  private final Logger logger = LoggerFactory.getLogger("server.ServerDataHandler");
  private final static ProtobufMessage.ProtobufData heartbeat =
      ProtobufMessage.ProtobufData.newBuilder().setDataString("HeartBeat").build();

  public ServerDataHandler(Server server) {
    this.server = server;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, JdssAuditor.DisplayData msg) throws Exception {
    logger.info("channelRead0 {} sent: {}", clientAddress, msg.toString());

  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    this.ctx = ctx;
    InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
    InetAddress inetaddress = socketAddress.getAddress();
    clientAddress =
        inetaddress.getHostName() == null ? ctx.channel().remoteAddress().toString() : inetaddress.getHostName();
    logger.info("channelActive connection made from {}", clientAddress);
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    logger.info("channelInactive > connection to {} closed", clientAddress);

  }

  public void sendheartBeat() {
    if (ctx.channel().isWritable()) {
      logger.debug("sendheartBeat > sending... {} ", heartbeat);
      ctx.writeAndFlush(heartbeat);
    }
  }
  
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    logger.warn("Exception in connection from {} cause {}", clientAddress, cause.toString());
      ctx.close();
  }

}
