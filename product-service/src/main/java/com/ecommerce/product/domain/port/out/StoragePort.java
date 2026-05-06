package com.ecommerce.product.domain.port.out;

import org.springframework.web.multipart.MultipartFile;

public interface StoragePort {
    String store(MultipartFile file);
}