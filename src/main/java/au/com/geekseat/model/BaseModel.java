package au.com.geekseat.model;

import au.com.geekseat.helper.MapConverter;
import au.com.geekseat.security.Principal;
import com.fasterxml.jackson.annotation.JsonIgnore;
import au.com.geekseat.helper.Utility;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@MappedSuperclass
public class BaseModel {
    @Id
    @GeneratedValue
    private Long id;

    @JsonIgnore
    @Column(name = "created_by")
    @Size(max = 255)
    private String createdBy;

    @JsonIgnore
    @Column(name = "updated_by")
    @Size(max = 255)
    private String updatedBy;

    @JsonIgnore
    @Column(name = "created")
    private LocalDateTime created;

    @JsonIgnore
    @Column(name = "updated")
    private LocalDateTime updated;

    @Convert(converter = MapConverter.class)
    private Object map;

    @Convert(converter = MapConverter.class)
    protected Object transitMap;

    public void createdBy(Principal principal) {
        this.created = Utility.now();
        this.createdBy = principal.getName();
    }

    public void updatedBy(Principal principal) {
        this.updated = Utility.now();
        this.updatedBy = principal.getName();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }

    public Object getMap() {
        return map;
    }

    public void setMap(Object map) {
        this.map = map;
    }

    public Object getTransitMap() {
        return transitMap;
    }

    public void setTransitMap(Object transitMap) {
        this.transitMap = transitMap;
    }
}
