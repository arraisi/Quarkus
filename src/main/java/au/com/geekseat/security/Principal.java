package au.com.geekseat.security;

import au.com.geekseat.helper.Utility;
import au.com.geekseat.model.Gender;

import javax.ws.rs.core.SecurityContext;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Principal implements java.security.Principal {

    private Long id;
    private String name;
    private String email;
    private Gender gender;
    private Object map;
    private Set<String> roles;
    private List<String> states;
    private boolean administrator;
    private String token;

    public static final Principal System = new Principal(null, null, null, null, null, null, null, false);

    public Principal(Long id, String name, String email, Object map, Gender gender, Set<String> roles, List<String> states, boolean administrator) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.map = map;
        this.roles = roles;
        this.states = states;
        this.administrator = administrator;
    }

    public Principal(Long id, String firstName, String lastName, String email, Object map, Gender gender, Set<String> roles, List<String> states, boolean administrator) {
        this.id = id;
        this.name = Utility.strip(Utility.stringify(firstName) + " " + Utility.stringify(lastName));
        this.email = email;
        this.gender = gender;
        this.map = map;
        this.roles = roles;
        this.states = states;
        this.administrator = administrator;
    }

    public Principal() {
    }

    public Principal(SecurityContext context) {
        Principal userPrincipal = (Principal) context.getUserPrincipal();
        this.id = userPrincipal.getId();
        this.name = userPrincipal.getName();
        this.email = userPrincipal.getEmail();
        this.gender = userPrincipal.getGender();
        this.map = userPrincipal.getMap();
        this.roles = userPrincipal.getRoles();
        this.states = userPrincipal.getStates();
        this.administrator = userPrincipal.isAdministrator();
    }

    public Principal essence() {
        return new Principal(this.id, this.name, this.email, this.map, this.gender, this.roles, this.states, this.administrator);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Object getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public List<String> getStates() {
        return states;
    }

    public void setStates(List<String> states) {
        this.states = states;
    }

    public boolean isAdministrator() {
        return administrator;
    }

    public void setAdministrator(boolean administrator) {
        this.administrator = administrator;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
