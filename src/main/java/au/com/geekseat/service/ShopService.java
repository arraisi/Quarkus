package au.com.geekseat.service;

import au.com.geekseat.model.Product;
import au.com.geekseat.model.Shop;
import au.com.geekseat.security.Principal;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ShopService implements PanacheRepository<Shop> {

    @Inject
    ProductService productService;

    public Uni<List<Shop>> checkout(Principal person) {
        return find("person_id", person.getId()).list()
                .chain(shops -> {
                    List<Uni<Product>> productList = new ArrayList<>();
                    for (Shop shop : shops) {
                        if (!shop.getActive()) {
                            return Uni.createFrom().failure(() -> new Exception("Inactive invoice"));
                        }
                        // product
                        productList.add(productService.checkout(shop));
                        // shop
                        shop.setActive(false);
                        updateStatus(shop);
                    }
                    // Now join the result. The productService will be called concurrently
                    return Uni.join().all(productList).andFailFast()
                            // fail fast stops after the first failure.
                            // replace the result with the list of shops as in the question
                            .replaceWith(shops);
                });
    }

    public Uni<Integer> updateQty(Shop shop) {
        shop.updatedBy();
        return Panache.withTransaction(() -> update("quantity = ?1 where id = ?2", shop.getQuantity(), shop.getId()));
    }

    public Uni<Shop> updateStatus(Shop shop) {
        shop.updatedBy();
        return Panache.withTransaction(() ->
                update("active = ?1 where id = ?2", shop.getActive(), shop.getId()).replaceWith(shop)
        );
    }
}
