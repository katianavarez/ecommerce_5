package itson.ecommercewebaplication.dto;

/**
 * Envoltorio estándar para las respuestas JSON de la API REST. Mantiene una
 * forma uniforme {@code {success, message, data}} para que el JS del cliente
 * (api.js) pueda manejar errores y datos sin distinguir endpoint por endpoint.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
 */
public class ResponseDTO {

    private boolean success;
    private String message;
    private Object data;

    public ResponseDTO() {
    }

    public ResponseDTO(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ResponseDTO(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
