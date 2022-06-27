package com.anf.core.services.impl;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ResourceUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anf.core.services.ContentService;

@Component(immediate = true, service = ContentService.class)
public class ContentServiceImpl implements ContentService {
	
	private static Logger LOGGER = LoggerFactory.getLogger(ContentServiceImpl.class);
	
	@Reference
	ResourceResolverFactory resourceResolverFactory;

    @Override
    public boolean commitUserDetails(String fname, String lname, String age) {
    	try {
			ResourceResolver resourceResolver = getResourceResolver();
			final Resource parent = ResourceUtil.getOrCreateResource(resourceResolver, "/var/anf-code-challenge",
				      Collections.singletonMap("jcr:primaryType", (Object) "sling:OrderedFolder"), null, false);
			Map<String, Object> props = new HashMap<>();
			props.put("jcr:primaryType", (Object) "nt:unstructured");
			props.put("firstname", fname);
			props.put("lastname", lname);
			props.put("age", age);
			resourceResolver.create(parent, new Date().getTime()+"",
				      props);
			resourceResolver.commit();
			resourceResolver.close();
			return true;
		} catch (LoginException | PersistenceException e) {
			LOGGER.error(e.getMessage(), e);
		}
    	return false;
    }
    
    private ResourceResolver getResourceResolver() throws LoginException {
    	return resourceResolverFactory.getServiceResourceResolver(Collections.singletonMap(
                ResourceResolverFactory.SUBSERVICE,
                (Object) "subSer"));
    }
}
