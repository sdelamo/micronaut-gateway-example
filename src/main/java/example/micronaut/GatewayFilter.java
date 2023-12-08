package example.micronaut;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Filter;
import io.micronaut.http.annotation.ServerFilter;
import io.micronaut.http.client.ProxyHttpClient;
import io.micronaut.http.filter.HttpServerFilter;
import io.micronaut.http.filter.ServerFilterChain;
import org.reactivestreams.Publisher;

import java.net.URI;
import java.util.List;

@Filter(ServerFilter.MATCH_ALL_PATTERN)
class GatewayFilter implements HttpServerFilter {
    private final List<Route> routes;
    private final ProxyHttpClient proxyHttpClient;

    GatewayFilter(List<Route> routes, ProxyHttpClient proxyHttpClient) {
        this.routes = routes;
        this.proxyHttpClient = proxyHttpClient;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    @Override
    public Publisher<MutableHttpResponse<?>> doFilter(HttpRequest<?> request, ServerFilterChain chain) {
        for (Route route : routes) {
            if (matches(route, request)) {
                MutableHttpRequest<?> mutableHttpRequest = request.mutate();
                mutableHttpRequest.uri(URI.create(route.getUri()));
                return proxyHttpClient.proxy(mutableHttpRequest);
            }
        }
        return chain.proceed(request);
    }

    private boolean matches(@NonNull Route route, @NonNull HttpRequest<?> request) {
        for (Predicate predicate : route.getPredicates()) {
            if (matches(predicate, request)) {
                return true;
            }
        }
        return false;
    }

    private boolean matches(@NonNull Predicate predicate, @NonNull HttpRequest<?> request) {
        if (predicate instanceof ConfigurationPredicate configurationPredicate) {
            if (configurationPredicate.getPath() != null) {
                return request.getPath().startsWith(configurationPredicate.getPath());
            }
        }
        return false;
    }
}
