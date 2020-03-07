package untitymeow;

import untitymeow.model.Danmaku;
import untitymeow.retrofit.*;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
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

    @Path("/listCountedDanmakus/{scene}")
    @GET
    @Transactional
    public ListCountedDanmakusResult listCountedDanmakus(@PathParam("scene") String scene) {
        ListCountedDanmakusResult result = new ListCountedDanmakusResult();
        try {
            Query query = entityManager.createQuery("select danmaku.text, count(danmaku.uuid) from Danmaku danmaku " +
                    "where danmaku.scene = :scene " +
                    "group by danmaku.text")
                    .setParameter("scene", scene);
            List<Object[]> list0 = query.getResultList();
            List<CountedDanmaku> list = new ArrayList<>();
            for (Object[] objects : list0) {
                CountedDanmaku countedDanmaku = new CountedDanmaku();
                countedDanmaku.text = (String) objects[0];
                countedDanmaku.count = ((Number) objects[1]).intValue();
                list.add(countedDanmaku);
            }
            result.setList(list.toArray(new CountedDanmaku[list.size()]));
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
            if (WordUtil.getStringSearch().ContainsAny(addDanmaku.text)) {
                StringBuilder str = new StringBuilder("text contains sensitive word: ");
                for (String s : WordUtil.getStringSearch().FindAll(addDanmaku.text)) {
                    str.append(s);
                    str.append(",");
                }
                result.setStatus(str.toString());
            } else {
                Danmaku danmaku = new Danmaku();
                danmaku.setUuid(UUID.randomUUID().toString());
                danmaku.setScene(addDanmaku.scene);
                danmaku.setTime(addDanmaku.time);
                danmaku.setText(addDanmaku.text);
                entityManager.persist(danmaku);
                result.setStatus("ok");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setStatus("failed");
        }
        return result;
    }

    @Path("/removeDanmaku")
    @POST
    @Transactional
    public Result removeDanmaku(RemoveDanmaku removeDanmaku) {
        Result result = new Result();
        try {
            if (removeDanmaku.password.equals("123456")) {
                Query query = entityManager.createQuery("select danmaku from Danmaku danmaku " +
                        "where danmaku.scene = :scene " +
                        "and danmaku.text = :text")
                        .setParameter("scene", removeDanmaku.scene)
                        .setParameter("text", removeDanmaku.text);
                List<Danmaku> list0 = query.getResultList();
                for (Danmaku danmaku : list0) {
                    entityManager.remove(danmaku);
                }
                result.setStatus("ok");
            } else {
                result.setStatus("password error");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setStatus("failed");
        }
        return result;
    }

    @Path("/clearDanmakus")
    @POST
    @Transactional
    public Result clearDanmakus(ClearDanmakus removeDanmaku) {
        Result result = new Result();
        try {
            if (removeDanmaku.password.equals("123456")) {
                Query query = entityManager.createQuery("select danmaku from Danmaku danmaku " +
                        "where danmaku.scene = :scene")
                        .setParameter("scene", removeDanmaku.scene);
                List<Danmaku> list0 = query.getResultList();
                for (Danmaku danmaku : list0) {
                    entityManager.remove(danmaku);
                }
                result.setStatus("ok");
            } else {
                result.setStatus("password error");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setStatus("failed");
        }
        return result;
    }
}
