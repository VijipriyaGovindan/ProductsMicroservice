package com.event.rest.products.service;

import com.event.core.ProductCreatedEvent;
import com.event.rest.products.constants.ProductConstant;
import com.event.rest.products.model.ProductRequest;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * The type Product service.
 */
@Service
public class ProductServiceImpl implements ProductService
{
    private final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);

    /**
     * The Kafka template.
     */
    KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;

    /**
     * Instantiates a new Product service.
     *
     * @param kafkaTemplate the kafka template
     */
    ProductServiceImpl(KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate)
    {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * * Save the product in DB, gets the product id,
     * * Send the event to Kafka, wait sync for ack from Broker
     *
     * @param productRequest
     * @return
     * @throws Exception
     */
    @Override
    public String createProduct(ProductRequest productRequest) throws Exception
    {
        String productId = UUID.randomUUID().toString();
        //TODO : Persist product details into db before publishing
        ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent(productId, productRequest.getTitle(), productRequest.getPrice(), productRequest.getQuantity());

        ProducerRecord<String,ProductCreatedEvent>  producerRecord = new ProducerRecord<>(ProductConstant.TOPIC_NAME,productId,productCreatedEvent);
        producerRecord.headers().add("message_id",UUID.randomUUID().toString().getBytes());

        CompletableFuture<SendResult<String, ProductCreatedEvent>> completableFuture = kafkaTemplate.send(producerRecord);
        LOGGER.info("Before invoking kafka");

        completableFuture.whenComplete((result, exception) ->
        {
            if (exception != null)
            {
                LOGGER.error("Failed to send Message " + exception.getMessage());

            } else
            {
                LOGGER.info("Message sent successfully" + result.getRecordMetadata());
                LOGGER.info("Message details" + result.getRecordMetadata().partition() + result.getRecordMetadata().topic() + result.getRecordMetadata().offset());
            }
        }).get();


        LOGGER.info("Returning product id");
        return productId;
    }
}
