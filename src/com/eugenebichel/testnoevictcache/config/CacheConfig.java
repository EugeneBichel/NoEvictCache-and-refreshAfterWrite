package com.eugenebichel.testnoevictcache.config;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@EnableCaching
@Configuration
public class CacheConfig extends CachingConfigurerSupport {

    @Bean
    public CacheManager noEvictCacheManager() {

        final int expireAfterWriteHours = 1;
        return new NoEvictCacheManager(expireAfterWriteHours);
    }

    @Bean
    public CacheManager expireAfterWriteCacheManager(final TestRepository testRepository) {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager() {

            CacheLoader cacheLoader = new CacheLoader<Object, Object>() {
                @Override
                public Object load(Object key) throws Exception {
                    return testRepository.get();
                }
            };

            @Override
            protected Cache createConcurrentMapCache(final String name) {
                return new ConcurrentMapCache(
                        name,
                        CacheBuilder.newBuilder()
                                .refreshAfterWrite(1, TimeUnit.HOURS)
                                .build(cacheLoader)
                                .asMap(),
                        true
                );
            }
        };

        return cacheManager;
    }
}
