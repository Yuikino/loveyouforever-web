package untitymeow;

import io.netty.channel.Channel;

public class ChannelWrapper {
    private Channel channel;

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
