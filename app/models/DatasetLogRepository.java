package models;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.inject.Named;
import javax.inject.Singleton;

@Named
@Singleton
public interface DatasetLogRepository extends CrudRepository<DatasetLog, Long> {
	
	List<DatasetLog> findByOrderByServiceExecutionStartTimeDesc();
	
    List<DatasetLog> findByServiceExecutionStartTimeGreaterThanEqualAndServiceExecutionEndTimeLessThanEqualAndUser_Id(Date serviceExecutionStartTime, Date serviceExecutionEndTime, long userId);
    
    @Query(value = "select d.* from DatasetLog d where userId = ?1 and dataSetId = ?2", nativeQuery = true)
    List<DatasetLog> findByUserIdAndDatasetId(long userId, long dataSetId);
    
    @Query(value = "select d.* from ServiceExecutionLog s join DatasetLog d on s.id = d.serviceExecutionLogId where s.userId = ?1 and s.serviceId = ?2", nativeQuery = true)
    List<DatasetLog> findByUserIdAndServiceId(long userId, long serviceId);
    
    @Query(value = "select d.* from ServiceExecutionLog s join DatasetLog d on s.id = d.serviceExecutionLogId where d.dataSetId = ?2 and s.serviceId = ?1 group by d.userId", nativeQuery = true)
    List<DatasetLog> findByServiceIdAndDatasetId(long serviceId, long dataSetId);
}
