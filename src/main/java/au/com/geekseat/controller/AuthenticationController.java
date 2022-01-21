package au.com.geekseat.controller;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import au.com.geekseat.helper.JWTUtils;
import au.com.geekseat.helper.PasswordEncoder;
import au.com.geekseat.helper.Utility;
import au.com.geekseat.service.PersonService;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.*;

@Path("/auth")
@RequestScoped
public class AuthenticationController {

    private final PersonService personService;

    public AuthenticationController(PersonService personService,
                                    PasswordEncoder pwdEncoder,
                                    JWTUtils jwtUtils) {
        this.personService = personService;
    }

    @POST
    @Path("/login")
    @Produces({APPLICATION_JSON, APPLICATION_XML})
    @Consumes(APPLICATION_FORM_URLENCODED)
    public Uni<Response> personById(@FormParam("email") String email, @FormParam("password") String password) {
        return personService.findByUsername(email)
                .map(person -> {
                    if (person == null || !Utility.checkPassword(password, person.getPassword())) {
                        Log.warn("User not found or incorrect password");
                        return Response.status(Response.Status.UNAUTHORIZED);
                    }
                    if (!person.getActive()) {
                        Log.warn("Login Failed user is inactive");
                        return Response.status(Response.Status.UNAUTHORIZED);
                    }
                    try {
                        return Response.ok(JWTUtils.generate(person));
                    } catch (Exception e) {
                        Log.error("Error loggin in: {}");
                        return Response.status(Response.Status.BAD_REQUEST);
                    }
                }).map(Response.ResponseBuilder::build);
    }

}
