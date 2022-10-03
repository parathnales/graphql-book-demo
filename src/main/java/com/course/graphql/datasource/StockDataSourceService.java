package com.course.graphql.datasource;

import com.github.javafaker.Faker;
import com.netflix.dgs.codegen.generated.types.Stock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class StockDataSourceService {

    @Autowired
    private Faker faker;

    public Stock createStock() {
        return Stock.newBuilder()
                .name(this.faker.stock().nyseSymbol())
                .publishedDate(LocalDate.now())
                .price(this.faker.number().randomDigitNotZero())
                .build();
    }
}
