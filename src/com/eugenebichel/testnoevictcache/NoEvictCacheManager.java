package com.eugenebichel.testnoevictcache.config;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class NoEvictCacheManager implements CacheManager {
    private final ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap<>(16);
    private final int expireAfterWriteHours;

    public NoEvictCacheManager(final int expireAfterWriteHours) {
        this.expireAfterWriteHours = expireAfterWriteHours;
    }

    @Override
    public Cache getCache(final String name) {
        return cacheMap.computeIfAbsent(name, this::createCache);
    }

    private Cache createCache(String name) {
        final ConcurrentMapCache concurrentMapCache = new ConcurrentMapCache(name, new ConcurrentHashMap<>(256), false) {
            @Override
            public void clear(){ }

            @Override
            public void evict(Object key) { }
        };
        return new NoEvictCache(concurrentMapCache, expireAfterWriteHours);
    }

    @Override
    public Collection<String> getCacheNames() {
        return Collections.unmodifiableSet(this.cacheMap.keySet());
    }
}

