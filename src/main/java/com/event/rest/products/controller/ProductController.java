package com.event.rest.products.controller;

import com.event.rest.products.exception.ErrorMessage;
import com.event.rest.products.model.ProductRequest;
import com.event.rest.products.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * The type Product controller.
 */
@RestController
@RequestMapping("/products")
public class ProductController
{

    private final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    /**
     * The Product service.
     */
    ProductService productService;

    /**
     * Instantiates a new Product controller.
     *
     * @param productService the product service
     */
    public ProductController(ProductService productService)
    {
        this.productService = productService;
    }

    /**
     * Create product response entity.
     * Save the product in DB, gets the product id,
     * Send the event to Kafka, wait sync for ack from Broker
     *
     * @param productRequest the product request
     * @return the response entity
     */
    @PostMapping
    public ResponseEntity<Object> createProduct(@RequestBody ProductRequest productRequest)
    {
        String productid = null;
        try
        {
            productid = productService.createProduct(productRequest);
        } catch (Exception e)
        {
            LOGGER.error("Error in controller", e);
            ErrorMessage errorMessage = new ErrorMessage(new Date(), e.getMessage(), "/products");
            return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);

        }
        return new ResponseEntity<>(productid, HttpStatus.CREATED);
    }
}
