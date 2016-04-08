/**
 * @author xingyuchen
 * Created on Feb 29, 2016
 */
package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author xingyuchen
 *
 */
@Entity
public class PublicationTopic {
	
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    private String topicName;
    private int publicationNum;
    
	public PublicationTopic() {
		super();
	}

	public PublicationTopic(String topicName, int publicationNum) {
		super();
		this.topicName = topicName;
		this.publicationNum = publicationNum;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public int getPublicationNum() {
		return publicationNum;
	}

	public void setPublicationNum(int publicationNum) {
		this.publicationNum = publicationNum;
	}

	@Override
	public String toString() {
		return "PublicationTopic [id=" + id + ", topicName=" + topicName + ", publicationNum=" + publicationNum + "]";
	}

}
