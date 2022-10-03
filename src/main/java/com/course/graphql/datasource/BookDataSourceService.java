package com.course.graphql.datasource;

import com.github.javafaker.Faker;
import com.netflix.dgs.codegen.generated.types.Address;
import com.netflix.dgs.codegen.generated.types.Author;
import com.netflix.dgs.codegen.generated.types.Book;
import com.netflix.dgs.codegen.generated.types.ReleaseHistory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class BookDataSourceService {

    private final List<Book> books = new ArrayList<>();

    private final Faker faker;

    public BookDataSourceService(final Faker faker) {
        this.faker = faker;
    }

    @PostConstruct
    public void postConstruct() {
        IntStream.range(0, 20)
                .mapToObj(this.createBook())
                .forEach(books::add);
    }

    private IntFunction<Book> createBook() {
        return (counter) -> {
            var author = Author.newBuilder()
                    .name(this.faker.book().author())
                    .addresses(this.createAddresses(counter))
                    .originCountry(this.faker.country().name())
                    .build();
            return Book.newBuilder()
                    .bookId(UUID.randomUUID().toString())
                    .title(this.faker.book().title())
                    .author(author)
                    .randomNumber(this.faker.number().randomDigit())
                    .publisher(this.faker.book().publisher())
                    .released(this.createReleaseHistory(counter))
                    .downloaded(this.faker.number().numberBetween(100, 10000))
                    .build();
        };
    }

    private List<Address> createAddresses(int counter) {
        return IntStream.<Address>range(0, 5)
                .mapToObj(i -> Address.newBuilder()
                            .city(this.faker.address().city())
                            .street(this.faker.address().streetAddress())
                            .country(this.faker.address().country())
                            .zipCode(this.faker.address().zipCode())
                            .build())
                .collect(Collectors.toList());
    }

    private List<ReleaseHistory> createReleaseHistory(int counter) {
        return IntStream.range(0, 2)
                .mapToObj(i -> {
                    try {
                        return ReleaseHistory.newBuilder()
                                .releasedCountry(this.faker.country().name())
                                .printedEdition(true)
                                .releaseDate(LocalDate.now())
                                .publishedOn(new URL("https://"+this.faker.internet().url()))
                                .build();
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

    }

    public List<Book> getBooks() {
        return this.books;
    }
}
