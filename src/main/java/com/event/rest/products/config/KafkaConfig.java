package com.event.rest.products.config;

import com.event.core.ProductCreatedEvent;
import com.event.rest.products.constants.ProductConstant;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * The type Kafka config.
 */
@Configuration
public class KafkaConfig
{
    @Value("${spring.kafka.producer.bootstrap-servers}")
    private String bootStrapServer;

    @Value("${spring.kafka.producer.key-serializer}")
    private String keySerializer;

    @Value("${spring.kafka.producer.value-serializer}")
    private String valueSerializer;

    @Value("${spring.kafka.producer.acks}")
    private String acks;

    @Value("${spring.kafka.producer.properties.delivery.timeout.ms}")
    private String deliveryTimeout;

    @Value("${spring.kafka.producer.properties.linger.ms}")
    private String linger;

    @Value("${spring.kafka.producer.properties.request.timeout.ms}")
    private String requestTimeout;

    @Value("${spring.kafka.producer.properties.enable.idempotence}")
    private boolean idempotence;

    @Value("${spring.kafka.producer.properties.max.in.flight.requests.per.connection}")
    private Integer inflightRequest;

    /**
     * Producer config map.
     *
     * @return the map
     */
    Map<String, Object> producerConfig()
    {
        Map<String, Object> configMap = new HashMap<>();
        configMap.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServer);
        configMap.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
        configMap.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);
        configMap.put(ProducerConfig.ACKS_CONFIG, acks);
        configMap.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, deliveryTimeout);
        configMap.put(ProducerConfig.LINGER_MS_CONFIG, linger);
        configMap.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, requestTimeout);
        configMap.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, idempotence);
        configMap.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, inflightRequest);

        return configMap;
    }

    /**
     * Producer factory
     *
     * @return the producer factory
     */
    @Bean
    ProducerFactory<String, ProductCreatedEvent> producerFactory()
    {
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    /**
     * Kafka template
     *
     * @return the kafka template
     */
    @Bean
    KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate()
    {
        return new KafkaTemplate<String, ProductCreatedEvent>(producerFactory());
    }

    /**
     * Create topic new topic.
     *
     * @return the new topic
     */
    @Bean
    NewTopic createTopic()
    {
        return TopicBuilder.name(ProductConstant.TOPIC_NAME).partitions(3)
                .replicas(3).configs(Map.of("min.insync.replicas", "2")).build();
    }

}
