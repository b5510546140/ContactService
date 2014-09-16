package contact.main;
/**
 * @author wat wattanagaroon
 * @version 2014/09/16
 */
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.server.ServerProperties;

import contact.resource.*;

public class JettyMain {
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
		server.stop();
	}
}


