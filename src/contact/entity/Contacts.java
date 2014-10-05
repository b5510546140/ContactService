package contact.entity;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Wrapper for list of contacts, for JAXB serialization.
 */
@XmlRootElement(name="contacts")
@XmlAccessorType(XmlAccessType.FIELD)
public class Contacts extends Md5{

	@XmlElement(name="contact")
	private List<Contact> contactsList;

	public List<Contact> getContacts() {
		return contactsList;
	}

	public void setContacts( List<Contact> contactsList ) {
		this.contactsList = contactsList;
	}

	@Override
	public String getMd5() {
		StringBuilder data = new StringBuilder();
		for ( Contact c : getContacts() ) {
			data.append( c.getMd5() );
		}
		return super.digest( data.toString() );
	}
}