package au.com.geekseat.service;

import au.com.geekseat.model.Product;
import au.com.geekseat.model.Shop;
import au.com.geekseat.security.Principal;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;

import static javax.ws.rs.core.Response.ok;

@ApplicationScoped
public class ProductService implements PanacheRepository<Product> {

    public Uni<List<Product>> checkoutProduct() {
        return findAll().list()
                .map(products -> {
                    products.forEach(product -> {
                        if (product.getQuantity() < 1) {
                            throw new RuntimeException(product.getName() + " is out of stock");
                        }
                        product.setQuantity(product.getQuantity() - 1);
                        persist(product);
                    });
                    return products;
                });
    }


    public Uni<Product> checkout(Shop shop) {
        return findById(shop.getId()).map(
                Unchecked.function(product -> {
                            if (product.getQuantity() < shop.getQuantity()) {
                                throw new RuntimeException(product.getName() + " is out of stock");
                            }
                            product.setQuantity(product.getQuantity() - shop.getQuantity());
                            return product;
                        }
                )
        );
    }

    public Uni<Response> update(Product product, Principal principal) {
        String query = "name = :name, map = :map, price = :price, quantity = :quantity, created = :created, createdBy = :createdBy, updated = :updated, updatedBy = :updatedBy " +
                "where id = :id";
        product.updatedBy(principal);
        return Panache.withTransaction(() -> update(query, params(product)))
                .map(integer -> ok(integer).build());
    }

    private HashMap<String, Object> params(Product entity) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", entity.getId());
        params.put("name", entity.getName());
        params.put("price", entity.getPrice());
        params.put("quantity", entity.getQuantity());
        params.put("created", entity.getCreated());
        params.put("createdBy", entity.getCreatedBy());
        params.put("updated", entity.getUpdated());
        params.put("updatedBy", entity.getUpdatedBy());
        return params;
    }
}
