package models;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.inject.Named;
import javax.inject.Singleton;

@Named
@Singleton
public interface PublicationsRepository extends CrudRepository<Publications, Long> {
	List<Publications> findAll();
	
	@Query(value = "select p.* from Publications p order by p.id desc", nativeQuery = true)
	List<Publications> getPublicationOrderByLatestAccessTime();
	
	@Query(value = "select p.* from Publications p order by p.id desc", nativeQuery = true)
	List<Publications> getPublicationOrderByCreateTime();
	
	@Query(value = "select * from Publications where ((paperTitle like ?1) and (authorList like ?2) and (publicationChannel like ?3) and (year = ?4))", nativeQuery = true)
	List<Publications> findPublication(String paperTitle, String author, String publicationChannel, int year);

	@Query(value = "select * from Publications where ((paperTitle like ?1) and (authorList like ?2) and (publicationChannel like ?3))", nativeQuery = true)
	List<Publications> findPublicationWithoutYear(String paperTitle, String author, String publicationChannel);

	@Query(value = "select p.* from Publications p order by p.id desc", nativeQuery = true)
	List<Publications> getPublicationOrderByCount();
	
	@Query(value = "select id from Publications where fileID = ?1", nativeQuery = true)
	long getPublicationID(String fileID);
	
	@Query(value = "select p.* from Publications p where id = ?1", nativeQuery = true)
	List<Publications> getPublicationById(long id);
}
