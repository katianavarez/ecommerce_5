package itson.ecommercewebaplication.dto;

/**
 * Estructura del cuerpo JSON que llega al endpoint POST /api/auth/login.
 * Lo usamos como objeto intermedio para que Jackson deserialice el body
 * directamente sin tener que leer campos sueltos del request.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
 */
public class LoginRequestDTO {

    private String correo;
    private String contrasena;

    public LoginRequestDTO() {
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}
