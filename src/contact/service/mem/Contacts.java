package contact.service.mem;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import contact.entity.Contact;

@XmlRootElement(name="contacts")
@XmlAccessorType(XmlAccessType.FIELD)
public class Contacts {
	
	@XmlElement(name="contact")
    private List<Contact> contactsList;

	public List<Contact> getContacts() {
		return contactsList;
	}

	public void setContacts( List<Contact> contactsList ) {
		this.contactsList = contactsList;
	}
	
}