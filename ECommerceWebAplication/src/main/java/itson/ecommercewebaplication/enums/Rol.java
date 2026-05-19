package itson.ecommercewebaplication.enums;

/**
 * Roles que distinguen a los usuarios de la aplicación. CLIENTE solo puede
 * comprar, reseñar y editar su perfil; ADMINISTRADOR entra al panel /admin
 * para gestionar catálogo, pedidos, clientes y reseñas. El rol se guarda
 * en el JWT al hacer login y se valida en los filtros de autenticación.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
 */
public enum Rol {
    CLIENTE,
    ADMINISTRADOR
}
