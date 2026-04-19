package itson.ecommercewebaplication.listeners;

import itson.ecommercewebaplication.bo.CategoriaBO;
import itson.ecommercewebaplication.models.Categoria;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author PC
 */
@WebListener
public class AppListener implements ServletContextListener {

    private static final List<String> CATEGORIAS_DEFAULT = Arrays.asList(
            "Vestidos", "Blusas", "Conjuntos", "Blazers",
            "Faldas", "Pantalones", "Tops", "Accesorios"
    );

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            CategoriaBO categoriaBO = new CategoriaBO();
            List<Categoria> existentes = categoriaBO.obtenerTodas();

            if (existentes.isEmpty()) {
                System.out.println("[AppListener] Insertando categorías por defecto...");
                for (String nombre : CATEGORIAS_DEFAULT) {
                    Categoria cat = new Categoria();
                    cat.setNombre(nombre);
                    categoriaBO.crear(cat);
                    System.out.println("[AppListener] Categoría creada: " + nombre);
                }
                System.out.println("[AppListener] Categorías insertadas correctamente.");
            } else {
                System.out.println("[AppListener] Categorías ya existentes (" + existentes.size() + "), no se insertan.");
            }
        } catch (Exception e) {
            System.err.println("[AppListener] Error al insertar categorías: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // nada
    }
}
