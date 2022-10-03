package com.course.graphql.datasource;

import com.github.javafaker.Faker;
import com.netflix.dgs.codegen.generated.types.Hello;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Component
public class HelloWorldDataSourceService {

    private final List<Hello> hellos = new ArrayList<>();

    private final Faker faker;

    @Autowired
    public HelloWorldDataSourceService(Faker faker) {
        this.faker = faker;
    }

    @PostConstruct
    public void postConstruct() {
        IntStream.range(0, 20)
                .forEach(counter -> {
                    var hello =Hello.newBuilder()
                            .randomNumber(this.faker.random().nextInt(5000))
                            .text(this.faker.company().name())
                            .build();
                    hellos.add(hello);
                });
    }

    public List<Hello> getHellos() {
        return this.hellos;
    }
}
