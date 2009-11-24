package nz.gen.wellington.tinyurl;

import java.io.IOException;
import java.net.URLEncoder;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;

public class TinyUrlService {
    
    Logger log = Logger.getLogger(TinyUrlService.class);

    final private static String TINY_URL_API = "http://tinyurl.com/api-create.php?url=";

    public String makeTinyUrl(String url) {
        log.info("Fetching tinyurl for: " + url);
        HttpClient client = new HttpClient();
        String apiCallUrl = TINY_URL_API + URLEncoder.encode(url);
        HttpMethod method = new GetMethod(apiCallUrl);
        method.setFollowRedirects(false);
        try {
            client.executeMethod(method);                   
            if (method.getStatusCode() == HttpStatus.SC_OK) {
                final String tinyUrl = method.getResponseBodyAsString();
                log.info("Tinyurl is: " + tinyUrl);
                return tinyUrl;
            } else {
                log.warn("The http call returned http status:" + method.getStatusCode());
            }            
        } catch (HttpException e) {
            log.error(e);
        } catch (IOException e) {
            log.error(e);
        }
        log.warn("Could not make tiny url, returning orginal url");
        return url;        
    }

}
