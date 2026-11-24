
public class ResendTest {

    public static void main(String[] args) {

        String recipient = "naomichan001@gmail.com";

        try {
            String emailId = ResendEmailService.sendEmail(
                recipient,
                "RentEase API Integration Test",
                "<h2>RentEase Email Test</h2>"
                + "<p>Your Resend API integration is working!</p>"
                + "<p>This is a test email from your RentEase application.</p>"
            );

            System.out.println("EMAIL SENT SUCCESSFULLY!");
            System.out.println("Resend Email ID: " + emailId);

        } catch (Exception e) {
            System.out.println("EMAIL SENDING FAILED!");
            System.out.println("Reason: " + e.getMessage());
        }
    }
}
