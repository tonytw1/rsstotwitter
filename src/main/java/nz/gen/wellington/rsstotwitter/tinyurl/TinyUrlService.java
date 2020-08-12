package nz.gen.wellington.rsstotwitter.tinyurl;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;

@Component
public class TinyUrlService {

    private final static Logger log = Logger.getLogger(TinyUrlService.class);

    private final static String TINY_URL_API = "http://tinyurl.com/api-create.php?url=";
    private final static int TEN_SECONDS = 10000;

    private HttpClient client = HttpClients.createDefault();

    public String makeTinyUrl(String url) throws IOException {
        log.info("Fetching tinyurl for: " + url);

        HttpGet getTinyUrl = new HttpGet(TINY_URL_API + URLEncoder.encode(url, "UTF8"));
        RequestConfig withTenSecondTimeout = RequestConfig.custom()
                .setConnectionRequestTimeout(TEN_SECONDS)
                .setConnectTimeout(TEN_SECONDS)
                .setSocketTimeout(TEN_SECONDS)
                .build();
        getTinyUrl.setConfig(withTenSecondTimeout);

        HttpResponse response = client.execute(getTinyUrl);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == HttpStatus.SC_OK) {
            final String tinyUrl = EntityUtils.toString(response.getEntity());
            log.info("Tinyurl is: " + tinyUrl);
            return tinyUrl;
        } else {
            log.warn("The http call returned http status:" + statusCode);
        }

        log.warn("Could not make tiny url, returning original url");
        return url;
    }

}
