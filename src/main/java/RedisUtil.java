import redis.clients.jedis.*;

/***
 * 利用jedis来操作redis server
 */
public class RedisUtil {
    private static JedisPool pool = null;


    /**
     * <p>传入ip和端口号构建redis 连接池</p>
     * @param ip ip
     * @param port 端口
     * @param auth 密码
     */
    public RedisUtil(String ip, int port,String auth) {
        if (pool == null) {
            JedisPoolConfig config = new JedisPoolConfig();
            // 控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；
            // 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
            config.setMaxTotal(500);
            // 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
            config.setMaxIdle(5);
            // 表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
            config.setMaxWaitMillis(1000 * 100);
            // 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
            config.setTestOnBorrow(true);
            // pool = new JedisPool(config, "192.168.0.121", 6379, 100000);
            pool = new JedisPool(config, ip, port, 100000,auth);

        }
    }


    public synchronized Jedis getJedis(){
        //连接池中获取资源
        try{
            if (pool != null){
                Jedis jedis = pool.getResource();
                return jedis;
            }else {
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    //释放jedis和jedisPool资源
    public  void releaseResource(final Jedis jedis){
        if (jedis == null){
            jedis.close();
        }
        if (pool == null){
            pool.close();
        }
    }

    //-------------------常用方法的自行封装-----------------------------------------------------

    /**
     * 发布一个消息
     * @param channel
     * @param message
     */
    public void publishMsg(String channel,String message){
        Jedis jedis = null;
        try {
            if (pool != null){
                jedis = pool.getResource();
                jedis.publish(channel, message);
            }
        } catch (Exception e) {
        } finally {
            releaseResource(jedis);
        }
    }

    /**
     * 订阅一格消息
     * @param jedisPubSub
     * @param channels
     */
    public void subscribeMsg(JedisPubSub jedisPubSub, String channels){
        Jedis jedis = null;
        try {
            if (pool != null) {
                jedis = pool.getResource();
                jedis.subscribe(jedisPubSub, channels);
            }
        }catch (Exception e) {
        } finally {
            releaseResource(jedis);
        }
    }

//-------------set hset sadd



}
