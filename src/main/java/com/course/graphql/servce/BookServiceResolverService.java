package com.course.graphql.servce;

import com.course.graphql.datasource.BookDataSourceService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.dgs.codegen.generated.DgsConstants;
import com.netflix.dgs.codegen.generated.types.Book;
import com.netflix.dgs.codegen.generated.types.ReleaseHistoryInput;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.InputArgument;
import graphql.schema.DataFetchingEnvironment;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@DgsComponent
public class BookServiceResolverService {

    private final BookDataSourceService bookDataSourceService;
    private final ObjectMapper objectMapper;

    public BookServiceResolverService(final BookDataSourceService bookDataSourceService,
                                      final ObjectMapper objectMapper) {
        this.bookDataSourceService = bookDataSourceService;
        this.objectMapper = objectMapper;
    }

    /**
     * If the query parameter specified in schema is not equal to the name of method in java class
     * Then use DGSData annotation
     *
     * If the attribute is optional in schema, then we can use the java.util.Optional as method argument
     * If the attribute name in schema and java class is different, then use the  InputArgument annotation
     *
     * @param authorName
     * @return
     */
    @DgsData(parentType = "Query", field = "books")
    public List<Book> getBookByAuthor(@InputArgument(name = "author") final Optional<String> authorName) {
        if(authorName.isEmpty() || StringUtils.isEmpty(authorName.get())) {
            return this.bookDataSourceService.getBooks();
        }

        return this.bookDataSourceService.getBooks().stream()
                .filter(book -> StringUtils.containsIgnoreCase(book.getAuthor().getName(), authorName.get()))
                .collect(Collectors.toList());
    }

    /**
     * Instead of DataFetchingEnvironment you can Directly use the InputArgument annotation with Optional class
     * @param dataFetchingEnvironment
     * @return
     * @throws IOException
     */
    @DgsData(
            parentType = DgsConstants.QUERY_TYPE,
            field = DgsConstants.QUERY.BooksByReleaseHistory
    )
    public List<Book> getBooksByReleaseHistory(DataFetchingEnvironment dataFetchingEnvironment) throws IOException {
        TypeReference<ReleaseHistoryInput> releaseHistoryInputType = new TypeReference<ReleaseHistoryInput>() {};
        var releaseHistoryInput = this.objectMapper.readValue(
                this.objectMapper.writeValueAsString(dataFetchingEnvironment.getArguments().get(DgsConstants.QUERY.BOOKSBYRELEASEHISTORY_INPUT_ARGUMENT.ReleaseHistoryInput)),
                releaseHistoryInputType);

        if(Objects.isNull(releaseHistoryInput)) {
            return Collections.emptyList();
        }
        return this.bookDataSourceService.getBooks().stream()
                .filter(book -> book.getReleased().stream()
                        .filter(releaseHistory -> releaseHistory.getPrintedEdition() == releaseHistoryInput.getPrintedEdition() &&
                                releaseHistory.getReleaseDate().isAfter(releaseHistoryInput.getAfterYear()))
                        .findAny()
                        .isPresent()
                ).collect(Collectors.toList());
    }
}
