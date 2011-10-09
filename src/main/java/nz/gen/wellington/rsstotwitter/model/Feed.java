package nz.gen.wellington.rsstotwitter.model;

public class Feed {
    
    private int id;
    private String url;
    
    public Feed() {       
    }

    public  Feed(String url) {
        this.id = 0;
        this.url = url;      
    }

    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
	public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
}
