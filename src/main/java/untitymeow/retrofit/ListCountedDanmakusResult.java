package untitymeow.retrofit;

public class ListCountedDanmakusResult extends Result {
    private CountedDanmaku[] list;

    public CountedDanmaku[] getList() {
        return list;
    }

    public void setList(CountedDanmaku[] list) {
        this.list = list;
    }
}