package au.com.geekseat.service;

import au.com.geekseat.model.Pocket;
import au.com.geekseat.security.Principal;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;

import javax.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;

@ApplicationScoped
public class PocketService implements PanacheRepository<Pocket> {
    public Uni<Pocket> updatePocket(Principal principal) {
        return find("person_id", principal.getId())
                .singleResult()
                .map(Unchecked.function(pocket -> {
                    if (pocket.getBalance().compareTo(new BigDecimal(100)) < 0) {
                        throw new Exception("Balance is not enough");
                    }
                    pocket.setBalance(pocket.getBalance().subtract(new BigDecimal(100)));
                    return pocket;
                }));
    }
}
