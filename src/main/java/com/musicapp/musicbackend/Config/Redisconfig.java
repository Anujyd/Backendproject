package com.musicapp.musicbackend.Config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
@EnableCaching
@Configuration
public class Redisconfig {
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory, ObjectMapper objectMapper) {
        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new FluxRedisSerializer(objectMapper)));
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(cacheConfiguration)
                .transactionAware()
                .build();
    }

    @Bean
    public FluxRedisSerializer fluxRedisSerializer(ObjectMapper objectMapper) {
        return new FluxRedisSerializer(objectMapper);
    }

//    public class FluxRedisSerializer implements RedisSerializer<Object> {
//
//        private final ObjectMapper objectMapper;
//
//        public FluxRedisSerializer(ObjectMapper objectMapper) {
//            this.objectMapper = objectMapper;
//        }
//
//        @Override
//        public byte[] serialize(Object object) throws SerializationException {
//            try {
//                if (object instanceof Flux) {
//                    List<?> list = ((Flux<?>) object).collectList().block();
//                    return objectMapper.writeValueAsBytes(list);
//                } else if (object instanceof Mono) {
//                    Object value = ((Mono<?>) object).block();
//                    return objectMapper.writeValueAsBytes(value);
//                }
//                throw new SerializationException("Unsupported data type: " + object.getClass().getName());
//            } catch (Exception e) {
//                throw new SerializationException("Error serializing object", e);
//            }
//        }
//
//        @Override
//        public Object deserialize(byte[] bytes) throws SerializationException {
//            try {
//                if (bytes != null && bytes.length > 0) {
//                    return objectMapper.readValue(bytes, List.class);
//                }
//                return null;
//            } catch (Exception e) {
//                throw new SerializationException("Error deserializing object", e);
//            }
//        }
//    }
public class FluxRedisSerializer implements RedisSerializer<Object> {

    private final ObjectMapper objectMapper;

    public FluxRedisSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public byte[] serialize(Object object) throws SerializationException {
        try {
            if (object instanceof Flux) {
                List<?> list = ((Flux<?>) object).collectList().block();
                return objectMapper.writeValueAsBytes(list);
            } else if (object instanceof Mono) {
                Object value = ((Mono<?>) object).block();
                return objectMapper.writeValueAsBytes(value);
            }
            throw new SerializationException("Unsupported data type: " + object.getClass().getName());
        } catch (Exception e) {
            throw new SerializationException("Error serializing object", e);
        }
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        try {
            if (bytes != null && bytes.length > 0) {
                return objectMapper.readValue(bytes, List.class);
            }
            return null;
        } catch (Exception e) {
            throw new SerializationException("Error deserializing object", e);
        }
    }
}
}
//    public class FluxRedisSerializer implements RedisSerializer<Flux<?>> {
//
//        private final ObjectMapper objectMapper;
//
//        public FluxRedisSerializer(ObjectMapper objectMapper) {
//            this.objectMapper = objectMapper;
//        }
//
//        @Override
//        public byte[] serialize(Flux<?> flux) throws SerializationException {
//            try {
//                List<?> list = flux.collectList().block();
//                return objectMapper.writeValueAsBytes(list);
//            } catch (Exception e) {
//                throw new SerializationException("Error serializing Flux", e);
//            }
//        }
//
//        @Override
//        public Flux<?> deserialize(byte[] bytes) throws SerializationException {
//            try {
//                if (bytes != null) {
//                    List<?> list = objectMapper.readValue(bytes, List.class);
//                    return Flux.fromIterable(list);
//                }
//                return Flux.empty();
//            } catch (Exception e) {
//                throw new SerializationException("Error deserializing Flux", e);
//            }
//        }
//    }



//    public class FluxRedisSerializer implements RedisSerializer<Flux<?>> {
//
//        private final ObjectMapper objectMapper;
//
//        public FluxRedisSerializer(ObjectMapper objectMapper) {
//            this.objectMapper = objectMapper;
//        }
//
//        @Override
//        public byte[] serialize(Flux<?> flux) throws SerializationException {
//            try {
//                return objectMapper.writeValueAsBytes(flux.collectList().block());
//            } catch (Exception e) {
//                throw new SerializationException("Error serializing Flux", e);
//            }
//        }
//
//        @Override
//        public Flux<?> deserialize(byte[] bytes) throws SerializationException {
//            try {
//                if (bytes != null) {
//                    List<?> list = objectMapper.readValue(bytes, List.class);
//                    return Flux.fromIterable(list);
//                }
//                return null;
//            } catch (Exception e) {
//                throw new SerializationException("Error deserializing Flux", e);
//            }
//        }
//    }

