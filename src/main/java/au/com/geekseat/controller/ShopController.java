package au.com.geekseat.controller;

import au.com.geekseat.helper.Utility;
import au.com.geekseat.model.Shop;
import au.com.geekseat.service.PocketService;
import au.com.geekseat.service.ShopService;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.quarkus.hibernate.reactive.panache.Panache.withTransaction;
import static javax.ws.rs.core.Response.*;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

@Path("/shop")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ShopController {
    @Inject
    ShopService shopService;
    @Inject
    PocketService pocketService;

    @GET
    @Path("/{id}")
    public Uni<Shop> shopById(Long id) {
        return shopService.findById(id);
    }

    @PUT
    @Path("/checkout")
    public Uni<Response> checkout(@Context SecurityContext context) {
        return withTransaction(() -> Uni.combine().all()
                .unis(
                        pocketService.updatePocket(Utility.getPrincipal(context)),
                        shopService.checkout(Utility.getPrincipal(context))
                ).asTuple())
                .map(objects -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("pocket", objects.getItem1());
                    map.put("shop", objects.getItem2());
                    return map;
                })
                .map(objects -> ok(objects).build())
                .onFailure()
                .recoverWithItem((e) -> serverError().entity(e.getMessage()).build());
    }

    @POST
    public Uni<Response> save(Shop shop, @Context SecurityContext ctx) {
        shop.createdBy(Utility.getPrincipal(ctx));
        return Panache.withTransaction(() -> shopService.persist(shop))
                .map(response -> created(URI.create("/person" + response.getId())).build());
    }

    @PUT
    @Path("/qty")
    public Uni<Response> updateQty(Shop shop, @Context SecurityContext ctx) {
        if (shop.getId() == null) {
            return Uni.createFrom().item(status(BAD_REQUEST))
                    .map(ResponseBuilder::build);
        }
        return Panache.withTransaction(() -> shopService.findById(shop.getId())
                .map(response -> response.updateMapper(shop, Utility.getPrincipal(ctx)))
                .map(integer -> ok(integer).build()));
    }

    @DELETE
    @Path("/{id}")
    public Uni<Response> delete(@PathParam("id") Long id) {
        return Panache.withTransaction(() -> shopService.deleteById(id)
                .map(deleted -> deleted ? noContent() : notModified())
                .map(ResponseBuilder::build));
    }

    @GET
    @Path("/list")
    public Uni<List<Shop>> list() {
        return shopService.listAll(Sort.by("id"));
    }

}
