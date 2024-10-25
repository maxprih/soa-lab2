package org.bebra.soalab2movie.repository;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.bebra.soacommons.model.dto.PageDto;
import org.bebra.soacommons.model.dto.PageMetadata;
import org.bebra.soacommons.model.enums.MovieGenre;
import org.bebra.soalab2movie.exception.SortingFormatException;
import org.bebra.soalab2movie.model.entity.Movie;
import org.bebra.soalab2movie.utils.FilterCriterion;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Transactional
@Stateless
public class MovieRepository {

    @PersistenceContext(unitName = "MovieSource")
    private EntityManager entityManager;

    public Optional<Movie> findById(Integer id) {
        return Optional.ofNullable(entityManager.find(Movie.class, id));
    }

    public PageDto<Movie> findAll(int page, int size, List<String> sortParams, List<FilterCriterion> filters) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Movie> cq = cb.createQuery(Movie.class);
        Root<Movie> movie = cq.from(Movie.class);

        List<Predicate> predicates = buildPredicates(cb, movie, filters);

        cq.where(predicates.toArray(new Predicate[0]));

        List<Order> orderList = buildOrderList(cb, movie, sortParams);
        if (!orderList.isEmpty()) {
            cq.orderBy(orderList);
        }

        List<Movie> movies = entityManager.createQuery(cq)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Movie> movieCount = countQuery.from(Movie.class);

        countQuery.select(cb.count(movieCount));
        countQuery.where(buildPredicates(cb, movieCount, filters).toArray(new Predicate[0]));

        Long totalCount = entityManager.createQuery(countQuery).getSingleResult();

        return constructPage(movies, page, size, totalCount);
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

    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<Movie> movie, List<FilterCriterion> filters) {
        List<Predicate> predicates = new ArrayList<>();

        for (FilterCriterion filter : filters) {
            String fieldName = filter.getFieldName();
            String filterMode = filter.getFilterMode();
            String value = filter.getValue();

            Path<?> path = movie;

            String[] fieldParts = fieldName.split("\\.");
            for (int i = 0; i < fieldParts.length - 1; i++) {
                path = ((From<?, ?>) path).join(fieldParts[i]);
            }
            String actualField = fieldParts[fieldParts.length - 1];

            switch (filterMode) {
                case "startsWith":
                    predicates.add(cb.like(path.get(actualField), value + "%"));
                    break;
                case "contains":
                    predicates.add(cb.like(path.get(actualField), "%" + value + "%"));
                    break;
                case "notContains":
                    predicates.add(cb.notLike(path.get(actualField), "%" + value + "%"));
                    break;
                case "endsWith":
                    predicates.add(cb.like(path.get(actualField), "%" + value));
                    break;
                case "equals":
                    predicates.add(cb.equal(path.get(actualField), value));
                    break;
                case "notEquals":
                    predicates.add(cb.notEqual(path.get(actualField), value));
                    break;
                case "lt":
                    if (actualField.equals("genre")) {
                        predicates.add(cb.lessThan(path.get(actualField), MovieGenre.valueOf(value)));
                    } else {
                        predicates.add(cb.lessThan(path.get(actualField), value));
                    }
                    break;
                case "lte":
                    predicates.add(cb.lessThanOrEqualTo(path.get(actualField), value));
                    break;
                case "gt":
                    predicates.add(cb.greaterThan(path.get(actualField), value));
                    break;
                case "gte":
                    predicates.add(cb.greaterThanOrEqualTo(path.get(actualField), value));
                    break;
                case "dateIs":
                    predicates.add(cb.equal(path.get(actualField), LocalDate.parse(value)));
                    break;
                case "dateIsNot":
                    predicates.add(cb.notEqual(path.get(actualField), LocalDate.parse(value)));
                    break;
                case "dateBefore":
                    predicates.add(cb.lessThan(path.get(actualField), LocalDate.parse(value)));
                    break;
                case "dateAfter":
                    predicates.add(cb.greaterThan(path.get(actualField), LocalDate.parse(value)));
                    break;
                case "in":
                    String[] genreValues = value.split(",");
                    List<MovieGenre> genreList = Arrays.stream(genreValues)
                            .map(String::trim)
                            .map(MovieGenre::valueOf)
                            .collect(Collectors.toList());
                    predicates.add(path.get(actualField).in(genreList));
                    break;
            }
        }
        return predicates;
    }

    private List<Order> buildOrderList(CriteriaBuilder cb, Root<Movie> root, List<String> sortParams) {
        List<Order> orders = new ArrayList<>();

        if (sortParams != null) {
            for (String sortParam : sortParams) {
                String[] parts = sortParam.split(",");
                if (parts.length != 2) {
                    throw new SortingFormatException();
                }

                String field = parts[0].trim();
                String direction = parts[1].trim().toLowerCase();

                From<?, ?> path = root;

                String[] fieldParts = field.split("\\.");
                for (int i = 0; i < fieldParts.length - 1; i++) {
                    path = path.join(fieldParts[i]);
                }

                String actualField = fieldParts[fieldParts.length - 1];

                if ("asc".equals(direction)) {
                    orders.add(cb.asc(path.get(actualField)));
                } else if ("desc".equals(direction)) {
                    orders.add(cb.desc(path.get(actualField)));
                } else {
                    throw new IllegalArgumentException("Unsupported sort direction: " + direction);
                }
            }
        }

        return orders;
    }

    private PageDto<Movie> constructPage(List<Movie> movies, int page, int size, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        return new PageDto<>(movies, new PageMetadata(size, page, totalElements, totalPages));
    }
}
