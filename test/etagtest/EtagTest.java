package etagtest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.ws.rs.core.Response.Status;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import contact.entity.Contact;
import contact.main.JettyMain;
import contact.service.ContactDao;
import contact.service.DaoFactory;

public class EtagTest {
	private static ContactDao dao = DaoFactory.getInstance().getContactDao();;
	 private static String uri;
	private static Contact contacttest1;
	private static Contact contacttest2;
	private static Contact contacttest3;
	private static final int PORT = 8080;
	private HttpClient client;
	private static String serviceUrl;
	/**
	 * Start the service.
	 */
	@BeforeClass
	public static void doFirst( ) {
		serviceUrl = JettyMain.startServer(8080);
		serviceUrl += "contacts/";
		contacttest1 = new Contact( "contact1", "Joe Contact", "joe@microsoft.com", "0123456789" );
		contacttest2 = new Contact( 1456, "contact2", "Sally Contract", "sally@foo.com", "0123456780" );
		contacttest3 = new Contact( 4455, "contact2", "Sally Contract", "sally@foo.com", "0123456780" );
	}
	
	/**
	 * Stop the service.
	 */
	@AfterClass
	public static void doLast( ) {
		JettyMain.stopServer();
	}
	

	@Before
	public void beforeTest() {
		client = new HttpClient();
		try {
			client.start();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

	/**
	 * Stop the client,
	 * help the server load on abundant client connections.
	 */
	@After
	public void afterTest() {
		try {
			client.stop();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
	
	public void delete(long id){
		dao.delete(id);
	}


	@Test
	public void testGet() {
		Request request = client.newRequest(serviceUrl+101)
				.header(HttpHeader.IF_NONE_MATCH, null).method(HttpMethod.GET);
		ContentResponse ctr = null;		
			try {
				ctr = request.send();
			
		assertEquals("Should be 200 OK and get new Data",
				Status.OK.getStatusCode(), ctr.getStatus());
		
		//ETag of ID 101 print
		String etag = ctr.getHeaders().get(HttpHeader.ETAG);
		System.out.println("ETag of Contact 101 is " + etag);
		// etag is not empty
		assertTrue("Contacts ID 101 have ETag",!etag.isEmpty());
		
		request = client.newRequest(serviceUrl+101)
				.header(HttpHeader.IF_NONE_MATCH, etag).method(HttpMethod.GET);
		ctr = request.send();
		assertEquals("Should be 304 NOT MODIFIED",
				Status.NOT_MODIFIED.getStatusCode(), ctr.getStatus());
			}
		catch (InterruptedException | TimeoutException
				| ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testPost() {
		StringContentProvider content = new StringContentProvider(
				"<contact id=\"1\">\n"
						+ "<name>wat wattanagaroon</name>"
						+ "<email>eiei@hotmail.com</email>\n"
						+ "</contact>");

		Request request = client.newRequest(serviceUrl).content(content, "application/xml").method(HttpMethod.POST);
		try {
			ContentResponse response = request.send();
			assertEquals( "Should return 201 CREATED", Status.CREATED.getStatusCode(), response.getStatus() );
			String etag = response.getHeaders().get("ETag");
			assertTrue("ETag must be existed", etag != null);
			
			//test same value
			request = client.newRequest(serviceUrl).content(content, "application/xml").method(HttpMethod.POST);
			response = request.send();
			assertEquals( "Should response 409 Conflict", Status.CONFLICT.getStatusCode(), response.getStatus());
			etag = response.getHeaders().get("ETag");
			assertTrue("ETag must be existed", etag == null);
			
			//third test
			content = new StringContentProvider("<contact id=\"106\">\n"
					+ "<emaiasdl>aaa.gmail.com</email>\n"
					+ "</contact>");
			request = client.newRequest(serviceUrl).content(content, "application/xml").method(HttpMethod.POST);
			response = request.send();
			assertEquals("Should response 400 Bad request",
			Status.BAD_REQUEST.getStatusCode(), response.getStatus());
			etag = response.getHeaders().get("ETag");
			assertTrue("ETag must be existed", etag == null);
			
			
		} catch (InterruptedException | TimeoutException | ExecutionException e) {
			e.printStackTrace();
		}
		delete(1);
		
	}
	
	@Test
	public void testPut() {
		StringContentProvider content = new StringContentProvider(
				"<contact> <name>wat wattanagaroon</name>"
						+"<title>puttest</title>"
						+ "<email>happyoff@hotmail.com</email>\n"
						+"<phoneNumber>11111</phoneNumber>"
						+ "</contact>");
		
		
		Request request = client.newRequest(serviceUrl + 101).method(
				HttpMethod.GET);
		ContentResponse ctr;
		try {
			ctr = request.send();
			String etag = ctr.getHeaders().get(HttpHeader.ETAG);
			System.out.println("ETAG from server : " + etag);
			// first put
			request = client.newRequest(serviceUrl + 101)
					.content(content, "application/xml").header(HttpHeader.IF_MATCH, etag )
					.method(HttpMethod.PUT);
			ctr = request.send();
			assertEquals("PUT success Should response 200 OK",
					Status.OK.getStatusCode(), ctr.getStatus());
			assertEquals("Email of Contact 101 is happyoff@hotmail.com", dao
					.find(101).getEmail(), "happyoff@hotmail.com");
			etag = ctr.getHeaders().get(HttpHeader.ETAG);
			//test with wrong etag
			request = client.newRequest(serviceUrl + 101)
					.content(content, "application/xml")
					.header(HttpHeader.IF_MATCH, "\"100000dd20\"").method(HttpMethod.PUT);
			ctr = request.send();
			assertEquals("PUT not success Should 412 Precondition Failed",
					Status.PRECONDITION_FAILED.getStatusCode(), ctr.getStatus());
		} catch (InterruptedException | TimeoutException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testDELETE() {
		StringContentProvider content = new StringContentProvider(
				"<contact id=\"999\">"
						+ "<title>Test contact</title>"
						+ "<name>Joe Experimental</name>"
						+ "<email>none@testing.com</email>" + "</contact>");
		Request request = client.newRequest(serviceUrl)
				.content(content, "application/xml")
				.method(HttpMethod.POST);
		ContentResponse ctr;
		try {
			ctr = request.send();
			String etag = ctr.getHeaders().get(HttpHeader.ETAG);
			//first request to delete If-Match not match
			request = client.newRequest(serviceUrl + 999)
					.header(HttpHeader.IF_MATCH, "\"" + "sadasdasd" + "\"")
					.method(HttpMethod.DELETE);
			ctr = request.send();
					assertEquals("DELETE not success 412 Precondition Failed",
					Status.PRECONDITION_FAILED.getStatusCode(), ctr.getStatus());

					request = client.newRequest(serviceUrl + 999)
							.header(HttpHeader.IF_MATCH, etag)
							.method(HttpMethod.DELETE);
					ctr = request.send();
					assertEquals("DELETE success response 200 OK",
							Status.OK.getStatusCode(), ctr.getStatus());
					
					assertTrue("Is it really deleted", dao.find(999) == null);
			
		} catch (InterruptedException | TimeoutException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
