#### Test project for providing a cache, which can return expired data, when target call method throws exception 

##### We can use refreshAfterWrite for reading expired cache value if some unexpected behavior during calling for example testRepository.get() method. But load method tries to call testRepository.get each time until get returns new value

```
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
```

##### That is why I decided to write some cache, which will not to call testRepository.get each time when get throws exception of data requesting and call this cache as NoEvictCache 

```
@Bean
    public CacheManager noEvictCacheManager() {
        final int expireAfterWriteHours = 1;
        
        return new NoEvictCacheManager(expireAfterWriteHours);
    }
```

NoEvictCache implementation in `NoEvictCache.java` and in `NoEvictCacheManager.java` files