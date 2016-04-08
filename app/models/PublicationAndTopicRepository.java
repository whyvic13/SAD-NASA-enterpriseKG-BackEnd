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
public interface PublicationAndTopicRepository extends CrudRepository<PublicationAndTopic, Long> {
	List<PublicationAndTopic> findAll();
	
	@Query(value = "select * from PublicationAndTopic where topicId = ?1", nativeQuery = true)
	List<PublicationAndTopic> getPublicationIdByTopicId(long topicId);
	
	@Query(value = "select * from PublicationAndTopic where publicationId = ?1", nativeQuery = true)
	List<PublicationAndTopic> getTopicIdByPublicationId(long publicationId);
	
}
