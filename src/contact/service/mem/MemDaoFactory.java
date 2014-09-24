package contact.service.mem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import contact.entity.Contact;
import contact.service.ContactDao;
import contact.service.DaoFactory;

/**
 * MemDaoFactory is a factory for getting instances of entity DAO object
 * that use memory-based persistence, which isn't really persistence at all!
 * 
 * @see contact.service.DaoFactory
 * @version 2014.09.19
 * @author jim
 */
public class MemDaoFactory extends DaoFactory {
	/** instance of the entity DAO */
	private ContactDao daoInstance;
	public static final String EXTERNAL_FILE = "ContactsSevice.xml";
	private Map<Long, Contact> contacts;
	
	public MemDaoFactory() {
		this.createNotExistFile();
		this.loadFile();
		daoInstance = new MemContactDao();
	}
	
	private void createNotExistFile() {
		File file = new File(EXTERNAL_FILE);
		if(!file.exists()){
			try {
				JAXBContext context = JAXBContext.newInstance( Contacts.class );
				File outputFile = new File(EXTERNAL_FILE );
				Marshaller marshaller = context.createMarshaller();	
				Contacts contacts = new Contacts();
				contacts.setContacts(new ArrayList<Contact>());
				marshaller.marshal( contacts, outputFile );
			} catch ( JAXBException e ) {
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public ContactDao getContactDao() {
		return daoInstance;
	}
	
	@Override
	public void shutdown() {
		List<Contact> list = getContactDao().findAll();
		Contacts exportListOfContact = new Contacts();
		exportListOfContact.setContacts(list);
		try {
			JAXBContext context = JAXBContext.newInstance( Contacts.class );
			File outputFile = new File(EXTERNAL_FILE );
			Marshaller marshaller = context.createMarshaller();	
			marshaller.marshal( exportListOfContact, outputFile );
		} catch ( JAXBException e ) {
			e.printStackTrace();
		}
		System.out.println("Shutdown");
		////
		
	}
	
	public void loadFile() {
		try {
			Contacts importContacts = new Contacts();
			JAXBContext context = JAXBContext.newInstance( Contacts.class ) ;
			File inputFile = new File( MemDaoFactory.EXTERNAL_FILE );
			Unmarshaller unmarshaller = context.createUnmarshaller();	
			importContacts = (Contacts) unmarshaller.unmarshal( inputFile );
			if ( importContacts.getContacts() == null ) {
				return;
			}
			for ( Contact contact : importContacts.getContacts() ) {
				contacts.put( contact.getId(), contact );
			}
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

}
