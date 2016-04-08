/**
 * @author xingyuchen
 * Created on Feb 29, 2016
 */
package models;

import javax.persistence.Column;
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
public class PublicationFigure {
	
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
	@ManyToOne(optional = false)
	@JoinColumn(name = "publicationId", referencedColumnName = "id")
	private Publications publication;
    private String path;
    @Column(length = 2000)
    private String caption;
    
    public PublicationFigure() {
		
	}
    
	public PublicationFigure(Publications publication, String path, String caption) {
		super();
		this.publication = publication;
		this.path = path;
		this.caption = caption;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Publications getPublication() {
		return publication;
	}

	public void setPublication(Publications publication) {
		this.publication = publication;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}
	
	@Override
	public String toString() {
		return "PublicationFigure [id=" + id + ", publication=" + publication + ", path=" + path + ", caption="
				+ caption + "]";
	}

}
