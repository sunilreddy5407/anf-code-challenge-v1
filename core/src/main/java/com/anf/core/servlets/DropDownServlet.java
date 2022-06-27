package com.anf.core.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.servlet.Servlet;

import org.apache.commons.collections.iterators.TransformIterator;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.commerce.common.ValueMapDecorator;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.cq.commons.jcr.JcrConstants;

/**
 * The Class DropDownServlet.
 */
@Component(service = Servlet.class, property = { Constants.SERVICE_DESCRIPTION + "= Dynamic Drop Down",
		"sling.servlet.resourceTypes=" + "custom/dynamic-dropdown" })
public class DropDownServlet extends SlingSafeMethodsServlet {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The logger. */
	private static Logger LOGGER = LoggerFactory.getLogger(DropDownServlet.class);

	/**
	 * Do get.
	 *
	 * @param request the request
	 * @param response the response
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
		try {
			ResourceResolver resourceResolver = request.getResourceResolver();
			List<KeyValue> dropDownList = new ArrayList<>();
			Resource pathResource = request.getResource();
			String jsonPath = pathResource.getChild("datasource").getValueMap().get("jsonPath", String.class);
			Resource resource = request.getResourceResolver().getResource(jsonPath);
			JSONObject data = getJsonFromFile(resource);
			data.keys().forEachRemaining(key -> {
				try {
					dropDownList.add(new KeyValue(data.getString((String) key),(String) key));
				} catch (JSONException e) {
					LOGGER.error("Error while parsing json", e);
				}
			});
			DataSource ds = new SimpleDataSource(new TransformIterator(dropDownList.iterator(), input -> {
				KeyValue keyValue = (KeyValue) input;
				ValueMap vm = new ValueMapDecorator(new HashMap<>());
				vm.put("value", keyValue.key);
				vm.put("text", keyValue.value);
				return new ValueMapResource(resourceResolver, new ResourceMetadata(), JcrConstants.NT_UNSTRUCTURED, vm);
			}));
			request.setAttribute(DataSource.class.getName(), ds);

		} catch (Exception e) {
			LOGGER.error("Error in Get Drop Down Values", e);
		}
	}

	/**
	 * Gets the json from file.
	 *
	 * @param resource the resource
	 * @return the json from file
	 */
	private JSONObject getJsonFromFile(Resource resource) {
		JSONObject jsonObj = new JSONObject();
		try {
			Node jcnode = resource.adaptTo(Node.class).getNode("jcr:content");
			InputStream content = jcnode.getProperty("jcr:data").getBinary().getStream();
			StringBuilder sb = new StringBuilder();
			String line;
			BufferedReader br = new BufferedReader(new InputStreamReader(content, StandardCharsets.UTF_8));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			jsonObj = new JSONObject(sb.toString());

		} catch (RepositoryException | JSONException | IOException e) {
			LOGGER.error(e.getMessage(), e);
		}

		return jsonObj;
	}

	/**
	 * The Class KeyValue.
	 */
	private class KeyValue {

		/**
		 * key property.
		 */
		private String key;
		/**
		 * value property.
		 */
		private String value;

		/**
		 * constructor instance intance.
		 *
		 * @param newKey   -
		 * @param newValue -
		 */
		private KeyValue(final String newKey, final String newValue) {
			this.key = newKey;
			this.value = newValue;
		}
	}
}