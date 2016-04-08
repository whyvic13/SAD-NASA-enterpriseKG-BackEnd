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
public interface AuthorRepository extends CrudRepository<Author, Long> {
	List<Author> findAll();
	
	@Query(value = "select * from Author where id = ?1", nativeQuery = true)
	Author getAuthorById(long id);

}
