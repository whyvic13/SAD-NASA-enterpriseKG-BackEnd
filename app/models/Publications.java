package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Publications {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    @Column(length = 500)
    private String paperTitle;
    @Column(length = 1000)
    private String authorList;
    private String publicationChannel;
    private int year;
    private String fileID;
    private String variable;
    private String dataset;
    private String instrument;
    
    
    
	public Publications() {
	}

	public Publications(String paperTitle, String authorList,
			String publicationChannel, int year, String fileID,
			String ins, String dataset, String variable) {
		super();
		this.paperTitle = paperTitle;
		this.authorList = authorList;
		this.publicationChannel = publicationChannel;
		this.year = year;
		this.setFileID(fileID);
		this.setInstrument(ins);
		this.setDataset(dataset);
		this.setVariable(variable);
	}

	public String getPaperTitle() {
		return paperTitle;
	}

	public void setPaperTitle(String paperTitle) {
		this.paperTitle = paperTitle;
	}

	public String getAuthorList() {
		return authorList;
	}

	public void setAuthorList(String authorList) {
		this.authorList = authorList;
	}

	public String getPublicationChannel() {
		return publicationChannel;
	}

	public void setPublicationChannel(String publicationChannel) {
		this.publicationChannel = publicationChannel;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public long getId() {
		return id;
	}
	
	public String getFileID() {
		return fileID;
	}

	public void setFileID(String fileID) {
		this.fileID = fileID;
	}

	@Override
	public String toString() {
		return "Publication [id=" + id + ", paperTitle=" + paperTitle
				+ ", authors=" + authorList + ", publicationChannel="
				+ publicationChannel + ", year=" + year + "]";
	}

	public String getDataset() {
		return dataset;
	}

	public void setDataset(String dataset) {
		this.dataset = dataset;
	}

	public String getInstrument() {
		return instrument;
	}

	public void setInstrument(String instrument) {
		this.instrument = instrument;
	}

	public String getVariable() {
		return variable;
	}

	public void setVariable(String variable) {
		this.variable = variable;
	}
    
}

