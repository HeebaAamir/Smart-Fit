import com.google.gson.*;
import java.net.*;
import java.net.http.*;
import java.nio.file.*;
import java.util.Base64;

public class SerpApiClient {
    private static final String API_KEY = "YOUR_KEY_HERE";
    private static final String BASE_URL = "https://serpapi.com/search.json";

    // Original method (without budget)
    public static SerpResult searchByImage(String imagePath) {
        return searchByImageWithBudget(imagePath, Double.MAX_VALUE);
    }
    
    // NEW: Search with budget filter
    public static String searchByImageWithBudget(String imagePath, double maxBudget) {
        try {
            // Read image and convert to Base64
            byte[] imageBytes = Files.readAllBytes(Path.of(new java.net.URI(imagePath)));
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            
            // Build URL with price filter
            String url = BASE_URL + "?engine=google_lens&api_key=" + API_KEY;
            
            // Add price filter to request
            String requestBody = String.format(
                "{\"image\": \"%s\", \"tbs\": \"pricerange:%d-%d\"}",
                base64Image, 0, (int)maxBudget
            );
            
            // Send request
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
            
            HttpResponse<String> response = client.send(request, 
                HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 200) {
                return "Error: " + response.statusCode() + " - " + response.body();
            }
            
            // Filter results by price
            return filterResultsByBudget(response.body(), maxBudget);
            
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    
    private static String filterResultsByBudget(String json, double maxBudget) {
        StringBuilder results = new StringBuilder();
        results.append("Found items under Rs ").append((int)maxBudget).append(":\n\n");
        
        try {
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            int count = 0;
            
            if (root.has("visual_matches")) {
                for (JsonElement el : root.getAsJsonArray("visual_matches")) {
                    if (count >= 5) break; // Show top 5 results
                    
                    JsonObject match = el.getAsJsonObject();
                    String title = match.has("title") ? match.get("title").getAsString() : "Unknown";
                    String price = "";
                    String link = match.has("link") ? match.get("link").getAsString() : "";
                    
                    // Extract price
                    if (match.has("price") && match.getAsJsonObject("price").has("value")) {
                        price = match.getAsJsonObject("price").get("value").getAsString();
                        // Remove currency symbol and convert to number
                        double priceValue = extractPriceValue(price);
                        
                        // Only include if within budget
                        if (priceValue <= maxBudget) {
                            results.append((count+1)).append(". ")
                                   .append(title).append("\n")
                                   .append("   💰 Price: ").append(price).append("\n")
                                   .append("   🔗 ").append(link).append("\n\n");
                            count++;
                        }
                    } else {
                        // No price info, include anyway
                        results.append((count+1)).append(". ")
                               .append(title).append("\n")
                               .append("   💰 Price: Check website\n")
                               .append("   🔗 ").append(link).append("\n\n");
                        count++;
                    }
                }
            }
            
            if (count == 0) {
                results.append("❌ No items found within Rs ").append((int)maxBudget)
                       .append(". Try increasing your budget!");
            }
            
        } catch (Exception e) {
            return "Error parsing results: " + e.getMessage();
        }
        
        return results.toString();
    }
    
    private static double extractPriceValue(String priceStr) {
        // Extract number from price string like "Rs. 3,999" or "$49.99"
        String numberOnly = priceStr.replaceAll("[^0-9.]", "");
        try {
            return Double.parseDouble(numberOnly);
        } catch (NumberFormatException e) {
            return Double.MAX_VALUE;
        }
    }
}
