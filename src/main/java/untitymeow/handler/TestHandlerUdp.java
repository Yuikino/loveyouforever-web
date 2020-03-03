package untitymeow.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import untitymeow.ChannelWrapper;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class TestHandlerUdp extends SimpleChannelInboundHandler<DatagramPacket> {
    private Logger logger = Logger.getLogger("TestHandlerUdp");
    private ChannelWrapper channelWrapper;
    private static Set<InetSocketAddress> clients = new HashSet<>();

    public TestHandlerUdp(ChannelWrapper channelWrapper){
        this.channelWrapper = channelWrapper;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("channelActive");
    }

    // 加群封包
    // short opcode 0 代表加入群
    //
    // 群消息封包
    // short opcode 1 代表群信息
    // int 名字长度
    // String 名字
    // int 信息长度
    // String 信息
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        try {
            logger.info("channelRead0");
            InetSocketAddress sender = msg.sender();
            ByteBuf byteBuf = msg.content();
            short opcode = byteBuf.readShort();
            logger.info(Integer.toString(opcode));
            switch (opcode) {
                case 0:
                    clients.add(sender);
                    break;
                case 1:
                    for (InetSocketAddress client : clients) {
                        ByteBuf content = byteBuf.resetReaderIndex().copy();
                        DatagramPacket datagramPacket = new DatagramPacket(content, client);
                        channelWrapper.getChannel().writeAndFlush(datagramPacket);
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
