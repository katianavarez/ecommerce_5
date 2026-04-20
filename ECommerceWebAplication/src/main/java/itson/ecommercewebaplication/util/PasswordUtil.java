package itson.ecommercewebaplication.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Hashing y verificación de contraseñas con BCrypt.
 *
 * Los hashes generados empiezan con "$2a$" — esto se usa para detectar
 * contraseñas legacy en texto plano que aún viven en la BD.
 */
public class PasswordUtil {

    private static final int COST = 12;

    public static String hash(String plain) {
        return BCrypt.hashpw(plain, BCrypt.gensalt(COST));
    }

    public static boolean verify(String plain, String hash) {
        if (hash == null || hash.isEmpty()) return false;
        try {
            return BCrypt.checkpw(plain, hash);
        } catch (IllegalArgumentException e) {
            // El hash no tiene formato BCrypt válido (probablemente es texto plano legacy).
            return false;
        }
    }

    /** True si la cadena ya parece un hash BCrypt (no texto plano). */
    public static boolean esHash(String s) {
        return s != null && (s.startsWith("$2a$") || s.startsWith("$2b$") || s.startsWith("$2y$"));
    }
}
