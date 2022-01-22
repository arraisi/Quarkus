package au.com.geekseat.service;

import au.com.geekseat.model.Product;
import au.com.geekseat.model.Shop;
import au.com.geekseat.security.Principal;
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
                    List<Uni<Product>> products = new ArrayList<>();
                    for (Shop shop : shops) {
                        if (!shop.getActive()) {
                            return Uni.createFrom().failure(() -> new Exception("Inactive invoice"));
                        }
                        // product
                        products.add(productService.checkout(shop));
                        // shop
                        shop.setActive(false);
                    }
                    return Uni.join().all(products).andFailFast()
                            .replaceWith(shops);
                });
    }
}
