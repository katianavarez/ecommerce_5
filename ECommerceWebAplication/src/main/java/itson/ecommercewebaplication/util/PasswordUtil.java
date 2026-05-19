package itson.ecommercewebaplication.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Hashing y verificación de contraseñas con BCrypt (jBCrypt 0.4).
 * Se usa cost factor 12 para que cada hash tarde algunas decenas de
 * milisegundos, lo suficiente para frustrar ataques de fuerza bruta sin
 * volver lento el login real.
 * 
 * Los hashes generados empiezan con "$2a$" — esa firma se usa en
 * {@link #esHash(String)} para detectar contraseñas legacy en texto plano
 * que entraron por el script seed y que {@code UsuarioBO.login} re-hashea
 * automáticamente en el primer login del usuario.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
 */
public class PasswordUtil {

    private static final int COST = 12;

    /** Genera un hash BCrypt nuevo (con su salt incrustado) para la contraseña dada. */
    public static String hash(String plain) {
        return BCrypt.hashpw(plain, BCrypt.gensalt(COST));
    }

    /**
     * Compara una contraseña en texto plano contra un hash BCrypt.
     * Devuelve false si el hash es null, vacío o tiene un formato inválido
     * (caso típico cuando aún es texto plano del seed).
     */
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
