public class RedissonManager {
//    private static final String RAtomicName = "genId_";
//
//    private static Config config = new Config();
//    private static Redisson redisson = null;
//
//    public static void init(){
//        try {
//            config.useClusterServers() //这是用的集群server
//                    .setScanInterval(2000) //设置集群状态扫描时间
//                    .setMasterConnectionPoolSize(10000) //设置连接数
//                    .setSlaveConnectionPoolSize(10000)
//                    .addNodeAddress("127.0.0.1:6379","127.0.0.1:6380");
//            redisson = Redisson.create(config);
//            //清空自增的ID数字
//            RAtomicLong atomicLong = redisson.getAtomicLong(RAtomicName);
//            atomicLong.set(1);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//
//    public static Redisson getRedisson(){
//        return redisson;
//    }
//
//    /** 获取redis中的原子ID */
//    public static Long nextID(){
//        RAtomicLong atomicLong = getRedisson().getAtomicLong(RAtomicName);
//        atomicLong.incrementAndGet();
//        return atomicLong.get();
//    }
}
