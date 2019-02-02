import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import java.util.ArrayList;
import java.util.List;

public class RedisShardPoolUtils {
    private static ShardedJedisPool pool = null;

    private static void getSharedPool(){
        if(pool == null){
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(2);
            config.setMaxIdle(5);
            config.setMaxWaitMillis(10000);
            config.setTestOnBorrow(true);
            config.setTestOnReturn(true);

            String hostA = "118.31.18.2";
            int portA = 6379;

            String hostB = "118.31.18.2";
            int portB = 7000;

            List<JedisShardInfo> shards = new ArrayList<>();
            JedisShardInfo infoA = new JedisShardInfo(hostA, portA);
            JedisShardInfo infoB = new JedisShardInfo(hostB, portB);

            shards.add(infoA);
            shards.add(infoB);

            pool = new ShardedJedisPool(config, shards);
        }
    }


    public static void main(String[] args) {
        getSharedPool();
        ShardedJedis jedis = null;
        try{
            jedis = pool.getResource();
            for(int i = 0; i < 10; i++){
                String key = "n" + i;
                System.out.println(jedis.set(key, i+""));
            }
        }
        catch (Exception e) {
            e.printStackTrace();

        }
        finally{
            pool.returnResource(jedis);

        }

    }
}

