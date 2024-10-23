package org.bebra.soalab2movie.repository;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.bebra.soacommons.model.enums.MovieGenre;
import org.bebra.soalab2movie.model.entity.Movie;

import java.util.List;
import java.util.Optional;


@Transactional
@Stateless
public class MovieRepository {

    @PersistenceContext(unitName = "MovieSource")
    private EntityManager entityManager;

    public Optional<Movie> findById(Integer id) {
        return Optional.ofNullable(entityManager.find(Movie.class, id));
    }

    public List<Movie> findAll(int page, int size, List<String> sortParams) {
        String orderByClause = buildOrderByClause(sortParams);

        System.out.println(orderByClause);
        String jpql = "SELECT m FROM Movie m " + orderByClause;

        return entityManager.createQuery(jpql, Movie.class)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    public long countTotalMovies() {
        String jpql = "SELECT COUNT(m) FROM Movie m";
        return entityManager.createQuery(jpql, Long.class).getSingleResult();
    }

    public Optional<Movie> findTopByOrderByUsaBoxOfficeAsc() {
        return entityManager.createQuery("SELECT m FROM Movie m ORDER BY m.usaBoxOffice ASC", Movie.class)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }

    public int countAllByTagline(String tagline) {
        return ((Long) entityManager.createQuery("SELECT COUNT(m) FROM Movie m WHERE m.tagline = :tagline")
                .setParameter("tagline", tagline)
                .getSingleResult()).intValue();
    }

    public List<Movie> findByGenreLessThan(MovieGenre genre, int page, int size, List<String> sortParams) {
        String orderByClause = buildOrderByClause(sortParams);

        String jpql = "SELECT m FROM Movie m WHERE m.genre < :genre " + orderByClause;

        return entityManager.createQuery(jpql, Movie.class)
                .setParameter("genre", genre)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    public long countTotalMoviesGenreLessThen(MovieGenre genre) {
        String jpql = "SELECT COUNT(m) FROM Movie m WHERE m.genre < :genre";
        return entityManager.createQuery(jpql, Long.class)
                .setParameter("genre", genre)
                .getSingleResult();
    }

    public void save(Movie movie) {
        if (movie.getId() == null) {
            entityManager.persist(movie);
        } else {
            entityManager.merge(movie);
        }
    }

    public void delete(Movie movie) {
        entityManager.remove(movie);
    }

    public void rewardGenre(MovieGenre movieGenre) {
        String jpql = "UPDATE Movie m SET m.oscarsCount = m.oscarsCount + 1 WHERE m.genre = :genre";

        Query query = entityManager.createQuery(jpql);
        query.setParameter("genre", movieGenre);
        query.executeUpdate();
    }

    private String buildOrderByClause(List<String> sortParams) {
        if (sortParams == null || sortParams.isEmpty()) {
            return "";
        }

        StringBuilder orderByClause = new StringBuilder("ORDER BY ");

        for (int i = 0; i < sortParams.size(); i++) {
            String[] sortParts = sortParams.get(i).split(",");

            if (sortParts.length != 2) {
                throw new IllegalArgumentException("Invalid sort parameter: " + sortParams.get(i));
            }

            String field = sortParts[0];
            String direction = sortParts[1];

            orderByClause.append("m.")
                    .append(field)
                    .append(" ")
                    .append(direction);

            if (i < sortParams.size() - 1) {
                orderByClause.append(", ");
            }
        }

        return orderByClause.toString();
    }
}
