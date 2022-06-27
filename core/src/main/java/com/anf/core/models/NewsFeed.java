package com.anf.core.models;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import com.anf.core.beans.News;

/**
 * The Class NewsFeed.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class NewsFeed {
	
	/** The article. */
	List<News> articles = new ArrayList<>();
	
	/** The resource resolver. */
	@SlingObject
    private ResourceResolver resourceResolver;
	
	/**
	 * Inits the.
	 */
	@PostConstruct
	private void init() {
		Resource newsList = resourceResolver.getResource("/var/commerce/products/anf-code-challenge/newsData");
		final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
		String date = dateFormat.format(new Date());
		if(null != newsList) {
			for(Resource news:newsList.getChildren()) {
				News n = new News();
				n.setAuthor(news.getValueMap().get("author", ""));
				n.setContent(news.getValueMap().get("content", ""));
				n.setDescription(news.getValueMap().get("description", ""));
				n.setTitle(news.getValueMap().get("title", ""));
				n.setUrl(news.getValueMap().get("url", ""));
				n.setUrlImage(news.getValueMap().get("urlImage", ""));
				n.setDate(date);
				articles.add(n);
			}
		}
	}

	/**
	 * Gets the articles.
	 *
	 * @return the articles
	 */
	public final List<News> getArticles() {
		return articles;
	}
	
	
}
