package controllers;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import models.Dataset;
import models.DatasetAndUserRepository;
import models.DatasetRepository;
import models.ServiceAndUserRepository;
import models.ServiceExecutionLog;
import models.ServiceExecutionLogRepository;
import models.ServiceAndDatasetRepository;
import models.User;
import models.UserRepository;
import play.mvc.Controller;
import play.mvc.Result;

import com.google.gson.Gson;

/**
 * The main set of web services.
 */
@Named
@Singleton
public class DatasetAndServiceAndUserController extends Controller {
	
	private final DatasetAndUserRepository datasetAndUserRepository;
	private final ServiceAndUserRepository serviceAndUserRepository;
	private final DatasetRepository datasetRepository;
	private final UserRepository userRepository;
	private final ServiceExecutionLogRepository serviceExecutionLogRepository;
	private final ServiceAndDatasetRepository serviceAndDatasetRepository;
	
	@Inject
	public DatasetAndServiceAndUserController(
			final DatasetAndUserRepository datasetAndUserRepository,
			final UserRepository userRepository,
			final ServiceAndUserRepository serviceAndUserRepository,
			final ServiceAndDatasetRepository serviceAndDatasetRepository,
			final DatasetRepository datasetRepository,
			final ServiceExecutionLogRepository serviceExecutionLogRepository) {
		this.serviceAndDatasetRepository = serviceAndDatasetRepository;
		this.datasetAndUserRepository = datasetAndUserRepository;
		this.serviceAndUserRepository = serviceAndUserRepository;
		this.datasetRepository = datasetRepository;
		this.userRepository = userRepository;
		this.serviceExecutionLogRepository = serviceExecutionLogRepository;
	}
	
	public Result getAllDatasetsByUsers(long userId1, long userId2, String format) {
		List<BigInteger> datasets1 = datasetAndUserRepository.findByUserId(userId1);
		List<BigInteger> datasets2 = datasetAndUserRepository.findByUserId(userId2);
		List<BigInteger> datasetIds = new ArrayList<BigInteger>();
		
		for (BigInteger datasetId1 : datasets1) {
			for (BigInteger datasetId2 : datasets2) {
				if (datasetId1.equals(datasetId2)) {
					datasetIds.add(datasetId1);
				}
			}
		}
		
		if (datasetIds.size() == 0) {
			System.out.println("No datasets found");
		}
		
		List<Dataset> datasets = new ArrayList<Dataset>();
		for (BigInteger id : datasetIds) {
			datasets.add(datasetRepository.findOne(id.longValue()));
		}

		String result = new String();
		if (format.equals("json")) {
			result = new Gson().toJson(datasets);
		}

		return ok(result);
	}

	public Result getAllUsersByDatasets(long datasetId1, long datasetId2, String format) {
		List<BigInteger> users1 = datasetAndUserRepository.findByDatasetId(datasetId1);
		List<BigInteger> users2 = datasetAndUserRepository.findByDatasetId(datasetId2);
		List<BigInteger> userIds = new ArrayList<BigInteger>();

		for (BigInteger userId1 : users1) {
			for (BigInteger userId2 : users2) {
				if (userId1.equals(userId2)) {
					userIds.add(userId1);
				}
			}
		}

		if (userIds.size() == 0) {
			System.out.println("No datasets found");
		}

		List<User> users = new ArrayList<User>();
		for (BigInteger id : userIds) {
			users.add(userRepository.findOne(id.longValue()));
		}

		String result = new String();
		if (format.equals("json")) {
			result = new Gson().toJson(users);
		}

		return ok(result);
	}
	
	public Result getAllServicesByUsers(long userId1, long userId2, String format) {
		List<BigInteger> services1 = serviceAndUserRepository.findByUserId(userId1);
		List<BigInteger> services2 = serviceAndUserRepository.findByUserId(userId2);
		List<BigInteger> serviceIds = new ArrayList<BigInteger>();
		
		for (BigInteger serviceId1 : services1) {
			for (BigInteger serviceId2 : services2) {
				if (serviceId1.equals(serviceId2)) {
					serviceIds.add(serviceId1);
				}
			}
		}
		
		if (serviceIds.size() == 0) {
			System.out.println("No datasets found");
		}
		
		List<ServiceExecutionLog> services = new ArrayList<ServiceExecutionLog>();
		for (BigInteger id : serviceIds) {
			services.add(serviceExecutionLogRepository.findOne(id.longValue()));
		}

		String result = new String();
		if (format.equals("json")) {
			result = new Gson().toJson(services);
		}

		return ok(result);
	}
	
	public Result getAllServicesByDatasets(long datasetId1, long datasetId2, String format) {
		
		List<BigInteger> services1 = serviceAndDatasetRepository.findByDatasetId(datasetId1);
		List<BigInteger> services2 = serviceAndDatasetRepository.findByDatasetId(datasetId2);
		List<BigInteger> serviceIds = new ArrayList<BigInteger>();
		
		for (BigInteger serviceId1 : services1) {
			for (BigInteger serviceId2 : services2) {
				if (serviceId1.equals(serviceId2)) {
					serviceIds.add(serviceId1);
				}
			}
		}
		
		if (serviceIds.size() == 0) {
			System.out.println("No datasets found");
		}
		
		List<ServiceExecutionLog> services = new ArrayList<ServiceExecutionLog>();
		for (BigInteger id : serviceIds) {
			services.add(serviceExecutionLogRepository.findOne(id.longValue()));
		}

		String result = new String();
		if (format.equals("json")) {
			result = new Gson().toJson(services);
		}

		return ok(result);
	}
	
	public Result getAllUsersByServices(long serviceId1, long serviceId2, String format) {
		List<BigInteger> users1 = serviceAndUserRepository.findByServiceId(serviceId1);
		List<BigInteger> users2 = serviceAndUserRepository.findByServiceId(serviceId2);
		List<BigInteger> userIds = new ArrayList<BigInteger>();

		for (BigInteger userId1 : users1) {
			for (BigInteger userId2 : users2) {
				if (userId1.equals(userId2)) {
					userIds.add(userId1);
				}
			}
		}

		if (userIds.size() == 0) {
			System.out.println("No datasets found");
		}

		List<User> users = new ArrayList<User>();
		for (BigInteger id : userIds) {
			users.add(userRepository.findOne(id.longValue()));
		}

		String result = new String();
		if (format.equals("json")) {
			result = new Gson().toJson(users);
		}

		return ok(result);
	}
	
	public Result getAllDatasetsByServices(long serviceId1, long serviceId2, String format) {
		List<BigInteger> datasets1 = serviceAndDatasetRepository.findByServiceId(serviceId1);
		List<BigInteger> datasets2 = serviceAndDatasetRepository.findByServiceId(serviceId2);
		List<BigInteger> datasetIds = new ArrayList<BigInteger>();
		
		for (BigInteger datasetId1 : datasets1) {
			for (BigInteger datasetId2 : datasets2) {
				if (datasetId1.equals(datasetId2)) {
					datasetIds.add(datasetId1);
				}
			}
		}

		if (datasetIds.size() == 0) {
			System.out.println("No datasets found");
		}
		
		List<Dataset> datasets = new ArrayList<Dataset>();
		for (BigInteger id : datasetIds) {
			datasets.add(datasetRepository.findOne(id.longValue()));
		}

		String result = new String();
		if (format.equals("json")) {
			result = new Gson().toJson(datasets);
		}

		return ok(result);
	}
	
	
	
	
}
