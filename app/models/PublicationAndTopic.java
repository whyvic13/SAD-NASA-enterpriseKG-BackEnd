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
public class PublicationAndTopic {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	@ManyToOne(optional = false)
	@JoinColumn(name = "topicId", referencedColumnName = "id")
	private PublicationTopic topic;
	@ManyToOne(optional = false)
	@JoinColumn(name = "publicationId", referencedColumnName = "id")
	private Publications publication;
	
	public PublicationAndTopic() {
		super();
	}

	public PublicationAndTopic(PublicationTopic topic, Publications publication) {
		super();
		this.topic = topic;
		this.publication = publication;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public PublicationTopic getTopic() {
		return topic;
	}

	public void setTopic(PublicationTopic topic) {
		this.topic = topic;
	}

	public Publications getPublication() {
		return publication;
	}

	public void setPublication(Publications publication) {
		this.publication = publication;
	}

	@Override
	public String toString() {
		return "PublicationAndTopic [id=" + id + ", topic=" + topic + ", publication=" + publication + "]";
	}
	
	
}
