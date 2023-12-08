package example.micronaut;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.naming.Named;

import java.util.List;

public interface Route extends Named {

    @NonNull
    String getUri();

    @NonNull
    List<? extends Predicate> getPredicates();
}
