package contact.service.mem;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import contact.entity.Contact;
import contact.entity.Contacts;
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
	public static final String EXTERNAL_FILE = "/tmp/contacts.xml";
	private Map<Long, Contact> contacts;
	
	public MemDaoFactory() {
//		this.createNotExistFile();
		daoInstance = new MemContactDao();
		this.loadFile();
	}
	
	/**
	 * TODO This method is not necessary. 
         * Fix the logic in loadFile (check if file exists) and delete this method.
	 */
	private void createNotExistFile() {
		File file = new File(EXTERNAL_FILE);
		// always save to file
		//if(!file.exists()){
			try {
				JAXBContext context = JAXBContext.newInstance( Contacts.class );
				File outputFile = new File( EXTERNAL_FILE );
				Marshaller marshaller = context.createMarshaller();	
				Contacts contacts = new Contacts();
// don't replace the contacts list
//				contacts.setContacts(new ArrayList<Contact>());
				marshaller.marshal( contacts, outputFile );
			} catch ( JAXBException e ) {
				e.printStackTrace();
			}
		//}
		
	}

	@Override
	public ContactDao getContactDao() {
		return daoInstance;
	}
	
	@Override
	public void shutdown() {
		System.out.println("Shutdown");
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

		////
		
	}
	
	public void loadFile() {
		File inputFile = new File( EXTERNAL_FILE );
		if (! inputFile.exists() ) return;
		try {
			
			JAXBContext context = JAXBContext.newInstance( Contacts.class ) ;

			Unmarshaller unmarshaller = context.createUnmarshaller();	
			Contacts importContacts = (Contacts) unmarshaller.unmarshal( inputFile );
			if ( importContacts.getContacts() == null ) {
				return;
			}
			for ( Contact contact : importContacts.getContacts() ) {
//JIM: You should let the ContactDao do this
// Its the responsibility of DAO to manage contacts!
//				contacts.put( contact.getId(), contact );
				daoInstance.save( contact );
			}
//JIM: don't catch "Exception"
//1) catch specific exceptions, not general ones.
//2) log it and handle gracefully.  Not printStackTrace.
		} catch ( Exception e ) {
			e.printStackTrace();
		} 
	}
	
	public static void main(String[] args) {
		MemDaoFactory factory = new MemDaoFactory();
		factory.loadFile();
	}

}
