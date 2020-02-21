package untitymeow.retrofit;

import untitymeow.model.Danmaku;

public class ListDanmakusResult extends Result {
    private Danmaku[] list;

    public Danmaku[] getList() {
        return list;
    }

    public void setList(Danmaku[] list) {
        this.list = list;
    }
}
