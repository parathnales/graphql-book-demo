package com.course.graphql.servce;

import com.course.graphql.datasource.StockDataSourceService;
import com.netflix.dgs.codegen.generated.types.Stock;
import com.netflix.graphql.dgs.DgsComponent;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;

import java.time.Duration;

@DgsComponent
public class StockSubscriptionResolverService {

    @Autowired
    private StockDataSourceService dataSourceService;

    public Publisher<Stock> randomStock() {
        return Flux.interval(Duration.ofSeconds(3)).map(t -> this.dataSourceService.createStock());
    }
}
