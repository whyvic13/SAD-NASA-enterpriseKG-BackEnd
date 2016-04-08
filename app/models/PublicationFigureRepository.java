package models;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.inject.Named;
import javax.inject.Singleton;

@Named
@Singleton
public interface PublicationFigureRepository extends CrudRepository<PublicationFigure, Long> {
	List<PublicationFigure> findAll();
	
	@Query(value = "select * from PublicationFigure where publicationId = ?1", nativeQuery = true)
	List<PublicationFigure> getPublicationMetadataByPublicationId(long publicationId);

}
