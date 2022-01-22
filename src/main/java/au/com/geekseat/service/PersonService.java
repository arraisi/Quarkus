package au.com.geekseat.service;

import au.com.geekseat.model.Person;
import au.com.geekseat.security.Principal;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class PersonService implements PanacheRepository<Person> {

    @Inject
    MySQLPool client;

    public Uni<Long> rowCount() {
        return client.query("SELECT count(*) as rowCount FROM Person").execute()
                .map(RowSet::iterator)
                .map(row -> row.next().getLong("rowCount"));
    }

    public Uni<Person> findByUsername(String userName) {
        return find("email", userName).firstResult();
    }

    public Uni<Boolean> update(Person person) {
        return client.preparedQuery("UPDATE Person SET " +
                        "name = ?, " +
                        "birth = ?, " +
                        "map = ?, " +
                        "updated = ?, " +
                        "updated_by = ? " +
                        "WHERE id = ?")
                .execute(Tuple.from(new Object[]{
                        person.getName(),
                        person.getBirth(),
                        person.getMap(),
                        person.getUpdated(),
                        person.getUpdatedBy(),
                        person.getId()}))
                .onItem().transform(rowSet -> rowSet.rowCount() == 1);
    }

    public Uni<Boolean> queryDelete(Long id) {
        return client.preparedQuery("DELETE FROM Fruit WHERE id = ?").execute(Tuple.of(id))
                .onItem().transform(rowSet -> rowSet.rowCount() == 1);
    }

    public Uni<Person> queryFindById(Long id) {
        return client.preparedQuery("select id, active, birth, email, map, name, password from Person where id = ?")
                .execute(Tuple.of(id))
                .onItem().transform(RowSet::iterator)
                .onItem().transform(iterator -> iterator.hasNext() ? from(iterator.next()) : null);
    }

    public Multi<Person> queryPersonMultiList() {
        return client.query("SELECT id, active, birth, email, map, name, password FROM Person ORDER BY name ASC").execute()
                .onItem().transformToMulti(rows -> Multi.createFrom().iterable(rows))
                .onItem().transform(PersonService::from);
    }

    public Uni<List<Person>> queryPersonUniList() {
        return client.query("SELECT id, active, birth, email, map, name, password FROM Person ORDER BY name ASC").execute()
                .onItem().transformToMulti(rows -> rows.iterator().toMulti())
                .onItem().transform(PersonService::from).collect().asList();
    }

    private static Person from(Row row) {
        Person person = new Person();
        person.setId(row.getLong("id"));
        person.setName(row.getString("name"));
        person.setBirth(row.getLocalDate("birth"));
        person.setEmail(row.getString("email"));
        person.setPassword(row.getString("password"));
        person.setActive(row.getBoolean("active"));
        return person;
    }

    public Uni<Integer> updateMap(Person person, Principal principal) {
        person.updatedBy(principal);
        return Panache.withTransaction(() -> update("map = ?1 where id = ?2", person.getMap(), person.getId()));
    }
}
