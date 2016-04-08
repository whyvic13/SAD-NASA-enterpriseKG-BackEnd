package models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import java.sql.*;

@Entity
public class Picture {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private String url;
	private Blob image;
	
	public Picture() {	
	}
	
	public Picture (String url) {
		super();
		this.url = url;
	}
	
	public Picture (Blob image) {
		super();
		this.image = image;
	}
	
	public Blob getImage() {
		return image;
	}
	
	public void setImage(Blob image) {
		this.image = image;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "Picture [id=" + id + ", image=" + image + "]";
	}
	
}
