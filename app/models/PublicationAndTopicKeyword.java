/**
 * @author xingyuchen
 * Created on Mar 16, 2016
 */
package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * @author xingyuchen
 *
 */
@Entity
public class PublicationAndTopicKeyword {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	@ManyToOne(optional = false)
	@JoinColumn(name = "topicKeywordId", referencedColumnName = "id")
	private PublicationTopicKeyword topicKeyword;
	@ManyToOne(optional = false)
	@JoinColumn(name = "publicationId", referencedColumnName = "id")
	private Publications publication;
	private int count;
	
	
	public PublicationAndTopicKeyword() {
		super();
	}

	public PublicationAndTopicKeyword(PublicationTopicKeyword topicKeyword, Publications publication, int count) {
		super();
		this.topicKeyword = topicKeyword;
		this.publication = publication;
		this.count = count;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public PublicationTopicKeyword getTopicKeyword() {
		return topicKeyword;
	}

	public void setTopicKeyword(PublicationTopicKeyword topicKeyword) {
		this.topicKeyword = topicKeyword;
	}

	public Publications getPublication() {
		return publication;
	}

	public void setPublication(Publications publication) {
		this.publication = publication;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return "PublicationAndTopicKeyword [id=" + id + ", topicKeyword=" + topicKeyword + ", publication="
				+ publication + ", count=" + count + "]";
	}
	
	
}
