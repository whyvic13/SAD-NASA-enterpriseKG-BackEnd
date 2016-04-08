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
public class PublicationAndAuthor {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	@ManyToOne(optional = false)
	@JoinColumn(name = "authorId", referencedColumnName = "id")
	private Author author;
	@ManyToOne(optional = false)
	@JoinColumn(name = "publicationId", referencedColumnName = "id")
	private Publications publication;
	
	public PublicationAndAuthor() {
		super();
	}

	public PublicationAndAuthor(Author author, Publications publication) {
		super();
		this.author = author;
		this.publication = publication;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

	public Publications getPublication() {
		return publication;
	}

	public void setPublication(Publications publication) {
		this.publication = publication;
	}

	@Override
	public String toString() {
		return "PublicationAndAuthor [id=" + id + ", author=" + author + ", publication=" + publication + "]";
	}
	
	

}
