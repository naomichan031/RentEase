
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordUtil {

    private static final int ITERATIONS = 210000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;

    public static String hashPassword(char[] password) {

        try {
            byte[] salt = new byte[SALT_LENGTH];

            SecureRandom random = new SecureRandom();
            random.nextBytes(salt);

            KeySpec spec = new PBEKeySpec(
                password,
                salt,
                ITERATIONS,
                KEY_LENGTH
            );

            SecretKeyFactory factory =
                SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            byte[] hash = factory.generateSecret(spec).getEncoded();

            String encodedSalt =
                Base64.getEncoder().encodeToString(salt);

            String encodedHash =
                Base64.getEncoder().encodeToString(hash);

            return "pbkdf2$" + ITERATIONS + "$"
                    + encodedSalt + "$" + encodedHash;

        } catch (Exception e) {
            throw new IllegalStateException(
                "Password hashing failed.", e
            );
        }
    }

    public static boolean verifyPassword(
        char[] password, String storedHash
    ) {

        try {
            String[] parts = storedHash.split("\\$");

            if (parts.length != 4 || !parts[0].equals("pbkdf2")) {
                return false;
            }

            int iterations = Integer.parseInt(parts[1]);

            if (iterations <= 0 || iterations > 1000000) {
                return false;
            }

            byte[] salt =
                Base64.getDecoder().decode(parts[2]);

            byte[] expectedHash =
                Base64.getDecoder().decode(parts[3]);

            KeySpec spec = new PBEKeySpec(
                password,
                salt,
                iterations,
                expectedHash.length * 8
            );

            SecretKeyFactory factory =
                SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            byte[] actualHash = factory.generateSecret(spec).getEncoded();

            return java.security.MessageDigest.isEqual(
                expectedHash, actualHash
            );

        } catch (Exception e) {
            return false;
        }
    }
}
