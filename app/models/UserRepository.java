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
public interface UserRepository extends CrudRepository<User, Long> {
	List<User> findByUserName(String userName);
	
    @Query(value = "select u.* from User u where u.email = ?1", nativeQuery = true)
    User findByEmail(String email);
	@Query(value = "select u.* from User u where u.username = ?1", nativeQuery = true)
	User findByUsername(String username);
    @Query(value = "select u.userName from User u where u.email = ?1", nativeQuery = true)
	String getUserNameByEmail(String email);
    @Query(value = "select u.id from User u where u.email = ?1", nativeQuery = true)
	Long getUserIdByEmail(String email);
    @Query(value = "select u.userName from User u where u.id = ?1", nativeQuery = true)
	String getUserNameById(Long id);
    @Query(value = "select u.userName from User u", nativeQuery = true)
	List<String> getAllUserName();
    @Query(value = "select u.unreadMention from User u where u.email = ?1", nativeQuery = true)
	boolean getHasUnreadMentionByEmail(String email);

	//merged from team 15&16
	@Query(value = "select u.* from User u where id IN (select f.userId from Followers f where followerId = ?1)", nativeQuery = true)
	Set<User> findByFollowerId(long followerId);

	@Query(value = "select u.* from User u where (u.userName like %?1%)", nativeQuery = true)
	List<User> getUserByDisplayName(String displayName);

}
