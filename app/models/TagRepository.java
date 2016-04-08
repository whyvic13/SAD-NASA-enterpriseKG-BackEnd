package models;

import org.springframework.data.repository.CrudRepository;

import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Created by frankluo on 12/3/15.
 */

@Named
@Singleton
public interface TagRepository extends CrudRepository<Tag,Long>{
    Tag findByTag(String tag);

}
