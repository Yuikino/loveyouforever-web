package untitymeow.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.logging.Logger;

public class TestHandler extends SimpleChannelInboundHandler<ByteBuf> {
    Logger logger = Logger.getLogger("TestHandler");
    static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("channelActive");
        channelGroup.add(ctx.channel());
    }

    // short opcode 1 代表群信息
    // int 名字长度
    // String 名字
    // int 信息长度
    // String 信息
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        logger.info("channelRead0");
        short opcode = msg.readShort();
        logger.info(Integer.toString(opcode));
        switch (opcode){
            case 1:
                ByteBuf content = msg.resetReaderIndex().copy();
                channelGroup.writeAndFlush(content);
                break;
        }
    }
}
