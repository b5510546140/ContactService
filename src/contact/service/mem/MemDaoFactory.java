package contact.service.mem;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import contact.entity.Contact;
import contact.entity.Contacts;
import contact.service.ContactDao;
import contact.service.DaoFactory;
import contact.service.jpa.JpaDaoFactory;

/**
 * MemDaoFactory is a factory for getting instances of entity DAO object
 * that use memory-based persistence, which isn't really persistence at all!
 * 
 * @see contact.service.DaoFactory
 * @version 2014/09/27
 * @author jim and wat wattanagaroon
 */
public class MemDaoFactory extends DaoFactory {
	/** instance of the entity DAO */
	private ContactDao daoInstance;
	public static final String EXTERNAL_FILE = "/tmp/contactstemp.xml";
	private static Logger logger;
	static {
		logger = Logger.getLogger(MemDaoFactory.class.getName());
	}
	public MemDaoFactory() {

		daoInstance = new MemContactDao();
		this.loadFile();
	}

	@Override
	public ContactDao getContactDao() {
		return daoInstance;
	}
	/**
	 * Shut down the server and save all of data to file
	 */
	@Override
	public void shutdown() {
		List<Contact> list = getContactDao().findAll();
		Contacts exportListOfContact = new Contacts();
		exportListOfContact.setContacts(list);
		try {
			JAXBContext context = JAXBContext.newInstance( Contacts.class );
			File outputFile = new File( EXTERNAL_FILE );
			Marshaller marshaller = context.createMarshaller();	
			marshaller.marshal( exportListOfContact, outputFile );
		} catch ( JAXBException e ) {
			e.printStackTrace();
		}
		
	}
	/**
	 * load file and if file don't exist create a new file
	 */
	public void loadFile() {
		File inputFile = new File( EXTERNAL_FILE );
		if(!inputFile.exists()){
			try {
				JAXBContext context = JAXBContext.newInstance( Contacts.class );
				File outputFile = new File( EXTERNAL_FILE );
				Marshaller marshaller = context.createMarshaller();	
				Contacts contacts = new Contacts();
				marshaller.marshal( contacts, outputFile );
			} catch ( JAXBException e ) {
				e.printStackTrace();
			}
		}
			
			JAXBContext context = null;
			try {
				context = JAXBContext.newInstance( Contacts.class );
			} catch (JAXBException e) {
				logger.log(Level.SEVERE,e+"");
			}

			Unmarshaller unmarshaller = null;
			try {
				unmarshaller = context.createUnmarshaller();
			} catch (JAXBException e) {
				logger.log(Level.SEVERE,e+"");
			}	
			Contacts importContacts = null;
			try {
				importContacts = (Contacts) unmarshaller.unmarshal( inputFile );
			} catch (JAXBException e) {
				logger.log(Level.SEVERE,e+"");
			}
			if ( importContacts.getContacts() == null ) {
				return;
			}
			for ( Contact contact : importContacts.getContacts() ) {

				daoInstance.save( contact );
			}
	}
	/**
	 * test of create empty file
	 * @param args
	 */
	public static void main(String[] args) {
		MemDaoFactory factory = new MemDaoFactory();
		factory.loadFile();
	}

}
