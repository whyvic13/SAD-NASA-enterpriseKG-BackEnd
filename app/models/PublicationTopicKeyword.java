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
public class PublicationTopicKeyword {
	
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    private String topicKeywordName;
    // Just use for json
    private int count;
    
	public PublicationTopicKeyword() {
		super();
	}

	public PublicationTopicKeyword(String topicKeywordName) {
		super();
		this.topicKeywordName = topicKeywordName;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTopicKeywordName() {
		return topicKeywordName;
	}

	public void setTopicKeywordName(String topicKeywordName) {
		this.topicKeywordName = topicKeywordName;
	}

	@Override
	public String toString() {
		return "PublicationTopicKeyword [id=" + id + ", topicKeywordName=" + topicKeywordName + "]";
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
