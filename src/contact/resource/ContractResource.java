/**
 * This class is the contact resource
 * @author wat wattanagaroon
 * @version 2014/09/16
 */
package contact.resource;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

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
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import contact.entity.Contact;
import contact.service.ContactDao;
import contact.service.DaoFactory;

@Path("/contacts")
@Singleton
public class ContractResource {

	private ContactDao dao;
	@Context
	UriInfo uriInfo;

	public ContractResource() {
		dao = DaoFactory.getInstance().getContactDao();
	}

	/**
	 * get all of contact
	 * 
	 * @return
	 */
	public Response getContact() {
		System.out.println(uriInfo.getRequestUri());
		System.out.println(uriInfo.getAbsolutePath());

		GenericEntity<List<Contact>> GenericEn = new GenericEntity<List<Contact>>(
				dao.findAll()) {
		};
		return Response.ok(GenericEn).build();
	}

	/**
	 * get contact from id
	 * 
	 * @param id
	 *            that want to find contact
	 * @return contact from id
	 */
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_XML)
	public Response getContact(@PathParam("id") long id,
			@Context Request request) {
		Response.ResponseBuilder rb = null;
		Contact contact = dao.find(id);
		if (contact == null) return Response.status(Response.Status.NOT_FOUND).build();
		EntityTag etag = new EntityTag(contact.getMd5());
		// Verify if it matched with etag available in http request
		rb = request.evaluatePreconditions(etag);
		if (rb != null) {
			return rb.tag(etag).build();
		}
		Contact contactfind = dao.find(id);
		if (contactfind == null) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		// If rb is null then either it is first time request; or resource is
		// modified
		// Get the updated representation and return with Etag attached to it
		rb = Response.ok(contactfind).tag(etag);
		return rb.build();

		// if(contact == null){
		// return Response.status(Response.Status.NO_CONTENT).tag(etag).build();
		// }
		// return Response.ok(contact).tag(etag).build();
	}

	/**
	 * get contact from some word of title
	 * 
	 * @param q
	 *            string that want to find contact in title
	 * @return contact
	 */
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public Response getContact(@QueryParam("q") String q) {
		if (q == null) {
			return getContact();
		} else {
			 List<Contact> contact = dao.findByTitle(q);
			if (contact == null) {
				return Response.status(Response.Status.NO_CONTENT).build();
			}
			return Response.ok(contact).build();
		}
	}

	/**
	 * post new contact to the list of ContactDao
	 * 
	 * @param element
	 *            of the contact that want to add
	 * @param uriInfo
	 * @return response if contact can save return created,
	 * @return if it can't save return bad request
	 * @return if Dao don't find contact id it is conflict
	 */
	@POST
	@Consumes({ MediaType.APPLICATION_XML })
	public Response postContact(JAXBElement<Contact> contact) {
		Contact c = (Contact) contact.getValue();
		EntityTag etag = new EntityTag(c.getMd5());

		if (dao.find(c.getId()) == null) {
			boolean success = dao.save(c);
			if (success) {
				try {
					return Response
							.created(new
									URI( uriInfo.getRequestUri()+(c.getId()+"")))
							.type(MediaType.APPLICATION_XML).entity(contact)
							.tag(etag).build();
				} catch (URISyntaxException e) {
				}
			}
			return Response.status(Response.Status.BAD_REQUEST).build();
		} else {
			return Response.status(Response.Status.CONFLICT)
					.location(uriInfo.getRequestUri()).entity(contact).build();
		}

	}

	/**
	 * use to update some contact
	 * 
	 * @param element
	 *            that want to edit
	 * @param id
	 *            that want to edit
	 * @return response if it can update response ok
	 * @return response bad request if it Contact dao can't update
	 */
	@PUT
	@Path("{id}")
	@Consumes({MediaType.APPLICATION_XML , MediaType.TEXT_XML})
	public Response updateContact(JAXBElement<Contact> element,
			@PathParam("id") long id, @Context Request request) {
		Contact contact = element.getValue();
		Contact contactfind = dao.find(id);
		contact.setId(id);
		
		if (contactfind == null)
			return Response.status(Response.Status.NOT_FOUND).build();

		Response.ResponseBuilder rb = null;
//		 Verify if it matched with etag available in http request
		EntityTag etag = new EntityTag(contactfind.getMd5());
		rb = request.evaluatePreconditions(etag);
		if (rb != null) {
			return rb.build();
		}
		if (dao.update(contact)) {
			return Response.ok(contact).build();
		} else {
			return Response.status(Response.Status.PRECONDITION_FAILED).build();
		}
	}

	/**
	 * delete the contact from id.
	 * 
	 * @param id
	 *            that want to delete
	 * @return response if it can delete
	 * @return not found if it can't delete
	 */
	@DELETE
	@Path("{id}")
	public Response deleteContact(@PathParam("id") long id,
			@Context Request request) {
		Contact contact = dao.find(id);
		Response.ResponseBuilder rb = null;
		EntityTag etag = new EntityTag(contact.getMd5());
		// Verify if it matched with etag available in http request
		rb = request.evaluatePreconditions(etag);
		if (rb != null) {
			return rb.tag(etag).build();
		}
		if(dao.find(id)==null){
			return Response.status(Response.Status.NOT_FOUND).tag(etag).build();
		}
		if (dao.delete(id)) {
			return Response.ok().tag(etag).build();
		} else {
			return Response.status(Response.Status.BAD_REQUEST).tag(etag)
					.build();
		}
	}
		
}
