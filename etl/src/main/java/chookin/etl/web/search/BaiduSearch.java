package chookin.etl.web.search;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import chookin.etl.web.data.Link;
import chookin.etl.web.jsoup.LinkHelper;

public class BaiduSearch extends SearchEngine {
	private static final Logger LOG = Logger.getLogger(BaiduSearch.class);
	public BaiduSearch() {
		super("www.baidu.com");
	}
	public Map<String, Link> queryHosts(String keyword, int pageDepth) throws IOException {
		Map<String, Link> links = new HashMap<String, Link>();
		if(keyword == null || keyword.isEmpty()){
			return links;
		}
		URL url = encodeQuery(keyword);
		Document docs = LinkHelper.getDocument(url.getPath());
		Elements elems = docs.select("span.g");
		for(Element elem : elems){
			String subUrl = elem.text();
			String host = LinkHelper.getHost(subUrl);
			if (links.containsKey(host)) {
				Link link = links.get(host);
				link.setWeight(link.getWeight() + 1.0f);
			} else {
				links.put(host, new Link().setHref(host));
			}
		}
		return links;
	}
	@Override
	public URL encodeQuery(String keyword) {
		try {
			String address = String
					.format("/s?wd=%s&rsv_spt=1&issp=1&rsv_bp=0&ie=utf-8&tn=baiduhome_pg&rsv_sug3=1&rsv_sug=1&rsv_sug1=1&rsv_sug4=13",
							URLEncoder.encode(keyword, "UTF-8"));
			return new URL(this.getProtocol(), this.getHost(), address);
		} catch (IOException e) {
            // Errors should not occur under normal circumstances.
            throw new RuntimeException(String.format(
                    "An error occurred while encoding the query arguments %s.", keyword));
		}
	}
}
