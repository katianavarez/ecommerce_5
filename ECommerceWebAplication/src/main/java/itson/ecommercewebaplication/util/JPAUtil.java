package itson.ecommercewebaplication.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Helper estático para obtener un {@link EntityManager} sin tener que
 * crear y reusar una {@link EntityManagerFactory} en cada DAO. El factory
 * se inicializa una sola vez en el bloque static a partir de la unidad de
 * persistencia "Ecommerce" definida en {@code persistence.xml}.
 * 
 * Cada operación en los DAO/BO obtiene un EM nuevo y lo cierra al final
 * para no acumular conexiones abiertas hacia MySQL.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
 */
public class JPAUtil {

    private static final String PERSISTENCE_UNIT_NAME = "Ecommerce";
    private static EntityManagerFactory emf;

    static {
        try {
            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        } catch (Exception e) {
            System.err.println("Error al crear EntityManagerFactory: " + e.getMessage());
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Devuelve un EntityManager nuevo. Quien lo pide es responsable de
     * cerrarlo (típicamente en un bloque finally).
     */
    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    /**
     * Cierra la factory. Lo invoca {@code AppListener} cuando el contexto
     * de la aplicación se destruye, para liberar conexiones.
     */
    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
