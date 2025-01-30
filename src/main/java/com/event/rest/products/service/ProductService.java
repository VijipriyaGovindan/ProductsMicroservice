package com.event.rest.products.service;

import com.event.rest.products.model.ProductRequest;

public interface ProductService
{
    String createProduct(ProductRequest productRequest) throws Exception;
}
