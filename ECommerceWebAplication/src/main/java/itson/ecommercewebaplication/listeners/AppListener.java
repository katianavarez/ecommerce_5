package itson.ecommercewebaplication.listeners;

import itson.ecommercewebaplication.bo.CategoriaBO;
import itson.ecommercewebaplication.models.Categoria;
import itson.ecommercewebaplication.util.JPAUtil;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.util.Arrays;
import java.util.List;

/**
 * Listener del ciclo de vida de la aplicación. Al arrancar, siembra las
 * categorías por defecto de la tienda si la tabla está vacía, de modo que
 * el catálogo tenga estructura desde el primer despliegue sin depender del
 * script seed. Al apagarse (o en un redeploy de Tomcat), cierra el
 * EntityManagerFactory para liberar las conexiones a MySQL.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
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
        // Cerrar el EntityManagerFactory en shutdown / hot-redeploy de Tomcat
        // para liberar el pool de conexiones y evitar leaks del classloader.
        try {
            JPAUtil.close();
            System.out.println("[AppListener] EntityManagerFactory cerrado correctamente.");
        } catch (Exception e) {
            System.err.println("[AppListener] Error al cerrar EntityManagerFactory: " + e.getMessage());
        }
    }
}
