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
public interface ServiceAndDatasetRepository extends CrudRepository<ServiceAndDataset, Long> {

	List<ServiceAndDataset> findByClimateServiceOrderByCountDesc(ClimateService service);
	List<ServiceAndDataset> findByDatasetOrderByCountDesc(Dataset dataset);
	List<ServiceAndDataset> findAll(Sort sort);
	List<ServiceAndDataset> findByClimateServiceAndDataset(ClimateService climateService, Dataset dataset);
	
	@Query(value = "select climateServiceId from DatasetAndService where datasetId = ?1", nativeQuery = true)
    List<BigInteger> findByDatasetId(long datasetId1);
	
	@Query(value = "select datasetId from DatasetAndService where climateServiceId = ?1", nativeQuery = true)
    List<BigInteger> findByServiceId(long serviceId);
}

