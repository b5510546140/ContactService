package contact.entity;
import javax.persistence.*;

/**
 * @author wat wattanagaroon
 * @version 2014/09/27
 */
import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
@Entity
@Table(name="contact")
/**
 * A person is a contact with a name, title, and email.
 * title is text to display for this contact in a list of contacts,
 * such as a nickname or company name.
 */
@XmlRootElement(name="contact")
@XmlAccessorType(XmlAccessType.FIELD)
public class Contact extends Md5 implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@XmlAttribute
	private long id;
	private String name;
	private String title;
	private String email;
	private String phoneNumber;

	public Contact() { }

	public Contact(String title, String name, String email, String phoneNumber ) {
		this.title = title;
		this.name = name;
		this.email = email;
		this.phoneNumber = phoneNumber;
	}

	public Contact(int id,String title, String name, String email, String phoneNumber ) {
		this.id = id;
		this.title = title;
		this.name = name;
		this.email = email;
		this.phoneNumber = phoneNumber;
	}

	public Contact(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@Override
	public String toString() {
		return String.format("[%ld] %s (%s)", id, name, title);
	}

	/** Two contacts are equal if they have the same id,
	 * even if other attributes differ.
	 * @param other another contact to compare to this one.
	 */
	public boolean equals(Object other) {
		if (other == null || other.getClass() != this.getClass()) return false;
		Contact contact = (Contact) other;
		return contact.getId() == this.getId();
	}

	/**
	 * Update this contact's data from another Contact.
	 * The id field of the update must either be 0 or the same value as this contact!
	 * @param update the source of update values
	 */
	public void applyUpdate(Contact update) {
		if (update == null) return;
		if (update.getId() != 0 && update.getId() != this.getId() )
			throw new IllegalArgumentException("Update contact must have same id as contact to update");
		// Since title is used to display contacts, don't allow empty title
		if (! isEmpty( update.getTitle()) ) this.setTitle(update.getTitle()); // empty nickname is ok
		// other attributes: allow an empty string as a way of deleting an attribute in update (this is hacky)
		if (update.getName() != null ) this.setName(update.getName()); 
		else this.setName("");
		if (update.getEmail() != null) this.setEmail(update.getEmail());
		else this.setEmail("");
		if (update.getPhoneNumber() != null ) this.setPhoneNumber(update.getPhoneNumber());
		else this.setPhoneNumber("");
	}

	/**
	 * Test if a string is null or only whitespace.
	 * @param arg the string to test
	 * @return true if string variable is null or contains only whitespace
	 */
	private static boolean isEmpty(String arg) {
		return arg == null || arg.matches("\\s*");
	}

	@Override
	public String getMd5() {
		String data = (getId() + getTitle() + getName() + getEmail() + getPhoneNumber());
		return super.digest( data );
	}

}