package ca.uwaterloo.redynisdaemon.utils;

import ca.uwaterloo.redynisdaemon.exceptions.InternalAppError;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Set;

public class RedisHelper
{
    private static RedisHelper instance;
    private Jedis jedis;

    public static RedisHelper getInstance()
        throws InternalAppError
    {
        if (null == instance)
        {
            instance =
                new RedisHelper(
                    Options.getInstance().getAppConfig().getMetadataHost(),
                    Options.getInstance().getAppConfig().getMetadataPort()
                );
        }

        return instance;
    }

    private RedisHelper(String host, Integer port)
    {
        this.jedis = new Jedis(host, port);
    }

    public String getValue(String key)
    {
        return jedis.get(key);
    }

    public void setValue(String key, String value)
    {
        jedis.set(key, value);
    }

    public Set<String> getAllKeys()
    {
        return jedis.keys("*");
    }

    public List<String> multiGet(List<String> keys)
    {
        String[] keyArray = keys.toArray(new String[keys.size()]);
        return jedis.mget(keyArray);
    }
}
