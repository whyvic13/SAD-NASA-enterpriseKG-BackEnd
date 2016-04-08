package models;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Provides CRUD functionality for accessing people. Spring Data auto-magically takes care of many standard
 * operations here.
 */
@Named
@Singleton
public interface PublicationAndAuthorRepository extends CrudRepository<PublicationAndAuthor, Long> {
	List<PublicationAndAuthor> findAll();
	List<PublicationAndAuthor> findByAuthor(Author author);
}
