import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPubSub;

public class RedisMsgPubSubListener extends JedisPubSub {
    private static Logger logger = LoggerFactory.getLogger(RedisMsgPubSubListener.class);


    /**
     * 取得订阅的消息后的处理
     */
    @Override
    public void onMessage(String channel, String message) {
        logger.error("onMessage: channel["+channel+"], message["+message+"]");

        //如果消息类型与定义的审计专用类型一致
        if("news".equalsIgnoreCase(channel)){
            //处理审计的消息
            this.AuditMsgHandler(message);
        }
    }
    /**
     * 取得按表达式的方式订阅的消息后的处理
     */
    @Override
    public void onPMessage(String pattern, String channel, String message) {
        logger.debug("onPMessage: channel["+channel+"], message["+message+"]");

    }
    /**
     * 初始化订阅时候的处理
     */
    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
        logger.debug("onSubscribe: channel["+channel+"],"+
                "subscribedChannels["+subscribedChannels+"]");

    }
    /**
     * 取消订阅时候的处理
     */
    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {
        logger.debug("onUnsubscribe: channel["+channel+"], "+
                "subscribedChannels["+subscribedChannels+"]");

    }
    /**
     * 取消按表达式的方式订阅时候的处理
     */
    @Override
    public void onPUnsubscribe(String pattern, int subscribedChannels) {
        logger.debug("onPUnsubscribe: pattern["+pattern+"],"+
                "subscribedChannels["+subscribedChannels+"]");

    }

    /**
     * 初始化按表达式的方式订阅时候的处理
     */
    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {
        logger.debug("onPSubscribe: pattern["+pattern+"], "+
                "subscribedChannels["+subscribedChannels+"]");

    }

    /**
     * 私有方法：用于处理审计的消息
     */
    private void AuditMsgHandler(String message){
        //审计日志反序列化 String -> byte[] -> RedisMsgAuditInfo
        logger.debug(message);
        //TODO 后续怎么存先不写...
    }

}