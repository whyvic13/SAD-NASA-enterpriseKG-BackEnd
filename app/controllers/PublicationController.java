package controllers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Modifier;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.apache.hadoop.hbase.client.StatsTrackingRpcRetryingCaller;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;

/**
 * The main set of web services.
 */
@Named
@Singleton
public class PublicationController extends Controller {

	public static final String WILDCARD = "%";
	private final int initialcount = 0;

	// static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ssz";
	private final ClimateServiceRepository climateServiceRepository;
	private final PublicationsRepository publicationsRepository;
	private final JournalPublicationRepository journalPublicationRepository;
	private final UserRepository userRepository;
    private final ServiceEntryRepository serviceEntryRepository;
    private final PublicationFigureRepository publicationFigureRepository;
    private final AuthorRepository authorRepository;
    private final PublicationAndAuthorRepository publicationAndAuthorRepository;
    private final PublicationAndTopicRepository publicationAndTopicRepository;
    private final PublicationTopicRepository publicationTopicRepository;
    private final PublicationTopicKeywordRepository publicationTopicKeywordRepository;
    private final PublicationAndTopicKeywordRepository publicationAndTopicKeywordRepository;

	// We are using constructor injection to receive a repository to support our
	// desire for immutability.
	@Inject
	public PublicationController(
			final ClimateServiceRepository climateServiceRepository,
			UserRepository userRepository,ServiceEntryRepository serviceEntryRepository,
			JournalPublicationRepository journalPublicationRepository,
			PublicationsRepository publicationsRepository,
			PublicationFigureRepository publicationFigureRepository,
			AuthorRepository authorRepository,
			PublicationAndAuthorRepository publicationAndAuthorRepository,
			PublicationAndTopicRepository publicationAndTopicRepository,
			PublicationTopicRepository publicationTopicRepository,
			PublicationTopicKeywordRepository publicationTopicKeywordRepository,
			PublicationAndTopicKeywordRepository publicationAndTopicKeywordRepository) {
		this.climateServiceRepository = climateServiceRepository;
		this.userRepository = userRepository;
        this.serviceEntryRepository = serviceEntryRepository;
        this.journalPublicationRepository = journalPublicationRepository;
        this.publicationsRepository = publicationsRepository;
        this.publicationFigureRepository = publicationFigureRepository;
        this.authorRepository = authorRepository;
        this.publicationAndAuthorRepository = publicationAndAuthorRepository;
        this.publicationAndTopicRepository = publicationAndTopicRepository;
        this.publicationTopicRepository = publicationTopicRepository;
        this.publicationTopicKeywordRepository = publicationTopicKeywordRepository;
        this.publicationAndTopicKeywordRepository = publicationAndTopicKeywordRepository;
	}
	
