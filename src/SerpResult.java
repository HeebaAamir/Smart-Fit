import java.util.ArrayList;
import java.util.List;

public class SerpResult {
    
    // Match class for each shopping result
    public static class Match {
        public String title;
        public String link;
        public String source;
        public String price;
        public String thumbnail;
        
        @Override
        public String toString() {
            String p = (price != null && !price.isEmpty()) ? " — " + price : "";
            return title + p + "\n   From: " + source;
        }
    }
    
    // List of shopping matches
    public List<Match> matches = new ArrayList<>();
    
    // Related search terms
    public List<String> relatedSearches = new ArrayList<>();
    
    // Status flags
    public boolean success = false;
    public String errorMessage = "";
}