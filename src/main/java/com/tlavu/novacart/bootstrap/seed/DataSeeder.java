package com.tlavu.novacart.bootstrap.seed;

import com.tlavu.novacart.modules.catalog.application.usecase.CreateCategoryUseCase;
import com.tlavu.novacart.modules.catalog.application.usecase.CreateProductUseCase;
import com.tlavu.novacart.modules.catalog.domain.entity.Category;
import com.tlavu.novacart.modules.catalog.domain.entity.Product;
import com.tlavu.novacart.modules.catalog.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final CreateCategoryUseCase createCategoryUseCase;
    private final CreateProductUseCase createProductUseCase;

    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        try {
            Category electronics = createCategoryUseCase.execute("Electronics");
            Product iphone = createProductUseCase.execute(
                    "iPhone",
                    BigDecimal.valueOf(1234.56),
                    100,
                    electronics.getId()
            );

            log.info(
                    "Created category: {} ({})",
                    electronics.getName(),
                    electronics.getId()
            );

            log.info(
                    "Created product: {} ({})",
                    iphone.getName(),
                    iphone.getId()
            );

            Product product = productRepository.findById(iphone.getId())
                    .orElseThrow();

            log.info(
                    "Product: {}, Category: {}",
                    product.getName(),
                    product.getCategory().getName()
            );
        } catch (IllegalStateException e) {
            log.info("Seed data already exists: {}", e.getMessage());
        }
    }
}
