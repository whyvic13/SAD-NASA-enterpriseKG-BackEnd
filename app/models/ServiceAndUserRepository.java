package models;

import java.math.BigInteger;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.inject.Named;
import javax.inject.Singleton;

@Named
@Singleton
public interface ServiceAndUserRepository extends CrudRepository<ServiceAndUser, Long> {

	List<ServiceAndUser> findByUserOrderByCountDesc(User user);
	List<ServiceAndUser> findByClimateServiceOrderByCountDesc(ClimateService service);
	List<ServiceAndUser> findAll(Sort sort);
	List<ServiceAndUser> findByUserAndClimateService(User user, ClimateService climateService);
	
	@Query(value = "select * from ServiceAndUser group by userId", nativeQuery = true)
	List<ServiceAndUser> findDinstinctUsers();
	
	@Query(value = "select serviceId from ServiceAndUser where userId = ?1", nativeQuery = true)
    List<BigInteger> findByUserId(long userId);
	
	@Query(value = "select userID from ServiceAndUser where serviceId = ?1", nativeQuery = true)
	List<BigInteger> findByServiceId(long serviceId);
}

