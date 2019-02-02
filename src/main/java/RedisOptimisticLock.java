
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/***
 * 乐观锁 redis 秒杀业务
 */
public class RedisOptimisticLock {
    RedisUtil redisUtil;

   @Test
    public void test() {
        redisUtil = new RedisUtil("118.31.18.2",6379,"ivancainiaowo");
        getProductData();
        getClients();
        printResult();
    }

    /***
     * 获取商品信息
     */
    private  void getProductData() {
        int prdNum = 100;
        String key = "prdNum";
        String clientList = "clientList"; //抢购到商品的用户
        Jedis jedis = redisUtil.getJedis();

        if (jedis.exists(key)) {
            jedis.del(key);
        }

        if (jedis.exists(clientList)) {
            jedis.del(clientList);
        }

        jedis.set(key, String.valueOf(prdNum));
        redisUtil.releaseResource(jedis);
    }

    /**
     * 获取用户信息
     */
    public  void getClients() {
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        int clientNum = 10000;// 模拟客户数目
        for (int i = 0; i < clientNum; i++) {
            cachedThreadPool.execute(new ClientThread(i));
        }
        cachedThreadPool.shutdown();
        while(true){
            if(cachedThreadPool.isTerminated()){
                System.out.println("所有的线程都结束了！");
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public void printResult() {
        Jedis jedis = redisUtil.getJedis();
        Set<String> set = jedis.smembers("clientList");

        int i = 1;
        for (String value : set) {
            System.out.println("第" + i++ + "个抢到商品，"+value + " ");
        }

        redisUtil.releaseResource(jedis);
    }


    private class ClientThread implements Runnable {
        Jedis jedis = null;
        String key = "prdNum";// 商品主键
        String clientList = "clientList";//// 抢购到商品的顾客列表主键
        String clientName;


        public ClientThread(int i) {
            clientName = "编号=" + i;
        }

        @Override
        public void run() {
            try {
                Thread.sleep((int)(Math.random()*5000));
            } catch (InterruptedException e1) {
            }
            while (true) {
                System.out.println("顾客:" + clientName + "开始抢商品");
                jedis = redisUtil.getJedis();
                try {
                    jedis.watch(key);
                    int prdNum = Integer.parseInt(jedis.get(key));// 当前商品个数
                    if (prdNum > 0) {
                        Transaction transaction = jedis.multi();
                        transaction.set(key, String.valueOf(prdNum - 1));
                        List<Object> result = transaction.exec();
                        if (result == null || result.isEmpty()) {
                            System.out.println("悲剧了，顾客:" + clientName + "没有抢到商品");// 可能是watch-key被外部修改，或者是数据操作被驳回
                        } else {
                            jedis.sadd(clientList, clientName);// 抢到商品记录一下
                            System.out.println("好高兴，顾客:" + clientName + "抢到商品");
                            break;
                        }
                    } else {
                        System.out.println("悲剧了，库存为0，顾客:" + clientName + "没有抢到商品");
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    jedis.unwatch();
                    redisUtil.releaseResource(jedis);
                }

            }
        }
    }
}
