import java.net.*;
import java.net.http.*;
import java.nio.file.*;
import java.util.Base64;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import com.google.gson.*;

public class ImageUploader {
    
    private static final String IMGBB_API_KEY = "API_KEY GOES HERE";
    
    //Passing the image path to this method 
    public static String uploadImage(String imagePath) throws Exception {
        System.out.println("📤 Uploading image to ImgBB...");
        
        // Reading the image file
        byte[] imageBytes = Files.readAllBytes(Path.of(imagePath));
        
        // Converting to Base64 (text format)
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        
        // Building the upload request
        String url = "https://api.imgbb.com/1/upload?key=" + IMGBB_API_KEY;
        String body = "image=" + URLEncoder.encode(base64Image, StandardCharsets.UTF_8);
        
        // Sending the image to ImgBB to get a public URL
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();
        
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        // Getting the public URL from ImgBB's response
        if (response.statusCode() == 200) {
            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
            String imageUrl = json.getAsJsonObject("data").get("url").getAsString();
            System.out.println("✅ Image uploaded! URL: " + imageUrl);
            return imageUrl;
        }
        
        System.out.println("❌ Upload failed!");
        return null;
    }
}