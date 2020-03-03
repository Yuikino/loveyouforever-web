package untitymeow;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import untitymeow.handler.TestHandler;
import untitymeow.handler.TestHandlerUdp;
import untitymeow.model.Danmaku;
import untitymeow.retrofit.AddDanmaku;
import untitymeow.retrofit.ListDanmakusResult;
import untitymeow.retrofit.Result;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.UUID;

@Path("/loveyouforever")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LoveYouForeverResource {

    @Inject
    EntityManager entityManager;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    @Path("/listDanmakus/{scene}")
    @GET
    @Transactional
    public ListDanmakusResult listDanmakus(@PathParam("scene") String scene) {
        ListDanmakusResult result = new ListDanmakusResult();
        try {
            Query query = entityManager.createQuery("select danmaku from Danmaku danmaku " +
                    "where danmaku.scene = :scene " +
                    "order by danmaku.time")
                    .setParameter("scene", scene);
            List<Danmaku> list = query.getResultList();
            result.setList(list.toArray(new Danmaku[list.size()]));
            result.setStatus("ok");
        } catch (Exception e) {
            e.printStackTrace();
            result.setStatus("failed");
        }
        return result;
    }

    @Path("/addDanmaku")
    @POST
    @Transactional
    public Result addDanmaku(AddDanmaku addDanmaku) {
        Result result = new Result();
        try {
            Danmaku danmaku = new Danmaku();
            danmaku.setUuid(UUID.randomUUID().toString());
            danmaku.setScene(addDanmaku.scene);
            danmaku.setTime(addDanmaku.time);
            danmaku.setText(addDanmaku.text);
            entityManager.persist(danmaku);
            result.setStatus("ok");
        } catch (Exception e) {
            e.printStackTrace();
            result.setStatus("failed");
        }
        return result;
    }

    @Path("/startUdpServer")
    @GET
    public Result startUdpServer() {
        Result result = new Result();
        try {
            ChannelWrapper channelWrapper = new ChannelWrapper();
            bossGroup = new NioEventLoopGroup();
            Bootstrap b = new Bootstrap()
                    .group(bossGroup)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new ChannelInitializer() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new TestHandlerUdp(channelWrapper));
                        }
                    });
            ChannelFuture f = b.bind(2887).sync();
            Channel ch = f.channel();
            channelWrapper.setChannel(ch);
            result.setStatus("ok");
        } catch (Exception e) {
            e.printStackTrace();
            result.setStatus("failed");
        }
        return result;
    }

    @Path("/startTcpServer")
    @GET
    public Result startTcpServer() {
        Result result = new Result();
        try {
            bossGroup = new NioEventLoopGroup();
            workerGroup = new NioEventLoopGroup();
            ServerBootstrap b = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new LengthFieldBasedFrameDecoder(Short.MAX_VALUE, 0, 2, 0, 2))
                                    .addLast(new LengthFieldPrepender(2))
                                    .addLast(new TestHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture f = b.bind(9527).sync();
            result.setStatus("ok");
        } catch (Exception e) {
            e.printStackTrace();
            result.setStatus("failed");
        }
        return result;
    }

    @Path("/stopServer")
    @GET
    public Result stopServer() {
        Result result = new Result();
        try {
            if (bossGroup != null) {
                bossGroup.shutdownGracefully();
            }
            if (workerGroup != null) {
                workerGroup.shutdownGracefully();
            }
            result.setStatus("ok");
        } catch (Exception e) {
            e.printStackTrace();
            result.setStatus("failed");
        }
        return result;
    }
}
