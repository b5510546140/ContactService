package contact.main;

import java.io.*;
import java.util.*;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * A Servlet filter for logging request information.
 * In this filter, it prints the requests headers on System.out.
 * @author jim
 */
public class RequestLogFilter implements javax.servlet.Filter {
	PrintStream out;
	
	/** initialize filter for use */
	@Override
	public void init(FilterConfig arg0) throws ServletException {
		out = System.out;
	}
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		
		if (req instanceof HttpServletRequest) {
			HttpServletRequest request = (HttpServletRequest) req;
			out.println( request.getMethod()+" "+request.getRequestURI() );
			Enumeration<String> headers = request.getHeaderNames() ;
			while(headers.hasMoreElements()) {
				String header = headers.nextElement();
				String value = request.getHeader(header);
				out.printf("%s: %s\n", header, value);
			}
		}
		chain.doFilter(req, resp);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

}
