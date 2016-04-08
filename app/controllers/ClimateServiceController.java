package controllers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import models.*;
import util.Common;
import util.Constants;
import workflow.VisTrailJson;
import play.mvc.*;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.persistence.PersistenceException;

import org.apache.commons.lang3.StringEscapeUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;

/**
 * The main set of web services.
 */
@Named
@Singleton
public class ClimateServiceController extends Controller {
	public static final String WILDCARD = "%";
	private final int initialcount = 0;

	// static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ssz";
	private final ClimateServiceRepository climateServiceRepository;
	private final PublicationsRepository publicationsRepository;
	private final JournalPublicationRepository journalPublicationRepository;
	private final UserRepository userRepository;
    private final ServiceEntryRepository serviceEntryRepository;

	// We are using constructor injection to receive a repository to support our
	// desire for immutability.
	@Inject
	public ClimateServiceController(
			final ClimateServiceRepository climateServiceRepository,
			UserRepository userRepository,ServiceEntryRepository serviceEntryRepository,
			JournalPublicationRepository journalPublicationRepository,
			PublicationsRepository publicationsRepository) {
		this.climateServiceRepository = climateServiceRepository;
		this.userRepository = userRepository;
        this.serviceEntryRepository = serviceEntryRepository;
        this.journalPublicationRepository = journalPublicationRepository;
        this.publicationsRepository = publicationsRepository;
	}

	public Result addClimateService() {
		JsonNode json = request().body().asJson();
		
		if (json == null) {
			System.out
					.println("Climate service not saved, expecting Json data");
			return badRequest("Climate service not saved, expecting Json data");
		}

		// Parse JSON file
		long rootServiceId = json.findPath("rootServiceId").asLong();
		long creatorId = json.findPath("creatorId").asLong();
		String name = json.findPath("name").asText();
		String purpose = json.findPath("purpose").asText();
		String url = json.findPath("url").asText();
		String scenario = json.findPath("scenario").asText();
		Date createTime = new Date();
		SimpleDateFormat format = new SimpleDateFormat(Common.DATE_PATTERN);
		try {
			createTime = format.parse(json.findPath("createTime").asText());
		} catch (ParseException e) {
			System.out
					.println("No creation date specified, set to current time");
		}
		String versionNo = json.findPath("versionNo").asText();

		try {
			User user = userRepository.findOne(creatorId);
			ClimateService climateService = new ClimateService(rootServiceId,
					user, name, purpose, url, scenario, createTime, versionNo);
			ClimateService savedClimateService = climateServiceRepository
					.save(climateService);
			String registerNote = "ClimateService Name: " + savedClimateService.getName() + ", VersionNo: "+versionNo;
			ServiceEntry serviceEntry = new ServiceEntry(createTime, versionNo, user, createTime, registerNote, initialcount, savedClimateService);
			serviceEntryRepository.save(serviceEntry);
			System.out.println("Climate Service saved: "
					+ savedClimateService.getName());
			return created(new Gson().toJson(savedClimateService.getId()));
		} catch (PersistenceException pe) {
			pe.printStackTrace();
			System.out.println("Climate Service not saved: " + name);
			return badRequest("Climate Service not saved: " + name);
		}
	}
	
