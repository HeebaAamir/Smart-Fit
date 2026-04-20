import java.util.ArrayList;
import java.util.List;

public class SerpResult {

    // One shopping match from Google Lens
    public static class Match {
        public String title;      // e.g. "Red Wrap Dress"
        public String link;       // e.g. "https://zara.com/..."
        public String source;     // e.g. "zara.com"
        public String price;      // e.g. "PKR 8,999"
        public String thumbnail;  // URL of the item's image

        // For display in the GUI
        @Override
        public String toString() {
            String p = (price != null && !price.isEmpty())
                ? "  —  " + price : "";
            return title + p + "\n" + source;
        }
    }

    // List of shopping matches returned by Google Lens
    // These are the "similar items" shown to the user
    public List<Match> matches = new ArrayList<>();

    // Related search terms Google suggests
    public List<String> relatedSearches = new ArrayList<>();

    // Setting a bool variable to check if the API call (search feature) was successful or not
    // Declaring a string variable to store the error message if the API call was not successful
    public boolean success = false;
    public String  errorMessage = "";
}
