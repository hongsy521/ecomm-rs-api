package com.hongsy.ecommrsapi.util;

import com.hongsy.ecommrsapi.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CountScheduler {
    private final ProductService productService;

    /*@Scheduled(cron = "0 0/10 * * * *")
    public void runSynchronizeLikeCounts(){
        productService.synchronizeLikeCounts();
    }*/

}
