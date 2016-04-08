package models;

import javax.persistence.*;
import java.util.Set;

@Entity
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	private String userName;
	private String password;
	private String firstName;
	private String lastName;
	private String middleInitial;
	private String affiliation;
	private String title;
	private String email;
	private String mailingAddress;
	private String phoneNumber;
	private String faxNumber;
	private String researchFields;
	private String highestDegree;
	private boolean unreadMention;

	//Merged from Team 15&16
	private String avatar;
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(	name = "Followers",
			joinColumns = { @JoinColumn(name ="userId", referencedColumnName = "id")},
			inverseJoinColumns = { @JoinColumn(name = "followerId", referencedColumnName = "id") })
	protected Set<User> followers;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(	name = "friendRequests",
			joinColumns = { @JoinColumn(name ="userId", referencedColumnName = "id")},
			inverseJoinColumns = { @JoinColumn(name = "senderId", referencedColumnName = "id") })
	protected Set<User> friendRequestSender;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(	name = "friendship",
			joinColumns = { @JoinColumn(name ="userAId", referencedColumnName = "id")},
			inverseJoinColumns = { @JoinColumn(name = "userBId", referencedColumnName = "id") })
	protected Set<User> friends;

	public User(String userName, String email, String password) {
		this.userName = userName;
		this.email = email;
		this.password = password;
	}

	public User(String userName, String password,
				String email, String phoneNumber) {
		super();
		this.userName = userName;
		this.password = password;
		this.email = email;
		this.phoneNumber = phoneNumber;
	}
	public Set<User> getFollowers(){ return this.followers; }

	public void setFollowers(Set<User> followers) {
		this.followers = followers;
	}

	public void setFriendRequestSender(Set<User> friendRequestSender) {
		this.friendRequestSender = friendRequestSender;
	}
	public Set<User> getFriendRequestSender() {	return this.friendRequestSender;}

	public void setFriends(Set<User> friends) {this.friends = friends;}

	public Set<User> getFriends() {return this.friends;}


	public String toJson() {
		return "{\"User\":{\"id\":\"" + id + "\", \"userName\":\"" + userName
				+ "\", \"password\":\"" + password + "\", \"email\":\"" + email + "\", \"avatar\":\"" + avatar

				+ "\", \"phoneNumber\":\"" + phoneNumber + "\"}}";
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}



	// @OneToMany(mappedBy = "user", cascade={CascadeType.ALL})
	// private Set<ClimateService> climateServices = new
	// HashSet<ClimateService>();

	public User() {
	}

	public User(String userName, String password, String firstName,
			String lastName, String middleInitial, String affiliation,
			String title, String email, String mailingAddress,
			String phoneNumber, String faxNumber, String researchFields,
			String highestDegree) {
		super();
		this.userName = userName;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.middleInitial = middleInitial;
		this.affiliation = affiliation;
		this.title = title;
		this.email = email;
		this.mailingAddress = mailingAddress;
		this.phoneNumber = phoneNumber;
		this.faxNumber = faxNumber;
		this.researchFields = researchFields;
		this.highestDegree = highestDegree;
	}

	public long getId() {
		return id;
	}

	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getMiddleInitial() {
		return middleInitial;
	}

	public String getAffiliation() {
		return affiliation;
	}

	public String getTitle() {
		return title;
	}

	public String getEmail() {
		return email;
	}

	public String getMailingAddress() {
		return mailingAddress;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getFaxNumber() {
		return faxNumber;
	}

	public String getResearchFields() {
		return researchFields;
	}

	public String getHighestDegree() {
		return highestDegree;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setMiddleInitial(String middleInitial) {
		this.middleInitial = middleInitial;
	}

	public void setAffiliation(String affiliation) {
		this.affiliation = affiliation;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setMailingAddress(String mailingAddress) {
		this.mailingAddress = mailingAddress;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void setFaxNumber(String faxNumber) {
		this.faxNumber = faxNumber;
	}

	public void setResearchFields(String researchFields) {
		this.researchFields = researchFields;
	}

	public void setHighestDegree(String highestDegree) {
		this.highestDegree = highestDegree;
	}
	
	public boolean isUnreadMention() {
		return unreadMention;
	}

	public void setUnreadMention(boolean unreadMention) {
		this.unreadMention = unreadMention;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", userName=" + userName + ", password="
				+ password + ", firstName=" + firstName + ", lastName="
				+ lastName + ", middleInitial=" + middleInitial
				+ ", affiliation=" + affiliation + ", title=" + title
				+ ", email=" + email + ", mailingAddress=" + mailingAddress
				+ ", phoneNumber=" + phoneNumber + ", faxNumber=" + faxNumber
				+ ", researchFields=" + researchFields + ", highestDegree="
				+ highestDegree + ", unreadMention=" + unreadMention + "]";
	}

}

