package models;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.inject.Named;
import javax.inject.Singleton;

@Named
@Singleton
public interface PictureRepository extends CrudRepository<Picture, Long> {
	List<Picture> findAll();
	
	@Query(value = "select url from Picture", nativeQuery = true)
	List<String> findAllUrl();
}
