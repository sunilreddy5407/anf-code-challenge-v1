package com.anf.core.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletResourceTypes;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;

@Component(service = { Servlet.class })
@SlingServletResourceTypes(
        resourceTypes="anf-code-challenge/components/page",
        methods=HttpConstants.METHOD_GET,
        selectors = "list",
        extensions="txt")
@ServiceDescription("List Pages Servlet")
public class ListPages extends SlingSafeMethodsServlet{
	private static final long serialVersionUID = 1L;
	
	@Reference
	private QueryBuilder queryBuilder;
	
	private static Logger LOGGER = LoggerFactory.getLogger(ListPages.class);
	
    @Override
    protected void doGet(final SlingHttpServletRequest req,
            final SlingHttpServletResponse resp) throws ServletException, IOException {
    	Map<String, String> params = new HashMap<String, String>();
		params.put("path", "/content/anf-code-challenge/us/en");
		params.put("type", "cq:Page");
		params.put("p.offset", "0");
		params.put("p.limit", "10");
		params.put("1_property", JcrConstants.JCR_CONTENT + "/anfCodeChallenge");
		params.put("1_property.value","true");

		Session session = null;

		try {
			session = req.getResource().getResourceResolver().adaptTo(Session.class);
			Query query = queryBuilder.createQuery(PredicateGroup.create(params), session);

			SearchResult searchResults = query.getResult();
			resp.setContentType("text/plain");
			PrintWriter write = resp.getWriter();
			for (Hit hit : searchResults.getHits()) {
				Resource pageContent = hit.getResource();
				write.append(pageContent.getPath()+"\n");
			}
			write.close();
		} catch (RepositoryException e) {
			LOGGER.error("Exception due to", e);
		} finally {
			if (session.isLive() || session != null) {
				session.logout();
			}
		}
    }
}
