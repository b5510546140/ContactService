import static org.junit.Assert.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.ws.rs.core.Response.Status;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpMethod;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import contact.main.JettyMain;
/**
 * 
 * @author wat wattanagaroon
 * @version 2014/09/27
 */

public class WebServiceTest {

	 private static String serviceUrl;
	 private static HttpClient client;
	 
	 @BeforeClass
	 public static void doFirst() throws Exception {
		 // Start the Jetty server. 
		 serviceUrl = JettyMain.startServer( 8080 );
		 client = new HttpClient();
		 client.start();
	 }
	 
	 @AfterClass
	 public static void doLast( ) {
		 // stop the Jetty server after the last test
		 JettyMain.stopServer();
	 }
	 
	 @Test
	 public void testGetPass() throws InterruptedException, ExecutionException, TimeoutException {
		 ContentResponse res = client.GET(serviceUrl+"contacts");
		 assertEquals("The response should be 200 OK", Status.OK.getStatusCode(), res.getStatus());
		 assertTrue("Have body content", !res.getContentAsString().isEmpty());
	 }
	 
	 @Test
	 public void testGetFail() throws InterruptedException, ExecutionException, TimeoutException {
		 ContentResponse res = client.GET(serviceUrl+"contacts/12345");
		 assertEquals("The response should be 204 No Content", Status.NO_CONTENT.getStatusCode(), res.getStatus());
		 assertTrue("Empty Content", res.getContentAsString().isEmpty());
	 }

	 @Test
	 public void testPostPass() throws InterruptedException, ExecutionException, TimeoutException {
		 StringContentProvider content = new StringContentProvider("<contact id=\"123\">" +
					"<title>Titile of test post</title>" +
					"<name>Full Name</name>" +
					"<email>wat wattanagaroon</email>" +
					"<phoneNumber>555555555</phoneNumber>"+
					"</contact>");
		 Request request = client.newRequest(serviceUrl+"contacts");
		 request.method(HttpMethod.POST);
		 request.content(content, "application/xml");
		 ContentResponse res = request.send();
		
		 assertEquals("POST complete ,should response 201 Created", Status.CREATED.getStatusCode(), res.getStatus());
		 res = client.GET(serviceUrl+"contacts/123");
		 assertTrue("Check by using GET ,request posted id.", !res.getContentAsString().isEmpty() );
	 }
	 
	 @Test
	 public void testPostFail() throws InterruptedException, TimeoutException, ExecutionException {
		 StringContentProvider content = new StringContentProvider("<contact id=\"100\">" +
					"<title>Titile of test post</title>" +
					"<name>Full Name</name>" +
					"<email>wat wattanagaroon</email>" +
					"<phoneNumber>555555555</phoneNumber>"+
					"</contact>");
		 Request request = client.newRequest(serviceUrl+"contacts");
		 request.method(HttpMethod.POST);
		 request.content(content, "application/xml");
		 ContentResponse res = request.send();
		 
		 assertEquals("Should response CONFLICT because the id is already exist", Status.CONFLICT.getStatusCode(), res.getStatus());
	 }

	 @Test
	 public void testPutPass() throws InterruptedException, TimeoutException, ExecutionException {
		 StringContentProvider content = new StringContentProvider("<contact id=\"100\">" +
					"<title>Titile of test post</title>" +
					"<name>Full Name</name>" +
					"<email>wat wattanagaroon</email>" +
					"<phoneNumber>555555555</phoneNumber>"+
					"</contact>");
		 Request request = client.newRequest(serviceUrl+"contacts/100");
		 request.method(HttpMethod.PUT);
		 request.content(content, "application/xml");
		 ContentResponse res = request.send();
		 
		 assertEquals("PUT Success should response 200 OK", Status.OK.getStatusCode(), res.getStatus());
	 }
	 
	 @Test
	 public void testPutFail() throws InterruptedException, TimeoutException, ExecutionException {
		 StringContentProvider content = new StringContentProvider("<contact id=\"555\">" +
					"<title>Titile of test post</title>" +
					"<name>Full Name</name>" +
					"<email>wat wattanagaroon</email>" +
					"<phoneNumber>555555555</phoneNumber>"+
					"</contact>");
		 Request request = client.newRequest(serviceUrl+"contacts/555");
		 request.method(HttpMethod.PUT);
		 request.content(content, "application/xml");
		 ContentResponse res = request.send();
		 
		 assertEquals("PUT Fail should response 400 BAD REQUEST", Status.BAD_REQUEST.getStatusCode(), res.getStatus());
	 }
	 
	 @Test
	 public void testDeletePass() throws InterruptedException, ExecutionException, TimeoutException {
		 Request request = client.newRequest(serviceUrl+"contacts/123");
		 request.method(HttpMethod.DELETE);
		 ContentResponse res = request.send();
		 
		 assertEquals("DELETE success should response 200 OK", Status.OK.getStatusCode(), res.getStatus());
		 res = client.GET(serviceUrl+"contacts/123");
		 assertTrue("Really deleted", res.getContentAsString().isEmpty());
	 }
	 
	 @Test
	 public void testDeleteFail() throws InterruptedException, TimeoutException, ExecutionException {
		 Request request = client.newRequest(serviceUrl+"contacts/999");
		 request.method(HttpMethod.DELETE);
		 ContentResponse res = request.send();
		 
		 assertEquals("Contact does not exist should response 404 NOT FOUND", Status.NOT_FOUND.getStatusCode(), res.getStatus());
	 }


}