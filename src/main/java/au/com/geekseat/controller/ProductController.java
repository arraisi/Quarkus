package au.com.geekseat.controller;

import au.com.geekseat.model.Product;
import au.com.geekseat.service.ProductService;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.net.URI;
import java.util.List;

import static io.quarkus.panache.common.Sort.Direction.Ascending;
import static io.quarkus.panache.common.Sort.Direction.Descending;
import static javax.ws.rs.core.Response.*;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

@Path("/product")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductController {

    @Inject
    ProductService productService;

    @GET
    @Path("/datatables")
    public Uni<Response> datatables(
            @QueryParam("sortBy") String sortBy,
            @QueryParam("sortDesc") Boolean sortDesc,
            @QueryParam("pageIndex") Integer pageIndex,
            @QueryParam("pageSize") Integer pageSize) {
        return Panache.withTransaction(() -> Uni.combine().all()
                .unis(productService.findAll(Sort.by(sortBy, sortDesc ? Descending : Ascending)).page(pageIndex, pageSize).list()
                                .onItem().transformToMulti(persons -> Multi.createFrom().iterable(persons)).collect().asList(),
                        productService.count())
                .asTuple()
                .map(objects -> ok(objects).build()));
    }

    @POST
    public Uni<Response> save(Product product) {
        product.createdBy();
        return Panache.withTransaction(() -> productService.persist(product))
                .map(person -> created(URI.create("/person" + person.getId())).build());
    }

    @PUT
    public Uni<Response> update(Product product) {
        if (product.getId() == null) {
            return Uni.createFrom().item(status(BAD_REQUEST))
                    .map(Response.ResponseBuilder::build);
        }
        return productService.update(product);
    }

    @DELETE
    @Path("/{id}")
    public Uni<Response> delete(@PathParam("id") Long id) {
        return Panache.withTransaction(() -> productService.deleteById(id)
                .map(deleted -> deleted ? noContent() : notModified())
                .map(ResponseBuilder::build));
    }

    @GET
    @Path("/list")
    public Uni<List<Product>> list() {
        return productService.listAll(Sort.by("id"));
    }
}
