package contact.main;
/**
 * @author wat wattanagaroon
 * @version 2014/09/16
 */
import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ServerProperties;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

import contact.resource.*;
import contact.service.DaoFactory;
import contact.service.mem.MemDaoFactory;

/**public class JettyMain {
	static final int PORT = 8080;
	
	public static void main(String[] args) throws Exception {
		int port = PORT;  // the port the server will listen to for HTTP requests
		Server server = new Server( port );
		
		// (1) Use a ServletContextHandler to hold a "context" (our application)
		// that will be deployed on the server.
		// The parameter is a bitwise "or" of options, defined in ServletContextHandler.
		// Options are: SESSIONS, NO_SESSIONS, SECURITY, NO_SECURITY
		ServletContextHandler context = new ServletContextHandler( ServletContextHandler.SESSIONS );
		context.setContextPath("/");
		ServletHolder holder = new ServletHolder( org.glassfish.jersey.servlet.ServletContainer.class );
		holder.setInitParameter(ServerProperties.PROVIDER_PACKAGES, "contact.resource");
		
		context.addServlet( holder, "/*" );

		// (5) Add the context (our application) to the Jetty server.
		server.setHandler( context );
		
		System.out.println("Starting Jetty server on port " + port);
		server.start();
		
		System.out.println("Server started.  Press ENTER to stop it.");
		int ch = System.in.read();
		System.out.println("Stopping server.");
		DaoFactory.getInstance().shutdown();
		server.stop();
	}
}
*/



public class JettyMain {
	
	/** 
	 * The default port to listen on. Typically 80 or 8080.  
	 */
	static final int PORT = 8080;
	private static Server server;

	/**
	 * Create a Jetty server and a context, add Jetty ServletContainer
	 * which dispatches requests to JAX-RS resource objects,
	 * and start the Jetty server.
	 * 
	 * @param args not used
	 * @throws Exception if Jetty server encounters any problem
	 */
	public static void main(String[] args) throws Exception {
		startServer(PORT);
		waitForExit();
	}
	
	/**
	 * Create a Jetty server and a context, add Jetty ServletContainer
	 * which dispatches requests to JAX-RS resource objects,
	 * and start the Jetty server.
	 * @param port port of the server.
	 * @return the url for connecting to the server.
	 */
	public static String startServer(int port){
		server = new Server( port );
		
		ServletContextHandler context = new ServletContextHandler( ServletContextHandler.SESSIONS );
		context.setContextPath("/");
		
		ServletHolder holder = new ServletHolder( org.glassfish.jersey.servlet.ServletContainer.class );
		
		holder.setInitParameter(ServerProperties.PROVIDER_PACKAGES, "contact.resource");
		context.addServlet( holder, "/*" );
		context.addFilter(RequestLogFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
		server.setHandler( context );
		
		System.out.println("Starting Jetty server on port " + port);
		try {
			server.start();
			return server.getURI().toString();
		} catch (Exception e) {}
		
		return "";
	}
	
	/**
	 * Wait for stopping the server by pressing enter.
	 */
	public static void waitForExit() {
		try {
			System.out.println("Server started.  Press ENTER to exit.");
			System.in.read();
			System.out.println("Stopping server.");
			stopServer();
		} catch (Exception e) {
		}
	}
	
	/**
	 * Stop the server.
	 */
	public static void stopServer(){
		try {
			DaoFactory.getInstance().shutdown();
			server.stop();
		} catch (Exception e) {}
	}
	
}

