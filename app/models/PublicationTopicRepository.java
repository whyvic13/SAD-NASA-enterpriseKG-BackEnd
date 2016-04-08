package models;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import javax.inject.Named;
import javax.inject.Singleton;

@Named
@Singleton
public interface PublicationTopicRepository extends CrudRepository<PublicationTopic, Long> {
	List<PublicationTopic> findAll();

}
