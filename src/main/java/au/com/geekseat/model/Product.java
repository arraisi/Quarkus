package au.com.geekseat.model;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "product")
public class Product extends BaseModel {
    private String name;
    private BigDecimal price;
    private Integer quantity;

    public Product(String name, BigDecimal price, Integer quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public Product() {
    }

    public Product updateMapper(Product request) {
        setPrice(request.getPrice());
        setName(request.getName());
        setQuantity(request.getQuantity());
        setMap(request.getMap());
        setUpdatedBy(request.getUpdatedBy());
        setUpdated(request.getUpdated());
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
