package cifrador;

import com.google.common.hash.Hashing;
import java.nio.charset.StandardCharsets;

public class Cifrador {

    public String get_SHA_512_SecurePassword(String passwordToHash) {
        return Hashing.sha512().hashString(passwordToHash, StandardCharsets.UTF_8).toString();
    }
}
