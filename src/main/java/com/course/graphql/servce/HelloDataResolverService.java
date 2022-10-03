package com.course.graphql.servce;

import com.course.graphql.datasource.HelloWorldDataSourceService;
import com.netflix.dgs.codegen.generated.types.Hello;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * In the DGSComponent we need to specify the methods with names similar to the names specified in graphQL schema
 * and annotate them with DGSQuery
 */

@DgsComponent
public class HelloDataResolverService {

    private HelloWorldDataSourceService helloWorldDataSourceService;

    @Autowired
    public HelloDataResolverService(final HelloWorldDataSourceService helloWorldDataSourceService) {
        this.helloWorldDataSourceService = helloWorldDataSourceService;
    }

    @DgsQuery
    public List<Hello> allHellos() {
        return this.helloWorldDataSourceService.getHellos();
    }

    @DgsQuery
    public Hello oneHello() {
        return this.helloWorldDataSourceService.getHellos().get(
                ThreadLocalRandom.current().nextInt(this.helloWorldDataSourceService.getHellos().size())
        );
    }
}
