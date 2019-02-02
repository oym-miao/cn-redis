import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;

/***
 * 事务测试
 */
public class TranscationTest {
    static final String host = "118.31.18.2";
    static final int port = 6379;
    static final String password = "ivancainiaowo";
    static String key = "ticket";  //购票

    static RedisUtil redisUtil;

    public static void main(String[] args) {
        redisUtil = new RedisUtil(host,port,password);
        Jedis jedis = redisUtil.getJedis();
        jedis.set(key, "1");

        //启动另一个thread，模拟另外的client
        new OtherClient().start();
        try {
            Thread.sleep(500);
            Transaction tx = jedis.multi();//开始事务
            //以下操作会集中提交服务器端处理，作为“原子操作”
            tx.incr(key);
            tx.incr(key);
            Thread.sleep(400);//此处Thread的暂停对事务中前后连续的操作并无影响，其他Thread的操作也无法执行
            tx.incr(key);
            Thread.sleep(300);//此处Thread的暂停对事务中前后连续的操作并无影响，其他Thread的操作也无法执行
            tx.incr(key);
            Thread.sleep(200);//此处Thread的暂停对事务中前后连续的操作并无影响，其他Thread的操作也无法执行
            tx.incr(key);
            List<Object> result = tx.exec();//提交执行
            //解析并打印出结果
            for(Object rt : result){
                System.out.println("Client 1 > 事务中> "+rt.toString());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        jedis.close();
    }


    static class OtherClient extends Thread{
        @Override
        public void run() {
            Jedis jedis = redisUtil.getJedis();
            jedis.set(key, "100");
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Client 2 > "+jedis.incr(key));
            }
            jedis.close();
        }
    }
}
