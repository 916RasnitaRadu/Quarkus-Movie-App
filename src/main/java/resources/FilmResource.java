package resources;

import io.quarkus.vertx.http.runtime.security.annotation.HttpAuthenticationMechanism;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import model.Film;
import repository.FilmRepository;

import java.awt.*;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("/film")
public class FilmResource {

    @Inject
    FilmRepository filmRepository;

    @GET
    @Path("/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getFilm(@PathParam("id") Short id) {
        Optional<Film> film = filmRepository.getFilm(id);

        return film.isPresent() ? film.get().getTitle() : "No film was found";
    }

    @GET
    @Path("/pagedFilms/{page}/{minLength}")
    @Produces(MediaType.TEXT_PLAIN)
    public String paged(@PathParam("page") long page, @PathParam("minLength") short minLength) {
        return filmRepository.paged(page, minLength)
                .map(f -> String.format("%s (%d min)", f.getTitle(), f.getLength()))
                .collect(Collectors.joining("\n"));
    }

    @GET
    @Path("/actorFilms/{startsWith}/{minLength}")
    @Produces(MediaType.TEXT_PLAIN)
    public String filmsWithActors(@PathParam("startsWith") String startsWith, @PathParam("minLength") Short minLength) {
        return filmRepository.filmWithActors(startsWith, minLength)
                .map(f -> String.format("%s (%d min): %s",
                        f.getTitle(),
                        f.getLength(),
                        f.getActors().stream().map(a -> String.format("%s %s", a.getFirstName(), a.getLastName()))
                                .collect(Collectors.joining(", "))
                        ))
                .collect(Collectors.joining("\n"));
    }

    @PATCH
    @Path("/update/{minLength}/{rentalRate}")
    @Produces(MediaType.TEXT_PLAIN)
    public String updateRentalRate(@PathParam("rentalRate") BigDecimal rentalRate, @PathParam("minLength") Short minLength) {
        filmRepository.updateRentalRate(minLength, rentalRate);
        return filmRepository.getAllFilms(minLength)
                .map(f -> String.format("%s (%d min): $%f",
                        f.getTitle(),
                        f.getLength(),
                        f.getRentalRate()
                ))
                .collect(Collectors.joining("\n"));
    }
}
