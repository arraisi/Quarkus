package au.com.geekseat.controller;

import au.com.geekseat.helper.Utility;
import au.com.geekseat.model.Person;
import au.com.geekseat.service.PersonService;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.net.URI;
import java.util.List;

import static javax.ws.rs.core.Response.ResponseBuilder;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static io.quarkus.panache.common.Sort.Direction.*;
import static javax.ws.rs.core.Response.*;

@RolesAllowed({"user", "admin"})
@Path("/person")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PersonController {

    PersonService personService;
    SecurityContext ctx;

    PersonController(PersonService personService, SecurityContext securityContext) {
        this.personService = personService;
        this.ctx = securityContext;
    }

    @POST
    public Uni<Response> save(Person person) {
        return Panache.withTransaction(() -> personService.save(person, Utility.getPrincipal(ctx)))
                .map(created -> created(URI.create("/person" + created.getId())).build());
    }

    @PUT
    public Uni<Response> update(Person person) {
        if (person.getId() == null) {
            return Uni.createFrom().item(status(BAD_REQUEST))
                    .map(ResponseBuilder::build);
        }
        person.updatedBy(Utility.getPrincipal(ctx));
        return Panache.withTransaction(() -> personService.update(person))
                .map(updated -> ok(updated).build());
    }

    @PUT
    @Path("map")
    public Uni<Response> updateMap(Person person) {
        if (person.getId() == null) {
            return Uni.createFrom().item(status(BAD_REQUEST))
                    .map(ResponseBuilder::build);
        }
        return personService.updateMap(person, Utility.getPrincipal(ctx))
                .map(updated -> ok(updated).build());
    }

    @GET
    @Path("/{id}")
    public Uni<Response> personById(Long id) {
        return personService.findById(id)
                .map(person -> person == null ? status(NOT_FOUND) : ok(person))
                .map(ResponseBuilder::build);
    }

    @GET
    @Path("/list")
    public Multi<Person> list() {
        return personService.listAll()
                .onItem().transformToMulti(row -> Multi.createFrom().iterable(row));
    }

    @GET
    @Path("/list/sort/name")
    public Uni<List<Person>> listSoreByName() {
        return personService.listAll(Sort.by("name"));
    }

    @GET
    @Path("/list/active")
    public Multi<Person> listActive() {
        return personService.list("active", true)
                .onItem().transformToMulti(row -> Multi.createFrom().iterable(row));
    }

    @DELETE
    @Path("/{id}")
    public Uni<Response> delete(@PathParam("id") Long id) {
        return Panache.withTransaction(() -> personService.deleteById(id)
                .map(deleted -> deleted ? noContent() : notModified())
                .map(ResponseBuilder::build));
    }

    @GET
    @Path("/datatables")
    public Uni<Response> datatables(
            @QueryParam("sortBy") String sortBy,
            @QueryParam("sortDesc") Boolean sortDesc,
            @QueryParam("pageIndex") Integer pageIndex,
            @QueryParam("pageSize") Integer pageSize) {
        return Panache.withTransaction(() -> Uni.combine().all()
                .unis(personService.findAll(Sort.by(sortBy, sortDesc ? Descending : Ascending)).page(pageIndex, pageSize).list()
                                .onItem().transformToMulti(persons -> Multi.createFrom().iterable(persons)).collect().asList(),
                        personService.count())
                .asTuple()
                .map(objects -> ok(objects).build()));
    }

    @GET
    @Path("/query/{id}")
    public Uni<Person> queryUniById(Long id) {
        return personService.queryFindById(id);
    }

    @GET
    @Path("/query/list")
    public Multi<Person> queryMultiListAll() {
        return personService.queryPersonMultiList();
    }

    @GET
    @Path("/query/uni/list")
    public Uni<List<Person>> queryUniListAll() {
        return personService.queryPersonUniList();
    }
}
