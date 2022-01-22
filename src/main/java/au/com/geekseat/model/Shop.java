package au.com.geekseat.model;

import au.com.geekseat.security.Principal;

import javax.persistence.*;

@Entity
@Table(name = "shop")
public class Shop extends BaseModel {
    private Integer quantity;
    private Boolean active = true;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;

    public Shop(Integer quantity, Boolean active, Product product, Person person) {
        this.quantity = quantity;
        this.active = active;
        this.product = product;
        this.person = person;
    }

    public Shop() {
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Shop updateMapper(Shop request, Principal principal) {
        this.updatedBy(principal);
        setPerson(request.getPerson());
        setProduct(request.getProduct());
        setActive(request.getActive());
        setQuantity(request.getQuantity());
        setMap(request.getMap());
        setTransitMap(request.getTransitMap());
        return this;
    }
}
