package models;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import javax.inject.Named;
import javax.inject.Singleton;

@Named
@Singleton
public interface PublicationTopicKeywordRepository extends CrudRepository<PublicationTopicKeyword, Long> {
	List<PublicationTopicKeyword> findAll();

}
