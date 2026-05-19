package itson.ecommercewebaplication.enums;

/**
 * Paleta cerrada de colores que la tienda maneja para las prendas.
 * Cada constante guarda su nombre legible (para mostrar en el catálogo y
 * en los formularios del admin) y su valor hexadecimal (para las muestras
 * de color en los CSS y en la ficha de producto).
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
 */
public enum Colores {
    NEGRO("Negro", "#1A1A1A"),
    BLANCO("Blanco", "#F5F5F5"),
    GRIS("Gris", "#6B6B6B"),
    AZUL("Azul", "#1A1A2E"),
    AZUL_CLARO("Azul claro", "#4A90D9"),
    VERDE("Verde", "#2D5A2D"),
    ROJO("Rojo", "#8B1A1A"),
    ROSA("Rosa", "#E8C4C4"),
    VINO("Vino", "#4A0E1E"),
    MORADO("Morado", "#4A1A5C"),
    NARANJA("Naranja", "#C4621A"),
    AMARILLO("Amarillo", "#C4A81A"),
    BEIGE("Beige", "#D4C5A8"),
    CAFE("Café", "#6B3A1A"),
    DORADO("Dorado", "#C9A96E"),
    PLATEADO("Plateado", "#A8A8A8");

    private final String nombre;
    private final String hex;

    Colores(String nombre, String hex) {
        this.nombre = nombre;
        this.hex = hex;
    }

    public String getNombre() {
        return nombre;
    }

    public String getHex() {
        return hex;
    }
}