	public void parseJson(String fileID, String paperTitle, Publications publications) {
		//long id = publicationsRepository.getPublicationID(fileID);
		PublicationFigure publicationFigure = null;
		try {
			//read json file and get the caption
			String directory = System.getProperty("user.dir").replace("NASA-EnterpriseKG-Backend", "NASA-EnterpriseKG-Frontend");
			String filePath = directory + "/public/uploadFile/json/"+paperTitle+".json";
			JsonReader jsonReader = new JsonReader(new FileReader(filePath));
		    jsonReader.beginArray();
		    while (jsonReader.hasNext()) {
		    	String type = "";
		    	int number = 0;
		    	String caption = "";
		    	jsonReader.beginObject();
		    	while (jsonReader.hasNext()) {
		    		String name = jsonReader.nextName();
		    		if(name.equals("Type")) {
		    			type = jsonReader.nextString();
		    		} else if(name.equals("Number")) {
		    			number = jsonReader.nextInt();
		    		} else if(name.equals("Caption")) {
		    			caption = jsonReader.nextString();
		    		} else {
		    			jsonReader.skipValue();
		    		}
		    		
		    	}
		    	jsonReader.endObject();
		    	
		    	//Save to PublicationFigure table
		    	String path = "";
		    	if(type.equals("Table")) {
		    		path = "uploadFile/figures/"+paperTitle+"-Table-"+number+".png";
		    		publicationFigure = new PublicationFigure(publications, path, caption);
		    		//System.out.println(publicationFigure.toString());
		    		publicationFigureRepository.save(publicationFigure);
		    	} else if(type.equals("Figure")) {
		    		path = "uploadFile/figures/"+paperTitle+"-Figure-"+number+".png";
		    		publicationFigure = new PublicationFigure(publications, path, caption);
		    		//System.out.println(publicationFigure.toString());
		    		publicationFigureRepository.save(publicationFigure);
		    	}
		    }

		   jsonReader.endArray();
		   jsonReader.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		int hashCode = 0;
		try {
			// Parse JSON file
			ArrayList<String> authorArrayList = new ArrayList<>();
			String[] authorIdList = json.findPath("authorList").asText().split(",");
			for (String authorId: authorIdList) {
				try {
					Author authorAccordingToID = authorRepository.getAuthorById(Integer.parseInt(authorId));
					if (authorAccordingToID != null) {
						authorArrayList.add(authorAccordingToID.getAuthorName());
					}
				} catch (NumberFormatException nfe) {
					authorArrayList.add(authorId);
				}
			}
			authorList = authorArrayList.toString().replace("[", "").replace("]", "");
			
			publicationChannel = json.findPath("publicationChannel").asText();
			year = json.findPath("year").asInt();
			fileID = json.findPath("fileID").asText();//file Path also
			hashCode = json.findPath("hashCode").asInt();
			
			// Store Publications table
			Publications publications = new Publications(paperTitle,
					authorList, publicationChannel, year, fileID, "", "","");
			publicationsRepository.save(publications);
			
			long publicationId = publicationsRepository.getPublicationID(fileID);
			
			// Store PublicationAndAuthor table
			String[] authorParts = authorList.split(",");
			for (String part: authorParts) {
				try {
					long authorId = Long.parseLong(part);
					Author authorAccordingToID = authorRepository.getAuthorById(authorId);
					Author currentAuthor = authorAccordingToID;
					PublicationAndAuthor publicationAndAuthor = new PublicationAndAuthor(currentAuthor, publications);
					publicationAndAuthorRepository.save(publicationAndAuthor);
				} catch (NumberFormatException nfe) {
					String authorName = part;
					Author currentAuthor = new Author(authorName, "default Institute");
					authorRepository.save(currentAuthor);
					PublicationAndAuthor publicationAndAuthor = new PublicationAndAuthor(currentAuthor, publications);
					publicationAndAuthorRepository.save(publicationAndAuthor);
				}
			}
			
			// PDF caption extract
			
			String directory = System.getProperty("user.dir").replace("NASA-EnterpriseKG-Backend", "NASA-EnterpriseKG-Frontend");
			String filePath = directory + "/" + fileID;
			ProcessBuilder builder = new ProcessBuilder(
	    			"./pdffigures",
	    			"-j",
	    			String.valueOf(hashCode),
	    			filePath);
	    	builder.redirectErrorStream(true); // This is the important part
	    	builder.directory(new File(directory + "/public/uploadFile/pdffigures"));
	    	Process proc = builder.start();
		    proc.waitFor();
		    BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
	        String inputLine;
	        while ((inputLine = in.readLine()) != null) {
	            System.out.println(inputLine);
	        }
	        in.close();
		    
			System.out.println("-----------extract json finished.------------");
			// PDF figures extract
			builder = new ProcessBuilder(
	    			"./pdffigures",
	    			"-o",
	    			String.valueOf(hashCode),
	    			filePath);
	    	builder.redirectErrorStream(true); // This is the important part
	    	builder.directory(new File(directory + "/public/uploadFile/pdffigures"));
	    	proc = builder.start();
		    proc.waitFor();
		    in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
	        while ((inputLine = in.readLine()) != null) {
	            System.out.println(inputLine);
	        }
	        in.close();
			
	        // Store  PublicationFigure table
			//Parse json file, save caption to database
			parseJson(fileID, String.valueOf(hashCode), publications);

			return ok("Publication is saved");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Publication not saved: " + paperTitle);
			return badRequest("Publication not saved: " + paperTitle);
		}
	}

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
	
	public Result getAllPublicationTopics(String format) {
		List<PublicationTopic> topics = publicationTopicRepository
				.findAll();
		if (topics == null) {
			System.out.println("No publication topic found");
		}
		
		String result = new String();
		if (format.equals("json")) {
			result = new Gson().toJson(topics);
		}
		return ok(result);
	}
	
	public Result getAllPublicationsByTopicId(long id, String format) {
		List<PublicationAndTopic> publicationAndTopics = publicationAndTopicRepository.getPublicationIdByTopicId(id);
		List<Publications> publications = new ArrayList<>();
		
		for (PublicationAndTopic publicationAndTopic : publicationAndTopics) {
			Publications publication = publicationsRepository.findOne(publicationAndTopic.getPublication().getId());

			if (publication != null) {
				publications.add(publication);
			}
		}
		
		if (publications.size() == 0) {
			System.out.println("No publication found");
		}

		String result = new String();
		if (format.equals("json")) {
			result = new Gson().toJson(publications);
		}
		return ok(result);
	}
	
	public Result getAllPublicationTopicKeywords(String format) {
		List<PublicationTopicKeyword> topicKeywords = publicationTopicKeywordRepository
				.findAll();
		if (topicKeywords == null) {
			System.out.println("No publication topic keyword found");
		}
		
		String result = new String();
		if (format.equals("json")) {
			result = new Gson().toJson(topicKeywords);
		}
		return ok(result);
	}
	
	public Result getPublicationTopicByPublicationId(long id, String format) {
		List<PublicationAndTopic> publicationAndTopicList = publicationAndTopicRepository.getTopicIdByPublicationId(id);
		PublicationTopic publicationTopic = new PublicationTopic();
		if(!publicationAndTopicList.isEmpty()) 
			publicationTopic = publicationAndTopicList.get(0).getTopic();
		
		String result = new String();
		if (format.equals("json")) {
			result = new Gson().toJson(publicationTopic);
		}
		return ok(result);
	}
	
	public Result getAllPublicationTopicKeywordsByPublicationId(long id, String format) {
		List<PublicationAndTopicKeyword> publicationAndTopicKeywords = publicationAndTopicKeywordRepository.getTopicKeywordByPublicationId(id);
		List<PublicationTopicKeyword> publicationTopicKeywords = new ArrayList<>();
		
		for (PublicationAndTopicKeyword publicationAndTopicKeyword : publicationAndTopicKeywords) {
			PublicationTopicKeyword publicationTopicKeyword = publicationTopicKeywordRepository.findOne(publicationAndTopicKeyword.getTopicKeyword().getId());
			int count = publicationAndTopicKeyword.getCount();
			if (publicationTopicKeyword != null && count != 0) {
				publicationTopicKeyword.setCount(count);
				publicationTopicKeywords.add(publicationTopicKeyword);
			}
		}
		
		if (publicationTopicKeywords.size() == 0) {
			System.out.println("No publication found");
		}

		String result = new String();
		if (format.equals("json")) {
			result = new Gson().toJson(publicationTopicKeywords);
		}
		return ok(result);
	}
	
	public Result getAllPublicationsOrderByCount(String format) {
		List<Publications> publications = publicationsRepository
				.getPublicationOrderByCount();
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
	
	public Result getPublicationMetadataByPublicationId(long id, String format) {
		Iterable<PublicationFigure> publicationMetadata = publicationFigureRepository.getPublicationMetadataByPublicationId(id);
	    if (publicationMetadata == null) {
	        System.out.println("No Publication Metadata found");
	    }

	    String result = new String();
	    if (format.equals("json")) {
	        result = new Gson().toJson(publicationMetadata);
	    }

	    return ok(result);
		
	}
	
	public Result getPublicationPanelByPublicationId(long id, String format) {
		Iterable<Publications> publications = publicationsRepository.getPublicationById(id);
	    if (publications == null) {
	        System.out.println("No Publication found");
	    }

	    String result = new String();
	    if (format.equals("json")) {
	        result = new Gson().toJson(publications);
	    }

	    return ok(result);
		
	}
	
	public Result addAuthor(String name, String format) {
		Author author = new Author(name, "default Institute");
		authorRepository.save(author);

	    return ok("Author is saved");
	}
	
	public Result getAllAuthors(String format) {
		Iterable<Author> authorIterable = authorRepository.findAll();
        List<Author> authorList = new ArrayList<Author>();
        for (Author author : authorIterable) {
        	authorList.add(author);
        }
        String result = new String();
        if (format.equals("json")) {
            result = new GsonBuilder().excludeFieldsWithModifiers(Modifier.PROTECTED).create().toJson(authorList);
        }
        return ok(result);
	}
	
	//Haoyun Wen
	public Result queryPublications() {
		List<Publications> publications;
    	JsonNode json = request().body().asJson();
    	if (json == null) {
    		System.out.println("Publication cannot be queried, expecting Json data");
    		return badRequest("Publication cannot be queried, expecting Json data");
    	}
    	String result = new String();
    	try {
    		//Parse JSON file
    		String paperTitle = json.path("paperTitle").asText();
			
    		if (paperTitle.isEmpty()) {
    			paperTitle = WILDCARD;
    			System.out.println("paperTitle:"+paperTitle);
    		}
    		else {
    			paperTitle = WILDCARD+paperTitle+WILDCARD;
    		}
    		//System.out.println("paperTitle:"+paperTitle);
    		String author = json.path("author").asText();
			
    		if (author.isEmpty()) {
    			author = WILDCARD;
    		}
    		else {
    			author = WILDCARD+author+WILDCARD;
    		}
    		//System.out.println("author:"+author);
    		
    		String publicationChannel = json.path("publicationChannel").asText();
			

    		if (publicationChannel.isEmpty()) {
    			publicationChannel = WILDCARD;
    		}
    		else {
    			publicationChannel = WILDCARD+publicationChannel+WILDCARD;
    		}
    		//System.out.println("publicationChannel:"+publicationChannel);
    		
    		String year = json.path("year").asText();
    		if (year.isEmpty()) {
    			publications = publicationsRepository.findPublicationWithoutYear(paperTitle, author, publicationChannel);
    		}
    		else {
    			int yearInteger = Integer.parseInt(year);
    			publications = publicationsRepository.findPublication(paperTitle, author, publicationChannel, yearInteger);
    		}
    		    		
    		result = new Gson().toJson(publications);
    	} catch (Exception e) {
    		System.out.println("ServiceExecutionLog cannot be queried, query is corrupt");
    		return badRequest("ServiceExecutionLog cannot be queried, query is corrupt");
    	}

    	System.out.println("*******************************\n&&&&&&&&&&&&&&&&&&&&&&&&&&&&\n" + result);
    	return ok(result);
    }
}
