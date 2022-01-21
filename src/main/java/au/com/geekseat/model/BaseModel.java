package au.com.geekseat.model;

import au.com.geekseat.helper.MapConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import au.com.geekseat.helper.Utility;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

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

//    @JsonIgnore
//    @Column(name = "map_data")
//    protected String mapData;

//    @Transient
    @Convert(converter = MapConverter.class)
    private Object map = new HashMap<>();

    @Transient
    @JsonIgnore
    protected String transitMapData;

    @Transient
    protected Map<String, Object> transitMap = new HashMap<>();

    public void createdBy() {
        created = Utility.now();
//        setCreatorId(principal.getId());
//        setCreator(Utility.gson.toJson(principal.essence()));
    }

    public void updatedBy() {
        this.updated = Utility.now();
//        setEditorId(principal.getId());
//        setEditor(Utility.gson.toJson(principal.essence()));
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

//    public String getMapData() {
//        return mapData;
//    }
//
//    public void setMapData(String mapData) {
//        this.mapData = mapData;
//    }

    public Object getMap() {
        return map;
    }

    public void setMap(Object map) {
        this.map = map;
    }

    public String getTransitMapData() {
        return transitMapData;
    }

    public void setTransitMapData(String transitMapData) {
        this.transitMapData = transitMapData;
    }

    public Map<String, Object> getTransitMap() {
        return transitMap;
    }

    public void setTransitMap(Map<String, Object> transitMap) {
        this.transitMap = transitMap;
    }
}
