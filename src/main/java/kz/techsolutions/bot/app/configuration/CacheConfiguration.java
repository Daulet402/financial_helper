package kz.techsolutions.bot.app.configuration;

import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.guava.GuavaCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.concurrent.TimeUnit;

@EnableCaching
@Configuration
public class CacheConfiguration {

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager simpleCacheManager = new SimpleCacheManager();
        simpleCacheManager.setCaches(Lists.newArrayList(
                allCategoriesCache(),
                allSubcategoriesCache(),
                textDtoCache(),
                textDtoListCache(),
                textDtoMapCache(),
                currenciesCache()
        ));
        return simpleCacheManager;
    }

    public GuavaCache textDtoListCache() {
        return new GuavaCache(
                "textDtoList",
                CacheBuilder.newBuilder()
                        .maximumSize(1)
                        .expireAfterWrite(48, TimeUnit.HOURS)
                        .build()
        );
    }

    public GuavaCache textDtoCache() {
        return new GuavaCache(
                "textDto",
                CacheBuilder.newBuilder()
                        .maximumSize(50)
                        .expireAfterWrite(48, TimeUnit.HOURS)
                        .build()
        );
    }

    public GuavaCache textDtoMapCache() {
        return new GuavaCache(
                "textDtoMap",
                CacheBuilder.newBuilder()
                        .maximumSize(1)
                        .expireAfterWrite(48, TimeUnit.HOURS)
                        .build()
        );
    }

    public GuavaCache allCategoriesCache() {
        return new GuavaCache(
                "allCategories",
                CacheBuilder.newBuilder()
                        .maximumSize(1)
                        .expireAfterWrite(48, TimeUnit.HOURS)
                        .build()
        );
    }

    public GuavaCache allSubcategoriesCache() {
        return new GuavaCache(
                "allSubcategories",
                CacheBuilder.newBuilder()
                        .maximumSize(1)
                        .expireAfterWrite(48, TimeUnit.HOURS)
                        .build()
        );
    }

    public GuavaCache currenciesCache() {
        return new GuavaCache(
                "currencies",
                CacheBuilder.newBuilder()
                        .maximumSize(1)
                        .expireAfterWrite(48, TimeUnit.HOURS)
                        .build()
        );
    }
}