	public Result savePage() {
		JsonNode json = request().body().asJson();
		if (json == null) {
			System.out.println("Climate service not saved, expecting Json data");
			return badRequest("Climate service not saved, expecting Json data");
		}

		// Parse JSON file
		String temp = json.findPath("pageString").asText();

		// Remove delete button from preview page
		String result = temp.replaceAll("<td><button type=\\\\\"button\\\\\" class=\\\\\"btn btn-danger\\\\\" onclick=\\\\\"Javascript:deleteRow\\(this\\)\\\\\">delete</button></td>", "");	
		
		result = StringEscapeUtils.unescapeJava(result);
		result = result.substring(1, result.length() - 1);
		System.out.println(result);
		
		String str1 = Constants.htmlHead;
		String str2 = Constants.htmlTail;
		
		result = str1 + result + str2;
		
		String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss")
				.format(new Date());
		String location = "climateServicePageRepository/" + timeStamp + ".txt";

		File theDir = new File("climateServicePageRepository");

		// if the directory does not exist, create it
		if (!theDir.exists()) {
			System.out.println("creating directory: climateServicePageRepository");
			boolean create = false;

			try {
				theDir.mkdir();
				create = true;
			} catch (SecurityException se) {
				// handle it
			}
			if (create) {
				System.out.println("DIR created");
			}
		} else {
			System.out.println("No");
		}

		try {
			File file = new File(location);
			BufferedWriter output = new BufferedWriter(new FileWriter(file));
			output.write(result);
			output.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ok(result);
	}

	public Result deleteClimateServiceById(long id) {
		ClimateService climateService = climateServiceRepository.findOne(id);
		if (climateService == null) {
			System.out.println("Climate service not found with id: " + id);
			return notFound("Climate service not found with id: " + id);
		}

		climateServiceRepository.delete(climateService);
		System.out.println("Climate service is deleted: " + id);
		return ok("Climate service is deleted: " + id);
	}
	
	public Result deleteClimateServiceByName(String name) {
		ClimateService climateService = climateServiceRepository.findFirstByName(name);
		if (climateService == null) {
			System.out.println("Climate service not found with name: " + name);
			return notFound("Climate service not found with name: " + name);
		}

		climateServiceRepository.delete(climateService);
		System.out.println("Climate service is deleted: " + name);
		return ok("Climate service is deleted: " + name);
	}

	public Result updateClimateServiceById(long id) {
		JsonNode json = request().body().asJson();
		if (json == null) {
			System.out
					.println("Climate service not saved, expecting Json data");
			return badRequest("Climate service not saved, expecting Json data");
		}

		// Parse JSON file
		long rootServiceId = json.findPath("rootServiceId").asLong();
		long creatorId = json.findPath("creatorId").asLong();
		String name = json.findPath("name").asText();
		String purpose = json.findPath("purpose").asText();
		String url = json.findPath("url").asText();
		String scenario = json.findPath("scenario").asText();
		String versionNo = json.findPath("versionNo").asText();
		// Creation time should be immutable and not updated.

		try {
			ClimateService climateService = climateServiceRepository
					.findOne(id);
			User user = userRepository.findOne(creatorId);
			ServiceEntry serviceEntry = null;
			if (versionNo.equals(climateService.getVersionNo())) {
				List<ServiceEntry> serviceEntries = serviceEntryRepository.findByClimateServiceAndVersionNo(climateService, versionNo);
				if (serviceEntries.size()==0) {
					String registerNote = "ClimateService Name: " + climateService.getName() + ", VersionNo: "+versionNo;
					serviceEntry = new ServiceEntry(climateService.getCreateTime(), versionNo, user, climateService.getCreateTime(), registerNote, initialcount, climateService);
				}
				else {
					serviceEntry = serviceEntries.get(0);
				}
			}
			else {
				String registerNote = "ClimateService Name:" + climateService.getName() + ", VersionNo: "+versionNo;
				serviceEntry = new ServiceEntry(climateService.getCreateTime(), versionNo, user, climateService.getCreateTime(), registerNote, initialcount, climateService);
			}
			climateService.setUser(user);
			climateService.setName(name);
			climateService.setPurpose(purpose);
			climateService.setRootServiceId(rootServiceId);
			climateService.setScenario(scenario);
			climateService.setUrl(url);
			climateService.setVersionNo(versionNo);
			ClimateService savedClimateService = climateServiceRepository
					.save(climateService);
			serviceEntry.setClimateService(savedClimateService);
			ServiceEntry savedServiceEntry = serviceEntryRepository.save(serviceEntry);
			
			System.out.println("Climate Service updated: "
					+ savedClimateService.getName());
			return created("Climate Service updated: "
					+ savedClimateService.getName());
		} catch (PersistenceException pe) {
			pe.printStackTrace();
			System.out.println("Climate Service not updated: " + name);
			return badRequest("Climate Service not updated: " + name);
		}
	}

	public Result updateClimateServiceByName(String oldName) {
		JsonNode json = request().body().asJson();
		if (json == null) {
			System.out
					.println("Climate service not saved, expecting Json data");
			return badRequest("Climate service not saved, expecting Json data");
		}
		System.out.println(json);
		// Parse JSON file
		long rootServiceId = json.findPath("rootServiceId").asLong();
		long creatorId = json.findPath("creatorId").asLong();
		String name = json.findPath("name").asText();
		String purpose = json.findPath("purpose").asText();
		String url = json.findPath("url").asText();
		String scenario = json.findPath("scenario").asText();
		String versionNo = json.findPath("versionNo").asText();
		// Creation time is immutable and should not be updated

		if (oldName == null || oldName.length() == 0) {
			System.out.println("Old climate Service Name is null or empty!");
			return badRequest("Old climate Service Name is null or empty!");
		}

		try {
			ClimateService climateService = climateServiceRepository
					.findFirstByName(oldName);
			User user = userRepository.findOne(creatorId);
			ServiceEntry serviceEntry = null;
			if (versionNo.equals(climateService.getVersionNo())) {
				List<ServiceEntry> serviceEntries = serviceEntryRepository.findByClimateServiceAndVersionNo(climateService, versionNo);
				if (serviceEntries.size()==0) {
					String registerNote = "ClimateService Name: " + climateService.getName() + ", VersionNo: "+versionNo;
					serviceEntry = new ServiceEntry(climateService.getCreateTime(), versionNo, user, climateService.getCreateTime(), registerNote, initialcount, climateService);
				}
				else {
					serviceEntry = serviceEntries.get(0);
				}
			}
			else {
				String registerNote = "ClimateService Name: " + climateService.getName() + ", VersionNo: "+versionNo;
				serviceEntry = new ServiceEntry(climateService.getCreateTime(), versionNo, user, climateService.getCreateTime(), registerNote, initialcount, climateService);
			}
			climateService.setName(name);
			climateService.setPurpose(purpose);
			climateService.setRootServiceId(rootServiceId);
			climateService.setScenario(scenario);
			climateService.setUrl(url);
			
			climateService.setUser(user);
			climateService.setVersionNo(versionNo);

			ClimateService savedClimateService = climateServiceRepository
					.save(climateService);
			serviceEntry.setClimateService(savedClimateService);
			ServiceEntry savedServiceEntry = serviceEntryRepository.save(serviceEntry);
			System.out.println("Climate Service updated: "
					+ savedClimateService.getName());
			return created("Climate Service updated: "
					+ savedClimateService.getName());
		} catch (PersistenceException pe) {
			pe.printStackTrace();
			System.out.println("Climate Service not updated: " + name);
			return badRequest("Climate Service not updated: " + name);
		}
	}

	public Result getClimateService(String name, String format) {
		if (name == null || name.length() == 0) {
			System.out.println("Climate Service Name is null or empty!");
			return badRequest("Climate Service Name is null or empty!");
		}

		List<ClimateService> climateService = climateServiceRepository
				.findAllByName(name);
		if (climateService == null) {
			System.out.println("Climate service not found with name: " + name);
			return notFound("Climate service not found with name: " + name);
		}

		String result = new String();
		if (format.equals("json")) {
			result = new Gson().toJson(climateService);
		}

		return ok(result);
	}

	public Result getClimateServiceById(long id) {
		ClimateService climateService = climateServiceRepository.findOne(id);
		if (climateService == null) {
			System.out.println("Climate service not found with id: " + id);
			return notFound("Climate service not found with id: " + id);
		}

		String result = new Gson().toJson(climateService);

		return ok(result);
	}

	public Result getAllClimateServices(String format) {
		Iterable<ClimateService> climateServices = climateServiceRepository
				.findAll();
		if (climateServices == null) {
			System.out.println("No climate service found");
		}

		String result = new String();
		if (format.equals("json")) {
			result = new Gson().toJson(climateServices);
		}

		return ok(result);

	}
	
    public Result getAllClimateServicesOrderByCreateTime(String format){
        Iterable<ClimateService> climateServices = climateServiceRepository
                .findByOrderByCreateTimeDesc();
        if (climateServices == null) {
            System.out.println("No climate service found");
        }

        String result = new String();
        if (format.equals("json")) {
            result = new Gson().toJson(climateServices);
        }

        return ok(result);
    }

    public Result getAllClimateServicesOrderByLatestAccessTime(String format){
        Iterable<ClimateService> climateServices = climateServiceRepository.getClimateServiceOrderByLatestAccessTime();
        if (climateServices == null) {
            System.out.println("No climate service found");
        }

        String result = new String();
        if (format.equals("json")) {
            result = new Gson().toJson(climateServices);
        }

        return ok(result);
    }

    public Result getAllClimateServicesOrderByCount(String format){
//        Iterable<ClimateService> climateServices = climateServiceRepository
//                .findByOrderByCreateTimeDesc();
        Iterable<ClimateService> climateServices = climateServiceRepository.getClimateServiceOrderByCount();
        if (climateServices == null) {
            System.out.println("No climate service found");
        }

        String result = new String();
        if (format.equals("json")) {
            result = new Gson().toJson(climateServices);
        }

        return ok(result);
    }

    public Result addServiceEntry() {
        JsonNode json = request().body().asJson();
        if (json == null) {
            System.out
                    .println("Climate service not saved, expecting Json data");
            return badRequest("Climate service not saved, expecting Json data");
        }

        // Parse JSON file
        String versionNo = json.findPath("versionNo").asText();
        String registerNote = json.findPath("registerNote").asText();
        int count = json.findPath("count").asInt();
        // String scenario = json.findPath("scenario").asText();
        long serviceId = json.findPath("serviceId").asLong();
        long creatorId = json.findPath("creatorId").asLong();

        Date registerTime = new Date();
        SimpleDateFormat format = new SimpleDateFormat(Common.DATE_PATTERN);
        try {
            registerTime = format.parse(json.findPath("registerTimeStamp").asText());
        } catch (ParseException e) {
            System.out
                    .println("No creation date specified, set to current time");
        }

        Date latestAccessTime = new Date();
        try {
            latestAccessTime = format.parse(json.findPath("latestAccessTimeStamp").asText());
        } catch (ParseException e) {
            System.out
                    .println("No creation date specified, set to current time");
        }

        try {
            ClimateService climateService = climateServiceRepository.findOne(serviceId);
            User creator = userRepository.findOne(creatorId);
            ServiceEntry entry = new ServiceEntry();
            entry.setClimateService(climateService);
            entry.setCount(count);
            entry.setRegisterNote(registerNote);
            entry.setVersionNo(versionNo);
            entry.setUser(creator);
            entry.setRegisterTimeStamp(registerTime);
            entry.setLatestAccessTimestamp(latestAccessTime);

            ServiceEntry savedServiceEntry = serviceEntryRepository.save(entry);

            System.out.println("Service Entry saved: "
                    + savedServiceEntry.getId());
            return created(new Gson().toJson(savedServiceEntry));
        } catch (PersistenceException pe) {
            pe.printStackTrace();
            System.out.println("Service Entry not saved: " + serviceId);
            return badRequest("Service Entry not saved: " + serviceId);
        }
    }

    public Result getAllServiceEntries(String format) {
        Iterable<ServiceEntry> serviceEntries = serviceEntryRepository
                .findAll();
        if (serviceEntries == null) {
            System.out.println("No service entry found");
        }

        String result = new String();
        if (format.equals("json")) {
            result = new Gson().toJson(serviceEntries);
        }

        return ok(result);

    }
    
    public Result getTopKUsedClimateServicesByDatasetId(long id, String format) {
    	if (id < 0) {
			System.out.println("id is negative!");
			return badRequest("id is negative!");
		}
    	String result = new String();
    	try {
    		//Parse JSON file
    		List<ClimateService> climateService;
    		climateService = climateServiceRepository.getClimateServiceByDatasetId(5, id);
    		result = new Gson().toJson(climateService);
    	} catch (Exception e) {
    		System.out.println("ServiceExecutionLog cannot be queried, query is corrupt");
    		return badRequest("ServiceExecutionLog cannot be queried, query is corrupt");
    	}
    	return ok(result);
    }
    
    public Result queryClimateServices() {
    	JsonNode json = request().body().asJson();
    	if (json == null) {
    		System.out.println("ClimateService cannot be queried, expecting Json data");
    		return badRequest("ClimateService cannot be queried, expecting Json data");
    	}
    	String result = new String();
    	try {
    		//Parse JSON file
    		String name = json.path("name").asText();
    		if (name.isEmpty()) {
    			name = WILDCARD;
    		}
    		else {
    			name = WILDCARD+name+WILDCARD;
    		}
    		String purpose = json.path("purpose").asText();
    		if (purpose.isEmpty()) {
    			purpose = WILDCARD;
    		}
    		else {
    			purpose = WILDCARD+purpose+WILDCARD;
    		}
    		String url = json.path("url").asText();
    		if (url.isEmpty()) {
    			url = WILDCARD;
    		}
    		else {
    			url = WILDCARD+url+WILDCARD;
    		}
    		String scenario = json.path("scenario").asText();
    		if (scenario.isEmpty()) {
    			scenario = WILDCARD;
    		}
    		else {
    			scenario = WILDCARD+scenario+WILDCARD;
    		}
    		String versionNo = json.path("versionNo").asText();
    		if (versionNo.isEmpty()) {
    			versionNo = WILDCARD;
    		}
    		else {
    			versionNo = WILDCARD+versionNo+WILDCARD;
    		}

    		List<ClimateService> climateServices = climateServiceRepository.findClimateService(name, purpose, url, scenario, versionNo);
    		result = new Gson().toJson(climateServices);
    	} catch (Exception e) {
    		System.out.println("ServiceExecutionLog cannot be queried, query is corrupt");
    		return badRequest("ServiceExecutionLog cannot be queried, query is corrupt");
    	}

    	System.out.println("*******************************\n&&&&&&&&&&&&&&&&&&&&&&&&&&&&\n" + result);
    	return ok(result);
    }
    
    // Zuowei Edited
	public Result addAPublication() {
		JsonNode json = request().body().asJson();
		if (json == null) {
			System.out
					.println("Publication not saved, expecting Json data");
			return badRequest("Publication not saved, expecting Json data");
		}
		String paperTitle = json.findPath("paperTitle").asText();
		String authorList = "";
		String publicationChannel = "";
		int year = 0;
		String fileID = "";
		try {
			// Parse JSON file
			authorList = json.findPath("authorList").asText();
			publicationChannel = json.findPath("publicationChannel").asText();
			year = json.findPath("year").asInt();
			fileID = json.findPath("fileID").asText();
			Publications publications = new Publications(paperTitle,
					authorList, publicationChannel, year, fileID, "", "","");
			publicationsRepository.save(publications);

			return ok("Publication is saved");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Publication not saved: " + paperTitle);
			return badRequest("Publication not saved: " + paperTitle);
		}
	}
	// Zuowei Edited
	public Result getAllPublications(String format) {
		List<Publications> publications = publicationsRepository
				.findAll();
		if (publications == null) {
			System.out.println("No publication found");
		}

		String result = new String();
		if (format.equals("json")) {
			result = new Gson().toJson(publications);
		}
		return ok(result);
	}
	
	// Haoyun Wen edited
	public Result getAllJournalPublications(String format) {
		Iterable<JournalPublication> journalPublication = journalPublicationRepository
				.findAll();
		if (journalPublication == null) {
			System.out.println("No climate service found");
		}
		String result = new String();
		if (format.equals("json")) {
			result = new Gson().toJson(journalPublication);
		}
		return ok(result);

	}
	
	//Haoyun Wen
	public Result getAllPublicationsOrderByLatestAccessTime(String format){
        Iterable<Publications> publications = publicationsRepository.getPublicationOrderByLatestAccessTime();
        if (publications == null) {
            System.out.println("No climate service found");
        }

        String result = new String();
        if (format.equals("json")) {
            result = new Gson().toJson(publications);
        }

        return ok(result);
    }
	
	//Haoyun Wen
	public Result getAllPublicationsOrderByCreateTime(String format){
		Iterable<Publications> publications = publicationsRepository.getPublicationOrderByCreateTime();
	    if (publications == null) {
	        System.out.println("No climate service found");
	    }

	    String result = new String();
	    if (format.equals("json")) {
	        result = new Gson().toJson(publications);
	    }

	    return ok(result);
	}

}
