// AI's assistance was taken to write this class.

import com.google.gson.*; // Converts java objects into JSON format and vice versa
import java.net.*; // Network-related classes (e.g. URI)
import java.net.http.*; // For making HTTP requests 

public class GroqClient {

    //paste your Groq API key here
    private static final String API_KEY = "Groq API KEY GOES HERE";

    // Groq API endpoint — uses same format as OpenAI
    private static final String API_URL =
        "https://api.groq.com/openai/v1/chat/completions";

// Send outfit details to Groq and get back fashion advice as bullet points
    public static String getSuggestion(String topColour, String bottomColour,
                                        String shoeColour, String outerColour,
                                        String accColour, String season) {
        // building the prompt and then sending it over to Groq's API, and finally parsing the response to extract the advice text.                                    
        try {
            // Build the outfit description prompt
            StringBuilder outfitDesc = new StringBuilder();
            outfitDesc.append("Top: ").append(topColour).append(", ");
            outfitDesc.append("Bottom: ").append(bottomColour).append(", ");
            outfitDesc.append("Shoes: ").append(shoeColour);
            if (outerColour != null)
                outfitDesc.append(", Outerwear: ").append(outerColour);
            if (accColour != null)
                outfitDesc.append(", Accessory: ").append(accColour);
            if (season != null)
                outfitDesc.append(", Season: ").append(season);

            // The prompt we are sending
            String userMessage = "Give me 2-3 short bullet points of fashion and "
                + "colour theory advice for this outfit: "
                + outfitDesc
                + ". Each bullet starts with •. Keep total under 60 words. "
                + "Be specific about why the colours work or clash.";

            // Building JSON request body
            String requestBody = "{"
                 + "\"model\": \"llama-3.3-70b-versatile\","
                + "\"messages\": ["
                + "  {"
                + "    \"role\": \"system\","
                + "    \"content\": \"You are a professional fashion stylist "
                +        "who gives concise colour theory advice.\""
                + "  },"
                + "  {"
                + "    \"role\": \"user\","
                + "    \"content\": \"" + escapeJson(userMessage) + "\""
                + "  }"
                + "],"
                + "\"max_tokens\": 150,"
                + "\"temperature\": 0.7"
                + "}";

            // Send HTTP POST
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

            HttpResponse<String> response = client.send(
                request, HttpResponse.BodyHandlers.ofString()
            );

            System.out.println("Groq status: " + response.statusCode());

            if (response.statusCode() != 200) {
                System.out.println("Groq error body: " + response.body());
                return fallbackSuggestion(topColour, bottomColour, shoeColour);
            }

            // Parse response
            // Structure: { "choices": [{ "message": { "content": "..." } }] }
            JsonObject root = JsonParser.parseString(response.body())
                                        .getAsJsonObject();
            String text = root
                .getAsJsonArray("choices")
                .get(0).getAsJsonObject()
                .getAsJsonObject("message")
                .get("content").getAsString();

            return text.trim();

        } catch (Exception e) {
            System.out.println("Groq call failed: " + e.getMessage());
            return fallbackSuggestion(topColour, bottomColour, shoeColour);
        }
    }

    // Escapes special characters for safe JSON embedding
    private static String escapeJson(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    // Local fallback when API is unavailable. The following lines will then be displayed.
    private static String fallbackSuggestion(String top,
                                              String bottom,
                                              String shoe) {
        return "• " + top + " and " + bottom
                + " — check if these colours complement each other.\n"
                + "• " + shoe + " shoes work best matching or "
                + "contrasting intentionally with the bottom.\n"
                + "• (Connect to internet for AI-powered suggestions)";
    }
}
