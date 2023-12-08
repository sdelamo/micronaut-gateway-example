package example.micronaut;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.EachProperty;
import io.micronaut.context.annotation.Parameter;
import io.micronaut.core.annotation.Nullable;

import java.util.List;

@EachProperty("micronaut.gateway.routes")
public class RouteConfiguration implements Route {

    private final String name;
    private String uri;

    private List<ConfigurationPredicateProperties> predicates;

    public RouteConfiguration(@Parameter String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public List<ConfigurationPredicateProperties> getPredicates() {
        return predicates;
    }

    public void setPredicates(List<ConfigurationPredicateProperties> predicates) {
        this.predicates = predicates;
    }

    @ConfigurationProperties("predicates")
    public static class ConfigurationPredicateProperties implements ConfigurationPredicate {
        private String path;

        @Override
        @Nullable
        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }
}
