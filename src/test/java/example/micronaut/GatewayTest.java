package example.micronaut;

import io.micronaut.context.ApplicationContext;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.BlockingHttpClient;
import io.micronaut.http.client.HttpClient;
import io.micronaut.runtime.server.EmbeddedServer;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GatewayTest {

    @Test
    void gateway() {
        EmbeddedServer grailsServer = ApplicationContext.run(EmbeddedServer.class, Collections.singletonMap("framework", "grails"));
        EmbeddedServer micronautServer = ApplicationContext.run(EmbeddedServer.class, Collections.singletonMap("framework", "micronaut"));
        Map<String, Object> configuration = Map.of(
                "micronaut.gateway.routes.micronaut.uri", micronautServer.getURL().toString(),
                "micronaut.gateway.routes.micronaut.predicates[0].path", "/micronaut/books",
                "micronaut.gateway.routes.grails.uri", micronautServer.getURL().toString(),
                "micronaut.gateway.routes.grails.predicates[0].path", "/grails/books"
        );
        EmbeddedServer server = ApplicationContext.run(EmbeddedServer.class, configuration);
        Collection<Route> routes = server.getApplicationContext().getBeansOfType(Route.class);
        Optional<List<Map<String, String>>> property = server.getApplicationContext().getProperty("micronaut.gateway.routes.micronaut.predicates", Argument.listOf(Argument.mapOf(String.class, String.class)));
        assertNotNull(property);
        assertTrue(property.isPresent());
        assertEquals(1, property.get().size());
        assertEquals("/micronaut/books", property.get().get(0).get("path"));
        assertEquals(2, routes.size());
        assertTrue(routes.stream().anyMatch(route -> route.getName().equals("micronaut")));
        assertTrue(routes.stream().anyMatch(route -> route.getName().equals("micronaut")));
        assertTrue(routes.stream().allMatch(route -> route.getPredicates() != null));
        assertTrue(routes.stream().allMatch(route -> route.getPredicates() != null && route.getPredicates().size() == 1));
        assertTrue(routes.stream().allMatch(route -> route.getPredicates() != null && route.getPredicates().size() == 1 && ((ConfigurationPredicate) route.getPredicates().get(0)).getPath() != null));
        HttpClient httpClient = server.getApplicationContext().createBean(HttpClient.class, server.getURL());
        BlockingHttpClient client = httpClient.toBlocking();
        List<Book> books = assertDoesNotThrow(() -> client.retrieve(HttpRequest.GET("/micronaut/books"), Argument.listOf(Book.class)));
        assertEquals(2, books.size());

        books = assertDoesNotThrow(() -> client.retrieve(HttpRequest.GET("/grails/books"), Argument.listOf(Book.class)));
        assertEquals(6, books.size());

        server.close();
        grailsServer.close();
        micronautServer.close();
    }
}
