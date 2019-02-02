import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;

public class MainTest {
    @Test
    public void test(){
        RedisUtil redisUtil = new RedisUtil(BaseConfig.IP,BaseConfig.port,BaseConfig.password);
        Jedis jedis = redisUtil.getJedis();
        String watch = jedis.watch("testabcd");
        System.out.println(Thread.currentThread().getName()+"--"+watch);
        Transaction multi = jedis.multi();
        multi.set("testabcd", "23432");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<Object> exec = multi.exec();
        System.out.println("---"+exec);
        jedis.unwatch();
    }
    @Test
    public void testWatch2(){
        RedisUtil redisUtil = new RedisUtil("118.31.18.2",7000,"ivancainiaowo");
        Jedis jedis = redisUtil.getJedis();
        jedis.set("testabcd1", "1254");
    }


    @Test
    public void testSubscribe(){
        RedisUtil redisUtil = new RedisUtil(BaseConfig.IP,BaseConfig.port,BaseConfig.password);
        RedisMsgPubSubListener pubsub = new RedisMsgPubSubListener();
        redisUtil.subscribeMsg(pubsub,"news");
        redisUtil.publishMsg("news","this is my ");
    }

    @Test
    public void testPublish(){
        RedisUtil redisUtil = new RedisUtil(BaseConfig.IP,BaseConfig.port,BaseConfig.password);
        redisUtil.publishMsg("news","miao shuai ");
    }

    @Test
    public void testBase(){
        RedisUtil redisUtil = new RedisUtil("118.31.18.2",6379,"ivancainiaowo");
    }

}
