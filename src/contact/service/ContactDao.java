package contact.service;

import java.util.List;

import contact.entity.Contact;

public interface ContactDao {

	/** Find a contact by ID in contacts.
	 * @param the id of contact to find
	 * @return the matching contact or null if the id is not found
	 */
	public abstract Contact find(long id);

	/**
	 * find all of the contact
	 * @return collection of all contact
	 */
	public abstract List<Contact> findAll();

	/**
	 * Delete a saved contact.
	 * @param id the id of contact to delete
	 * @return true if contact is deleted, false otherwise.
	 */
	public abstract boolean delete(long id);

	/**
	 * Save or replace a contact.
	 * If the contact.id is 0 then it is assumed to be a
	 * new (not saved) contact.  In this case a unique id
	 * is assigned to the contact.  
	 * If the contact.id is not zero and the contact already
	 * exists in saved contacts, the old contact is replaced.
	 * @param contact the contact to save or replace.
	 * @return true if saved successfully
	 */
	public abstract boolean save(Contact contact);

	/**
	 * Update a Contact.  Only the non-null fields of the
	 * update are applied to the contact.
	 * @param update update info for the contact.
	 * @return true if the update is applied successfully.
	 */
	public abstract boolean update(Contact update);

	/**
	 * search the contact from title
	 * @param q word to find in title
	 * @return contact that find from title
	 */

	/**
	 * search is that is exist or not
	 * @param id of contact that want to find
	 * @return true if find contact
	 */
	public abstract boolean isExisted(long id);
	/**
	 * find from title
	 * @param titlestr
	 * @return
	 */
	public abstract List<Contact> findByTitle(String titlestr);
	
	public abstract void removeAll();

}