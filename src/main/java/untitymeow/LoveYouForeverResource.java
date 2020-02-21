package untitymeow;

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
}
