package client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class ClientConnector {

	private String host;
	private int port;
	private EventLoopGroup clientEventLoop;
	private Bootstrap clientBootstrap;
	private ChannelFuture channelFuture;
	private Channel channel;
	private boolean connected = false;

	private static final long RETRY_TIME = 10L; // 10 secs
	private static final long MAX_RETRY_TIME = 60L; // 1 min
	private static final int maxRetriesBeforeIncrease = 30; // 5 mins until
															// connection
															// interval is
															// changed - 6 tries
															// a minute 6/30 = 5
	private int retryCount = 0;

	private final Logger logger = LoggerFactory.getLogger("client.ClientConnector");

	public ClientConnector(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void configureConnection() {
		clientEventLoop = new NioEventLoopGroup();
		clientBootstrap = new Bootstrap();

		clientBootstrap.group(clientEventLoop)
		.channel(NioSocketChannel.class)
		.handler(new ClientChannelHandler(this))
		.option(ChannelOption.TCP_NODELAY, true)
		.option(ChannelOption.SO_KEEPALIVE, true);

	}

	public void connect() {
		channelFuture = clientBootstrap.connect(host, port);
		channelFuture.addListener(new ClientConnectionListener(this, calculateRetryTime()));
		channel = channelFuture.channel();
	}

	public synchronized boolean isConnected() {
		return connected;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	/**
	 * @return Will return the time in milliseconds. Returns the
	 *         {@code RETRY_TIME} for the specified {@code retryCount}. After
	 *         this limit is reached it will then only return the time specified
	 *         with {@code MAX_RETRY_TIME}
	 * 
	 */
	private long calculateRetryTime() {

		if (retryCount >= maxRetriesBeforeIncrease) {
			logger.debug("calculateRetryTime {}>={} setting {} as retry interval: total time retrying {} seconds",
					retryCount, maxRetriesBeforeIncrease, MAX_RETRY_TIME,
					((retryCount - maxRetriesBeforeIncrease) * MAX_RETRY_TIME)
							+ (maxRetriesBeforeIncrease * RETRY_TIME));
			retryCount++;
			return MAX_RETRY_TIME;
		} else {
			logger.debug(
					"calculateRetryTime {}<{} setting {} seconds as retry interval: total time retrying {} seconds",
					retryCount, maxRetriesBeforeIncrease, RETRY_TIME, RETRY_TIME * retryCount);
			retryCount++;
			return RETRY_TIME;
		}
	}

	public void resetRetryCount() {
		retryCount = 0;
	}

	public synchronized void setConnection(boolean connection) {
		this.connected = connection;
	}

	public void runAsTest() throws InterruptedException {

		try {
			logger.debug("runAsTest > Client connector running ");
			configureConnection();
			connect();
			while (true) {
				if (connected) {

					int count = 1;
					while (connected) {
						ClientDataHandler handler = channel.pipeline().get(ClientDataHandler.class);
						handler.sendData(count);
						count++;
						Thread.sleep(1000);
					}
				}
				Thread.sleep(300);
			}

		} finally {
			clientEventLoop.shutdownGracefully();
		}
	}

	public static void main(String... args) {

		try {
			new ClientConnector("localhost", 26002).runAsTest();
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

}
