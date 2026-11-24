
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResendEmailService {

    private static final String API_URL =
            "https://api.resend.com/emails";

    private static final String SENDER =
            "RentEase <onboarding@resend.dev>";

    private static String escapeJson(String value) {
        return value.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }

    public static String sendEmail(
            String recipient,
            String subject,
            String message) throws Exception {

        String apiKey = System.getenv("RESEND_API_KEY");

        if (apiKey == null || apiKey.isBlank()) {
            throw new Exception("RESEND_API_KEY is not configured.");
        }

        if (recipient == null ||
                !recipient.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new Exception("Please enter a valid email address.");
        }

        if (subject == null || subject.isBlank() ||
                message == null || message.isBlank()) {
            throw new Exception("Email subject and message are required.");
        }

        String json = "{"
                + "\"from\":\"" + escapeJson(SENDER) + "\","
                + "\"to\":[\"" + escapeJson(recipient) + "\"],"
                + "\"subject\":\"" + escapeJson(subject) + "\","
                + "\"html\":\"" + escapeJson(message) + "\""
                + "}";

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .timeout(Duration.ofSeconds(30))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() >= 200 &&
                response.statusCode() < 300) {

            Matcher matcher = Pattern
                    .compile("\"id\"\\s*:\\s*\"([^\"]+)\"")
                    .matcher(response.body());

            if (matcher.find()) {
                return matcher.group(1);
            }

            throw new Exception(
                    "Resend accepted the request but returned no email ID."
            );
        }

        throw new Exception(
                "Resend API error (" + response.statusCode() + "): "
                + response.body()
        );
    }
}
