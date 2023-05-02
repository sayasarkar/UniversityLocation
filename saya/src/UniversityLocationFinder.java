import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONArray;
import org.json.JSONObject;


public class UniversityLocationFinder{

  public static void main(String[] args) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    System.out.print("Enter university name: ");
    String universityName = reader.readLine();
    System.out.print("Enter your current location: ");
    String origin = reader.readLine();
    findRouteToUniversity(universityName, origin);
  }

  private static void findRouteToUniversity(String universityName, String origin)
      throws IOException {
    String encodedOrigin = URLEncoder.encode(origin, "UTF-8");
    String encodedUniversityName = URLEncoder.encode(universityName, "UTF-8");
    String apiUrl =
        "https://www.bing.com/maps/?cp=24.244523%7E90.350818&lvl=7.9"
            + encodedOrigin
            + "&destination="
            + encodedUniversityName
            + "&key=<your-api-key>";
    URL url = new URL(apiUrl);
    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
    connection.setRequestMethod("GET");
    connection.setRequestProperty("Accept", "application/json");
    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    StringBuilder response = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) {
      response.append(line);
    }
    reader.close();
    JSONObject jsonResponse = new JSONObject(response.toString());
    JSONArray routes = jsonResponse.getJSONArray("routes");
    if (routes.length() == 0) {
      System.out.println("No route found.");
    } else {
      JSONObject route = routes.getJSONObject(0);
      JSONArray legs = route.getJSONArray("legs");
      JSONObject leg = legs.getJSONObject(0);
      JSONObject distance = leg.getJSONObject("distance");
      String distanceText = distance.getString("text");
      JSONObject duration = leg.getJSONObject("duration");
      String durationText = duration.getString("text");
      JSONObject endLocation = leg.getJSONObject("end_location");
      double latitude = endLocation.getDouble("lat");
      double longitude = endLocation.getDouble("lng");
      System.out.println("Distance from " + origin + " to " + universityName + ": " + distanceText);
      System.out.println("Travel time from " + origin + " to " + universityName + ": " + durationText);
      System.out.println("University location: " + latitude + ", " + longitude);
    }
  }
}