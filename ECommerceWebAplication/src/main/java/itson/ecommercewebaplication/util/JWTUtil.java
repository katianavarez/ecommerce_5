package itson.ecommercewebaplication.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;

/**
 * Generación y validación de JSON Web Tokens con la librería jjwt.
 * Los tokens viven 24 horas y se firman con HMAC-SHA256 usando una clave
 * secreta leída en tiempo de carga; en el payload llevan el correo del
 * usuario como subject y su rol como claim adicional.
 * 
 * El JWT se entrega tanto en la respuesta del endpoint
 * {@code POST /api/auth/login} como guardado en la sesión del servlet
 * web, para que tanto el flujo MVC como la API REST queden alineados.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
 */
public class JWTUtil {

    // El secreto se lee de la variable de entorno VELOUR_JWT_SECRET para que en
    // producción se pueda rotar sin tocar código. El fallback hardcoded es solo
    // para desarrollo/demo local; basta exportar la variable para que tome efecto.
    private static final String SECRET = resolveSecret();
    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    private static String resolveSecret() {
        String env = System.getenv("VELOUR_JWT_SECRET");
        if (env != null && env.length() >= 32) {
            return env;
        }
        return "ECommerceSecreto256BitsSeguro!XY";
    }

    /**
     * Construye un JWT firmado con el correo como subject y el rol como
     * claim, con vigencia de 24 horas. El cliente debe enviarlo en el
     * header {@code Authorization: Bearer <token>}.
     */
    public static String generarToken(String correo, String rol) {
        return Jwts.builder()
                .subject(correo)
                .claim("rol", rol)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24)) // 24h
                .signWith(KEY)
                .compact();
    }

    /**
     * Verifica la firma y la fecha de expiración del token, y devuelve el
     * correo (subject). Si el token está expirado o la firma no cuadra,
     * jjwt lanza una excepción que el filtro convierte en HTTP 401.
     */
    public static String validarToken(String token) {
        return Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Lee el claim "rol" del token para que el filtro lo deje disponible
     * como atributo de la request, sin tener que volver a consultar BD.
     */
    public static String getRolFromToken(String token) {
        return Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("rol", String.class);
    }
}
