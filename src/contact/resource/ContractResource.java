/**
 * This class is the contact resource
 * @author wat wattanagaroon
 * @version 2014/09/16
 */
package contact.resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import contact.entity.Contact;
import contact.service.ContactDao;

@Path("/contacts")
@Singleton
public class ContractResource {

		private Map<String, String> greetings = new HashMap<>();
		private ContactDao dao = new ContactDao();
		@Context
		UriInfo uriInfo;
		
		/**
		 * get all of contact
		 * @return
		 */
		public Response getContact( ) {
				GenericEntity<List<Contact>> GenericEn = new GenericEntity<List<Contact>>(dao.findAll()){};
	            return Response.ok(GenericEn).build(); 
	            }
		
		/**
		 * get contact from id
		 * @param id that want to find contact
		 * @return contact from id
		 */
		@GET
	    @Path("{id}")
		@Produces( MediaType.APPLICATION_XML )
		public Response getContact( @PathParam("id") long id ) {
	            Contact contact = dao.find(id);
	            return Response.ok(contact).build(); 
	            }
		/**
		 * get contact from some word of title
		 * @param q string that want to find contact in title
		 * @return contact
		 */
		@GET
		@Produces( MediaType.APPLICATION_XML )
		public Response getContact( @QueryParam("q") String q ) {
				if(q == null){
					 return getContact(); 
				}
				else{
					return Response.ok(dao.searchTitle(q)).build(); 
				}
	    	}
		
		/**
		 * post new contact to the list of ContactDao
		 * @param element of the contact that want to add
		 * @param uriInfo 
		 * @return response if contact can save return created,
		 * @return if it can't save return bad request
		 * @return if Dao don't find contact id it is conflict
		 */
		@POST
		@Consumes({ MediaType.APPLICATION_XML})
		public Response addContact(JAXBElement<Contact> element, @Context UriInfo uriInfo ){
			Contact contact = element.getValue();
			if(dao.find(contact.getId())== null){
				if(dao.save(contact)){
					return Response.created(uriInfo.getAbsolutePathBuilder().path(contact.getId()+"").build()).build();
				}
				else{ //dao can't save
					return Response.status(Response.Status.BAD_REQUEST).build();
				}
			}
			else{ //dao find contact id
					return Response.status(Response.Status.CONFLICT).build();
			}
		}
		
		/**
		 * use to update some contact
		 * @param element that want to edit
		 * @param id that want to edit
		 * @return response if it can update response ok
		 * @return response bad request if it Contact dao can't update
		 */
		@PUT
		@Path ("{id}")
		public Response updateContact(JAXBElement<Contact> element , @PathParam ("id") long id){
			Contact contact = element.getValue();
			contact.setId(id);
			if(dao.update(contact)){
				return Response.ok(contact).build();
			}
			else{
				return Response.status(Response.Status.NOT_FOUND).build();
			}
		}
		
		/**
		 * delete the contact from id.
		 * @param id that want to delete
		 * @return response if it can delete
		 * @return not found if it can't delete
		 */
		@DELETE
		@Path("{id}")
		public Response deleteContact(@PathParam ("id") long id){
			if(dao.delete(id)){
				return Response.ok().build();
			}
			else{
				return Response.status(Response.Status.NOT_FOUND).build();
			}
		}		
}
	            
