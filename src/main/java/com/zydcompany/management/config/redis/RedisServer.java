package com.zydcompany.management.config.redis;

import com.google.common.base.Strings;
import com.zydcompany.management.util.ManagementLogUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.exceptions.JedisException;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class RedisServer {

    private static final ManagementLogUtil log = ManagementLogUtil.getLogger();
    private Jedis jedis;

    private RedisServer(Jedis jedis) {
        this.jedis = jedis;
    }

    public static RedisServer redisServerInstance(String poolName) {
        RedisServer redisServer = new RedisServer(RedisPool.jedisInstance(poolName));
        return redisServer;
    }


    public void release(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

    public void setString(String key, String value, int cacheSeconds, String methodName) {
        try {
            jedis.set(key, value);
            if (cacheSeconds > 0) {
                jedis.expire(key, cacheSeconds);
            }
        } catch (JedisException e) {
            log.error("redis执行" + methodName + "失败", e);
        } finally {
            release(jedis);
        }
    }

    public void setIncr(String key, String methodName) {
        try {
            jedis.incr(key);
            log.info("redis执行" + methodName + "成功");
        } catch (JedisException e) {
            log.error("redis执行" + methodName + "失败", e);
        } finally {
            release(jedis);
        }
    }

    public void setDecr(String key, String methodName) {
        try {
            jedis.decr(key);
            log.info("redis执行" + methodName + "成功");
        } catch (JedisException e) {
            log.error("redis执行" + methodName + "失败", e);
        } finally {
            release(jedis);
        }
    }

    public void setDecrBy(String key, long integer, String methodName) {
        try {
            jedis.decrBy(key, integer);
            log.info("redis执行" + methodName + "成功");
        } catch (JedisException e) {
            log.error("redis执行" + methodName + "失败", e);
        } finally {
            release(jedis);
        }
    }

    public void setHashByField(String key, String field, String value, String methodName) {
        try {
            jedis.hset(key, field, value);
        } catch (JedisException e) {
            log.error("redis执行" + methodName + "失败", e);
        } finally {
            release(jedis);
        }
    }

    public void deleteJedisByKey(String key, String methodName) {
        try {
            jedis.del(key);
        } catch (Exception e) {
            log.error("redis执行" + methodName + "失败", e);
        } finally {
            release(jedis);
        }
    }

    public String addStringToJedis(String key, String value, int cacheSeconds,
                                   String methodName) {
        String lastVal = null;
        try {
            lastVal = jedis.set(key, value);
            if (cacheSeconds > 0) {
                jedis.expire(key, cacheSeconds);
            }
        } catch (Exception e) {
            log.error("addStringToJedis 错误:" + e);
        } finally {
            release(jedis);
        }
        return lastVal;
    }

    public String addToken(String key, String value) {
        String lastVal = null;
        try {
            lastVal = jedis.set(key, value);
        } catch (Exception e) {
            log.error("######login save token failed " + key + "--->" + value
                    , e);
        } finally {
            release(jedis);
        }
        return lastVal;
    }

    public String get(String key) {
        String lastVal = null;
        try {
            lastVal = jedis.get(key);
        } catch (Exception e) {
            log.error("######get failed key--->" + key + " value--->"
                    + lastVal, e);
        } finally {
            release(jedis);
        }
        return lastVal;
    }

    public void addStringToJedis(Map<String, String> batchData,
                                 int cacheSeconds, String methodName) {
        try {
            Pipeline pipeline = jedis.pipelined();
            for (Map.Entry<String, String> element : batchData.entrySet()) {
                if (cacheSeconds > 0) {
                    pipeline.setex(element.getKey(), cacheSeconds,
                            element.getValue());
                } else {
                    pipeline.set(element.getKey(), element.getValue());
                }
            }
            pipeline.sync();
        } catch (Exception e) {
            log.error("线程：" + Thread.currentThread().getId() + ">>>类:"
                    + this.getClass().getName() + ">>>方法:addStringToJedis"
                    + ">>>调用方法:" + methodName + "fail", e);
        } finally {
            release(jedis);
        }
    }

    public void addStringToListJedis(String key, String value,
                                     int cacheSeconds, String methodName) {
        try {
            jedis.rpush(key, value);
            if (cacheSeconds > 0) {
                jedis.expire(key, cacheSeconds);
            }
        } catch (Exception e) {
            log.error("addStringToListJedis异常!", e);
        } finally {
            release(jedis);
        }
    }

    public void addListToJedis(String key, List<String> list, int cacheSeconds,
                               String methodName) {
        try {
            if (list != null && list.size() > 0) {
                if (jedis.exists(key)) {
                    jedis.del(key);
                }
                for (String aList : list) {
                    jedis.rpush(key, aList);
                }
                if (cacheSeconds > 0) {
                    jedis.expire(key, cacheSeconds);
                }
            }

        } catch (JedisException e) {
            log.error("线程：" + Thread.currentThread().getId() + ">>>类:"
                    + this.getClass().getName() + ">>>方法:addListToJedis"
                    + ">>>调用方法:" + methodName + "fail", e);
        } catch (Exception e) {
            log.error("线程：" + Thread.currentThread().getId() + ">>>类:"
                    + this.getClass().getName() + ">>>方法:addListToJedis"
                    + ">>>调用方法:" + methodName + "fail", e);
        } finally {
            release(jedis);
        }
    }

    public void addToSetJedis(String key, String[] value, String methodName) {

        try {
            jedis.sadd(key, value);
        } catch (Exception e) {
            log.error("addToSetJedis异常!", e);
        } finally {
            release(jedis);
        }
    }

    public Long removeSetJedis(String key, String value, String methodName) {

        Long result = null;
        try {
            result = jedis.srem(key, value);
        } catch (Exception e) {
            log.error("removeSetJedis异常!", e);
        } finally {
            release(jedis);
        }

        return result;
    }

    public Long pushDataToListJedis(String key, String data, int cacheSeconds,
                                    String methodName) {
        Long result = null;
        try {
            result = jedis.rpush(key, data);
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
        } catch (Exception e) {
            log.error("pushDataToListJedis异常!key：" + key + ",data:" + data, e);
        } finally {
            release(jedis);
        }
        return result;
    }

    public Long pushDataToListJedis(String key, List<String> batchData,
                                    int cacheSeconds, String methodName) {
        Long result = null;
        try {
            jedis.del(key);
            result = jedis.lpush(key,
                    batchData.toArray(new String[batchData.size()]));
            if (cacheSeconds > 0)
                jedis.expire(key, cacheSeconds);
        } catch (Exception e) {
            log.error("pushDataToListJedis异常!", e);
        } finally {
            release(jedis);
        }
        return result;
    }


    public void deleteDataFromListJedis(String key, List<String> values,
                                        String methodName) {
        try {
            Pipeline pipeline = jedis.pipelined();
            if (values != null && !values.isEmpty()) {
                for (String val : values) {
                    pipeline.lrem(key, 0, val);
                }
            }
            pipeline.sync();
        } catch (Exception e) {
            log.error("deleteDataFromListJedis异常!", e);
        } finally {
            release(jedis);
        }
    }


    public void deleteStrFromListJedis(String key, String value,
                                       String methodName) {
        try {
            Pipeline pipeline = jedis.pipelined();
            if (value != null && !value.isEmpty()) {
                pipeline.lrem(key, 0, value);
            }
            pipeline.sync();
        } catch (Exception e) {
            log.error("deleteDataFromListJedis异常!", e);
        } finally {
            release(jedis);
        }
    }


    public Long deleteDataFromSetJedis(String key, String member,
                                       String methodName) {
        Long result = null;
        try {
            result = jedis.srem(key, member);
        } catch (Exception e) {
            log.error("deleteDataFromSetJedis异常!", e);
        } finally {
            release(jedis);
        }
        return result;
    }

    public String addHashMapToJedis(String key, Map<String, String> map,
                                    int cacheSeconds, String methodName) {
        String result = null;
        if (map != null && map.size() > 0) {
            try {
                result = jedis.hmset(key, map);
                if (cacheSeconds > 0)
                    jedis.expire(key, cacheSeconds);
            } catch (Exception e) {
                log.error("key：" + key + ",map:" + map + "addHashMapToJedis异常!", e);
            } finally {
                release(jedis);
            }
        }
        return result;
    }

    public Long addHashMapToJedis(String key, String field, String value,
                                  int cacheSeconds, String methodName) {
        Long result = 0L;
        try {
            result = jedis.hset(key, field, value);
            if (cacheSeconds > 0) {
                jedis.expire(key, cacheSeconds);
            }
        } catch (Exception e) {
            log.error("addHashMapToJedis异常!", e);
        } finally {
            release(jedis);
        }
        return result;
    }

    public boolean existKey4HashMap(String key, String field) {

        try {
            return jedis.hexists(key, field);
        } catch (Exception e) {
            log.error("existKey4HashMap异常!", e);
        } finally {
            release(jedis);
        }
        return false;
    }

    public Long deleteMapKeyToJedis(String key, String field) {

        Long result = null;
        try {
            result = jedis.hdel(key, field);
        } catch (Exception e) {
            log.error("deleteMapKeyToJedis异常!", e);
        } finally {
            release(jedis);
        }
        return result;

    }


    public void setHashByMap(String key, Map<String, String> map,
                             int cacheSeconds, String methodName) {
        try {
            if (map != null && map.size() > 0) {
                jedis.hmset(key, map);
                if (cacheSeconds > 0) {
                    jedis.expire(key, cacheSeconds);
                }
            }
        } catch (JedisException e) {
            log.error("setHashByMap:" + methodName + "失败", e);
        } finally {
            release(jedis);
        }
    }

    public Map<String, String> getHashMapByKey(String key, String methodName) {
        try {
            Map<String, String> valueMap = jedis.hgetAll(key);
            return valueMap;
        } catch (JedisException e) {
            log.error("redis执行" + methodName + "失败", e);
        } finally {
            release(jedis);
        }
        return null;
    }

    public void updateHashMapToJedis(String key, String incrementField,
                                     long incrementValue, String dateField, String dateValue,
                                     String methodName) {

        try {
            jedis.hincrBy(key, incrementField, incrementValue);
            jedis.hset(key, dateField, dateValue);
        } catch (Exception e) {
            log.error("updateHashMapToJedis异常!", e);
        } finally {
            release(jedis);
        }
    }

    public String getStringFromJedis(String key, String methodName) {
        String value = null;
        try {
            if (jedis.exists(key)) {
                value = jedis.get(key);
            }
        } catch (Exception e) {
            log.error("#####getStringFromJedis failed####", e);
        } finally {
            release(jedis);
        }
        return value;
    }

    public String getString(String key) {
        String value = null;
        try {
            if (jedis.exists(key)) {
                value = jedis.get(key);
                value = !Strings.isNullOrEmpty(value) && !"nil".equalsIgnoreCase(value) ? value : null;
            }
        } catch (Exception e) {
            log.error("getString异常!", e);
        } finally {
            release(jedis);
        }
        return value;
    }


    public List<String> getStringFromJedis(String[] keys, String methodName) {

        try {
            return jedis.mget(keys);
        } catch (Exception e) {
            log.error("getStringFromJedis异常!", e);
        } finally {
            release(jedis);
        }
        return null;
    }

    public List<String> getListFromJedis(String key, String methodName) {
        List<String> list = null;
        try {
            if (jedis.exists(key)) {
                list = jedis.lrange(key, 0, -1);
            }
        } catch (JedisException e) {
            log.error("getListFromJedis异常!", e);
        } finally {
            release(jedis);
        }
        return list;
    }

    public Set<String> getSetFromJedis(String key, String methodName) {
        Set<String> list = null;
        try {
            if (jedis.exists(key)) {
                list = jedis.smembers(key);
            }
        } catch (Exception e) {
            log.error("getSetFromJedis异常!", e);
        } finally {
            release(jedis);
        }
        return list;
    }

    public Map<String, String> getHashMapFromJedis(String key, String methodName) {
        Map<String, String> hashMap = null;
        try {
            hashMap = jedis.hgetAll(key);
        } catch (Exception e) {
            log.error("getHashMapFromJedis异常!", e);
        } finally {
            release(jedis);
        }
        return hashMap;
    }

    public String getHashMapValueFromJedis(String key, String field,
                                           String methodName) {
        String value = null;
        try {
            if (jedis.exists(key)) {
                value = jedis.hget(key, field);
            }
        } catch (Exception e) {
            log.error("getHashMapValueFromJedis异常key:" + key + ",field:" + field, e);
        } finally {
            release(jedis);
        }
        return value;
    }

    public List<String> getHashMapValueByFieldsFromJedis(String key,
                                                         String[] field, String methodName) {
        List<String> value = null;
        try {
            if (jedis.exists(key)) {
                value = jedis.hmget(key, field);
            }
        } catch (Exception e) {
            log.error("getHashMapValueByFieldsFromJedis异常!", e);
        } finally {
            release(jedis);
        }
        return value;
    }

    public Long getIdentifyId(String identifyName, String methodName) {
        Long identify = null;
        try {

            if (jedis != null) {
                identify = jedis.incr(identifyName);
                if (identify == 0) {
                    return jedis.incr(identifyName);
                } else {
                    return identify;
                }
            }
        } catch (Exception e) {
            log.error("getIdentifyId异常!", e);
        } finally {
            release(jedis);
        }
        return null;
    }

    public void flushDBFromJedis(int dbIndex) {
        try {
            jedis.select(dbIndex);
            jedis.flushDB();
        } catch (Exception e) {
            log.error("flushDBFromJedis异常!", e);
        } finally {
            release(jedis);
        }
    }

    public boolean existKey(String key, String methodName) {

        try {
            return jedis.exists(key);
        } catch (Exception e) {
            log.error("existKey异常!", e);
        } finally {
            release(jedis);
        }
        return false;
    }

    /**
     * 阻塞型抛出 这个列表的阻塞弹出原语，是rpop函数的阻塞版本当列表中没有任何元素可供弹出的时候，
     * brpop将会被阻塞，直到等待超时或发现可弹出元素，keys有多个参数时候， 服务器会依次检查各个列表，弹出第一个非空头元素，无超时设置
     *
     * @param timeout    被阻塞等待的时间以秒为单位
     * @param key        需要弹出元素的关键字
     * @param methodName
     * @author EX-TANGYE001
     */
    public List<String> brpop(int timeout, String key, String methodName) {
        List<String> list = null;

        try {
            list = jedis.brpop(timeout, key);
        } catch (Exception e) {
            log.error("brpop异常", e);
        } finally {
            release(jedis);
        }
        return list;
    }

    /**
     * 右抛出,移除并返回列表 key 的尾元素。
     *
     * @param key        需要弹出元素的关键字
     * @param methodName
     * @author EX-TANGYE001
     */
    public String rpop(String key, String methodName) {
        String str = null;
        try {
            if (jedis.exists(key)) {
                str = jedis.rpop(key);
            }
        } catch (Exception e) {
            log.error("rpop异常:", e);
        } finally {
            release(jedis);
        }
        return str;
    }

    /**
     * 有序set类型jedis 将一个或多个member元素及其score值加入到有序集key当中，如果某个member已经是有序集的成员，
     * 那么更新这个member的score值，并通过重新插入这个member 元素，来保证该member在正确的位置上。 score
     * 值可以是整数值或双精度浮点数。如果key不存在，则创建一个空的有序集并执行ZADD操作。 当key存在但不是有序集类型时，返回一个错误
     *
     * @param key        有序集合的关键字
     * @param score      有序集合的score值，排序的先后根据这个进行
     * @param member     有序集合的字符串值
     * @param methodName
     * @return 被成功添加的新成员的数量，不包括那些被更新的、已经存在的成员
     * @author EX-TANGYE001
     */
    public Long zadd(String key, double score, String member,
                     String methodName) {
        Long result = null;
        try {

            result = jedis.zadd(key, score, member);
        } catch (Exception e) {
            log.error("zadd异常!", e);
        } finally {
            release(jedis);
        }
        return result;
    }


    public Set<String> zrange(String key, long start, long end,
                              String methodName) {
        Set<String> result = null;
        try {
            result = jedis.zrange(key, start, end);
        } catch (Exception e) {
            log.error("zrange异常!", e);
        } finally {
            release(jedis);
        }
        return result;
    }

    /**
     * 返回有序集 key 中，指定区间内的成员其中成员的位置按 score 值递增(从大到小)来排序
     *
     * @param key        有序集合的关键字
     * @param start      集合中的开始位置
     * @param end        集合中的结束位置
     * @param methodName
     * @return 指定区间内的有序集成员
     * @author EX-TANGYE001
     */
    public Set<String> zrevrange(String key, long start, long end,
                                 String methodName) {
        Set<String> result = null;
        try {
            result = jedis.zrevrange(key, start, end);
        } catch (Exception e) {

            log.error("zrevrange异常:", e);
        } finally {
            release(jedis);
        }
        return result;
    }


    public Set<Tuple> zrevrangewithscore(String key, long start, long end,
                                         String methodName) {
        Set<Tuple> result = null;
        try {
            result = jedis.zrevrangeWithScores(key, start, end);
        } catch (Exception e) {
            log.error("zrevrangewithscore异常:", e);
        } finally {
            release(jedis);
        }
        return result;
    }


    public Set<String> zrangeByScore(String key, double minScore,
                                     double maxScore, String methodName) {
        Set<String> result = null;
        try {
            result = jedis.zrangeByScore(key, minScore, maxScore);
        } catch (Exception e) {
            log.error("zrangeByScore异常:", e);
        } finally {
            release(jedis);
        }
        return result;
    }


    public Long zrem(String key, String member, String methodName) {
        Long result = null;
        try {
            result = jedis.zrem(key, member);
        } catch (Exception e) {
            log.error("zrem异常:", e);
        } finally {
            release(jedis);
        }
        return result;
    }


    public Long scard(String key, String methodName) {

        Long result = null;
        try {
            result = jedis.scard(key);
        } catch (Exception e) {
            log.error("scard异常:", e);
        } finally {
            release(jedis);
        }
        return result;
    }


    public Set<String> sdiff(String methodName, final String... keys) {
        Set<String> list = null;
        try {
            list = jedis.sdiff(keys);
        } catch (Exception e) {
            log.error("sdiff异常:", e);
        } finally {
            release(jedis);
        }
        return list;
    }


    public Long expire(String key, int seconds, String methodName) {
        Long result = null;
        try {
            result = jedis.expire(key, seconds);
        } catch (Exception e) {
            log.error("expire异常:", e);
        } finally {
            release(jedis);
        }
        return result;
    }


    public Long sdiffstore(String dstkey, String methodName,
                           final String... keys) {
        Long result = null;
        try {
            result = jedis.sdiffstore(dstkey, keys);
        } catch (Exception e) {
            log.error("sdiffstore异常:", e);
        } finally {
            release(jedis);
        }
        return result;
    }


    public Boolean hexists(String key, String field, String methodName) {
        Boolean result = false;
        try {
            result = jedis.hexists(key, field);
        } catch (Exception e) {
            log.error("hexists异常:", e);
        } finally {
            release(jedis);
        }
        return result;
    }


    public String lindex(String key, long index, String methodName) {
        String result = "";
        try {
            result = jedis.lindex(key, index);
        } catch (Exception e) {
            log.error("lindex异常:", e);
        } finally {
            release(jedis);
        }
        return result;
    }


    public List<String> lrange(String key, long start, long end,
                               String methodName) {
        List<String> result = null;
        try {
            result = jedis.lrange(key, start, end);
        } catch (Exception e) {
            log.error("lrange异常:", e);
        } finally {
            release(jedis);
        }
        return result;
    }

    public Long llen(String key, String methodName) {
        Long result = 0L;
        try {
            result = jedis.llen(key);
        } catch (Exception e) {
            log.error("获取长度异常:", e);
        } finally {
            release(jedis);
        }
        return result;
    }

    public Long zrank(String key, String member) {
        Long result = 0L;
        try {
            result = jedis.zrank(key, member);
        } catch (Exception e) {
            log.error("zrank异常:", e);
        } finally {
            release(jedis);
        }
        return result;
    }


    public Long zrevrank(String key, String member) {
        Long result = 0L;
        try {
            result = jedis.zrevrank(key, member);
        } catch (Exception e) {
            log.error("zrevrank异常:", e);
        } finally {
            release(jedis);
        }
        return result;
    }


    public String zscore(String key, String member) {
        String result = "";
        try {
            result = jedis.zscore(key, member) + "";
        } catch (Exception e) {
            log.error("zscore异常:", e);
        } finally {
            release(jedis);
        }
        return result;
    }

    public Double zscoreOriDis(String key, String member) {
        Double result = null;
        try {
            result = jedis.zscore(key, member);
        } catch (Exception e) {
            log.error("zscore异常:", e);
        } finally {
            release(jedis);
        }
        return result;
    }

    public Long hdelHashMapToJedis(String key, String field) {
        Long returnNum = null;
        try {
            if (jedis != null) {
                returnNum = jedis.hdel(key, field);
            }
        } catch (Exception e) {

            log.error("hdelHashMapToJedis异常:", e);
        } finally {
            release(jedis);
        }

        return returnNum;
    }

    public long addToSetJedisForStringValue(String key, String value,
                                            String methodName) {
        Long returnNum = null;
        try {
            returnNum = jedis.sadd(key, value);
        } catch (Exception e) {
            log.error("addToSetJedisForStringValue异常:", e);
        } finally {
            release(jedis);
        }
        return returnNum;
    }

    public Long addToSetValue(String key, String value,
                              int cacheTime) {
        Long returnNum = null;
        try {
            returnNum = jedis.sadd(key, value);
            if (cacheTime > 0) {
                jedis.expire(key, cacheTime);
            }
        } catch (Exception e) {

            log.error("addToSetJedisForStringValue异常:", e);
        } finally {
            release(jedis);
        }

        return returnNum;
    }

    public boolean sismember(String key, String value) {
        boolean isMember = false;
        try {
            isMember = jedis.sismember(key, value);
        } catch (Exception e) {
            log.error("addToSetJedisForStringValue异常:", e);
        } finally {
            release(jedis);
        }

        return isMember;
    }

    public String spopUtils(String key, String methodName) {

        String result = null;
        try {
            result = jedis.spop(key);
        } catch (Exception e) {
            log.error("spopUtils异常:", e);
        } finally {
            release(jedis);
        }
        return result;
    }


    public String srandmemberUtils(String key, String methodName) {
        String result = null;
        try {
            result = jedis.srandmember(key);
        } catch (Exception e) {
            log.error("srandmemberUtils异常:", e);
        } finally {
            release(jedis);
        }
        return result;
    }

    public Long delUtils(String key, String methodName) {
        Long result = null;
        try {
            result = jedis.del(key);
        } catch (Exception e) {
            log.error("delUtils异常:", e);
        } finally {
            release(jedis);
        }
        return result;
    }

    public String setString(String key, String value) {

        String result = null;
        try {
            result = jedis.set(key, value);
        } catch (Exception e) {
            log.error("setString异常:", e);
        } finally {
            release(jedis);
        }
        return result;
    }

    public String lpopUtils(String key) {

        String result = null;
        try {
            result = jedis.lpop(key);
        } catch (Exception e) {
            log.error("lpopUtils异常:", e);
        } finally {
            release(jedis);
        }
        return result;
    }

    public String rpoplpush(String srckey, String dstkey) {

        String result = null;
        try {
            result = jedis.rpoplpush(srckey, dstkey);
        } catch (Exception e) {
            log.error("rpoplpush异常:", e);
        } finally {
            release(jedis);
        }
        return result;
    }

    public String getUtils(String key) {

        String result = null;
        try {
            result = jedis.get(key);
        } catch (Exception e) {
            log.error("getUtils异常:", e);
        } finally {
            release(jedis);
        }
        return result;
    }

    public Transaction getTransaction() {
        return jedis.multi();
    }

    public boolean tryLock(String key, int cacheSeconds) {
        boolean isLock = false;
        try {
            Long i = jedis.setnx(key, "");
            if (cacheSeconds > 0) {
                jedis.expire(key, cacheSeconds);
            }
            if (1 == i) {
                isLock = Boolean.TRUE;
            } else {
                isLock = Boolean.FALSE;
            }
        } catch (Exception e) {
            log.error("tryLock异常:key:" + key, e);
            isLock = Boolean.FALSE;
        } finally {
            release(jedis);
        }
        return isLock;
    }

    public Long unlock(String key) {

        Long result = null;
        try {
            result = jedis.del(key);
        } catch (Exception e) {

            log.error("unlock异常:key:" + key, e);
        } finally {
            release(jedis);
        }
        return result;
    }

    public Set<String> hkeys(String key, String methodName) {

        Set<String> result = null;
        try {
            result = jedis.hkeys(key);
        } catch (Exception e) {
            log.error("hkeys异常:", e);
        } finally {
            release(jedis);
        }
        return result;
    }
}