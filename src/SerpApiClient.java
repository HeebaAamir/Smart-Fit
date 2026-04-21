import com.google.gson.*;
import java.net.*;
import java.net.http.*;
import java.nio.file.*;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

public class SerpApiClient {

    private static final String API_KEY = "API_KEY GOES HERE"; //
    private static final String BASE_URL = "https://serpapi.com/search";

    public static SerpResult searchByImage(String imagePath) {
        return searchByImageWithBudget(imagePath, Double.MAX_VALUE);
    }

    public static SerpResult searchByImageWithBudget(String imagePath, double maxBudget) {
    SerpResult result = new SerpResult();
    try {
        // STEP 1: Upload image to ImgBB and get a public URL
        String imageUrl = ImageUploader.uploadImage(imagePath);
        
        if (imageUrl == null) {
            result.errorMessage = "Failed to upload image";
            return result;
        }
        
        // STEP 2: Send the public URL to SerpApi (NOT the image itself)
        String url = BASE_URL 
            + "?engine=google_lens"
            + "&api_key=" + API_KEY
            + "&url=" + URLEncoder.encode(imageUrl, StandardCharsets.UTF_8);
        
        System.out.println("🔍 Searching SerpApi...");
        
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();
        
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        System.out.println("Status: " + response.statusCode());
        
        if (response.statusCode() != 200) {
            result.errorMessage = "Error " + response.statusCode();
            return result;
        }
        
        // STEP 3: Parse the results
        parseResponseWithBudget(response.body(), result, maxBudget);
        result.success = true;
        System.out.println("✅ Found " + result.matches.size() + " matches!");
        
    } catch (Exception e) {
        result.errorMessage = e.getMessage();
        e.printStackTrace();
    }
    return result;
}
    
    private static void parseResponseWithBudget(String json, SerpResult result, double maxBudget) {
        try {
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            
            if (root.has("visual_matches")) {
                JsonArray matchesArray = root.getAsJsonArray("visual_matches");
                System.out.println("Total matches: " + matchesArray.size());
                
                for (JsonElement el : matchesArray) {
                    if (result.matches.size() >= 10) break;
                    
                    JsonObject m = el.getAsJsonObject();
                    SerpResult.Match match = new SerpResult.Match();
                    
                    match.title = m.has("title") ? m.get("title").getAsString() : "Unknown";
                    match.link = m.has("link") ? m.get("link").getAsString() : "";
                    match.source = m.has("source") ? m.get("source").getAsString() : "";
                    match.thumbnail = m.has("thumbnail") ? m.get("thumbnail").getAsString() : "";
                    
                    if (m.has("price") && m.getAsJsonObject("price").has("value")) {
                        match.price = m.getAsJsonObject("price").get("value").getAsString();
                        double priceValue = extractPriceValue(match.price);
                        if (priceValue > maxBudget) continue;
                    } else {
                        match.price = "Price unknown";
                    }
                    
                    result.matches.add(match);
                }
            }
        } catch (Exception e) {
            System.out.println("Parse error: " + e.getMessage());
        }
    }
    
    private static double extractPriceValue(String priceStr) {
        if (priceStr == null || priceStr.isEmpty()) return Double.MAX_VALUE;
        String numberOnly = priceStr.replaceAll("[^0-9.]", "");
        try {
            return Double.parseDouble(numberOnly);
        } catch (NumberFormatException e) {
            return Double.MAX_VALUE;
        }
    }
}