package com.eugenebichel.testnoevictcache.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCache;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * NoEvictCache allows to keep values in cache storage if exception is thrown during updating expired cache keys
 */
public class NoEvictCache implements Cache {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ConcurrentMapCache concurrentMapCache;

    private final long WRITE_TIME_UNDEFINED = -1L;
    private final long expireAfterWriteHoursMillis;
    private volatile long writeTimeMillis = WRITE_TIME_UNDEFINED;

    public NoEvictCache(ConcurrentMapCache concurrentMapCache, int expireAfterWriteHours) {
        expireAfterWriteHoursMillis = TimeUnit.HOURS.toMillis(expireAfterWriteHours);
        this.concurrentMapCache = concurrentMapCache;
    }

    @Override
    public String getName() {
        return concurrentMapCache.getName();
    }

    @Override
    public Object getNativeCache() {
        return concurrentMapCache.getNativeCache();
    }

    @Override
    public ValueWrapper get(Object key) {
        if (isExpired()) {
            setWriteTime();
            logger.info("Records are expired in cache");
            return null;
        }

        return concurrentMapCache.get(key);
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        return concurrentMapCache.get(key, type);
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        return concurrentMapCache.get(key, valueLoader);
    }

    @Override
    public void put(Object key, Object value) {
        //if value is not null, then update value in cache
        if (value != null) {
            concurrentMapCache.put(key, value);
        }

        setWriteTime();
    }

    protected void setWriteTime() {
        writeTimeMillis = System.currentTimeMillis();
    }

    @Override
    public Cache.ValueWrapper putIfAbsent(Object key, Object value) {
        return concurrentMapCache.putIfAbsent(key, value);
    }

    @Override
    public void evict(Object key) {

    }

    @Override
    public void clear() {

    }

    private boolean isExpired() {
        return writeTimeMillis != WRITE_TIME_UNDEFINED
                && (System.currentTimeMillis() - writeTimeMillis) > expireAfterWriteHoursMillis;
    }
}

