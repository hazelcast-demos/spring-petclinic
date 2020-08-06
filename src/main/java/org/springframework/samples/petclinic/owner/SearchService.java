package org.springframework.samples.petclinic.owner;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import static java.util.stream.Collectors.toList;

@Component
public class SearchService {

	private final RestHighLevelClient client;

	@Value("${elastic.index:petclinic-index}")
	private String index;

	@Value("${elastic.searchField:search}")
	private String searchField;

	public SearchService(RestHighLevelClient client) {
		this.client = client;
	}

	public Collection<Integer> search(String query) {
		SearchRequest searchRequest = new SearchRequest(index);
		searchRequest.source().fetchSource(true).query(QueryBuilders.wildcardQuery(searchField, query + "*"));

		try {
			SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

			SearchHits hits = response.getHits();
			return Arrays.stream(hits.getHits()).map(hit -> (Integer) hit.getSourceAsMap().get("id")).collect(toList());
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
