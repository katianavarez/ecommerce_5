# Velour - Tienda de ropa en línea

Proyecto final de la materia Aplicaciones Web (ITSON), equipo 5.

Velour es una tienda de ropa en línea. El cliente se registra, ve el catálogo,
busca y filtra prendas, las agrega al carrito, paga y puede dejar reseñas de lo
que compró. Aparte hay un panel de administración con su propio login para
manejar productos, categorías, proveedores, pedidos, clientes y reseñas.

## Integrantes

- 252039 Hector Javier Alonso Zaragoza
- 252191 Freddy Ali Castro Roman
- 252855 Katia Ximena Navarez Espinoza
- 251622 Alejandro Rodriguez Lugo

## Qué necesitas

- JDK 11 o superior
- MySQL 8 o superior
- Apache Tomcat 10.1 (que sea la 10, porque el proyecto usa Jakarta EE y no el viejo javax)
- NetBeans 17 o superior, que es desde donde nosotros lo corrimos
- Un navegador (Chrome, Edge, Firefox, Brave, etc.)

Maven ya viene integrado en NetBeans, así que no hace falta instalarlo aparte.

## Cómo correrlo

Antes que nada abre `ECommerceWebAplication/src/main/resources/META-INF/persistence.xml`
y pon ahí tu usuario y contraseña de MySQL (el archivo trae root / ITSON de ejemplo).

Ya con eso:

1. Crea la base de datos en MySQL Workbench (o donde uses MySQL):

   ```sql
   DROP DATABASE IF EXISTS ecommerce_db;
   CREATE DATABASE ecommerce_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

2. En NetBeans, dale Clean and Build al proyecto y luego Run. La primera vez que
   arranca, Hibernate crea solo todas las tablas.

3. Cuando ya estén las tablas, corre el script `EJECUTARScriptParaDatosRapidos.sql`
   para llenar la base con los productos, las categorías y los usuarios de prueba.

Después de eso la app queda en `http://localhost:8080/ECommerceWebAplication/`.

## Usuarios de prueba

El script deja dos cuentas listas para entrar:

- Admin: `admin@gmail.com` / `Admin1234`
- Cliente: `equipo@gmail.com` / `Cliente123`

Las contraseñas se guardan encriptadas con BCrypt. En el script aparecen en texto
plano porque el sistema las encripta solas la primera vez que el usuario entra.

## Páginas principales

- `/` es el catálogo (la página de inicio)
- `/auth/registro` para crear una cuenta de cliente
- `/auth/login` para entrar como cliente
- `/admin/login` para entrar al panel de administración

## Tecnologías

En el backend usamos Jakarta EE 10 (Servlets, JSP y JSTL), Hibernate 6.6 con JPA
sobre MySQL, Jackson para el JSON de la API, jjwt para los tokens y jBCrypt para
encriptar las contraseñas. Todo se maneja con Maven.

En el frontend, HTML y CSS hechos a mano (sin Bootstrap ni Tailwind, como pedía
el Avance 2) y JavaScript con la Fetch API para consumir la API REST.

## Cómo está organizado

El proyecto sigue MVC: las vistas son JSP, los controladores son Servlets, la
lógica está en los BO y el acceso a datos en los DAO. Dentro de
`src/main/java/itson/ecommercewebaplication/` están las carpetas `apirest` (los
servlets de la API), `controllers/admin` y `controllers/app` (el panel y el
cliente), `bo` (lógica de negocio), `dao` (acceso a datos), `models` (las
entidades) y el resto de apoyo (`filters`, `util`, `enums`, `dto`, `listeners`).
Las vistas JSP, el CSS, el JS y las imágenes van en `src/main/webapp`.

## La API REST

Todo va bajo `/api`. Lo principal:

- `POST /api/auth/login` y `POST /api/auth/registro` para entrar o crear cuenta
- `GET /api/productos` y `GET /api/productos/{id}` para el catálogo
- `POST`, `GET`, `PUT` y `DELETE` sobre `/api/carrito` para el carrito
- `POST /api/pedidos`, `GET /api/pedidos/{id}` y `GET /api/pedidos/usuario/{id}` para los pedidos
- `POST /api/resenas` y `GET /api/resenas/producto/{id}` para las reseñas
- `PUT /api/usuarios/{id}` para editar el perfil

Los endpoints que no son públicos piden el token en el header
`Authorization: Bearer <token>`. En el repo dejamos la colección de Postman
(`VelourPostman_E5.postman_collection.json`) ya armada para probarlos.

## Notas

El pago es simulado: si se paga con tarjeta se aprueba al momento, y con
transferencia o contra entrega queda pendiente. La notificación del pedido por
correo también es simulada, en vez de mandar un correo real se imprime el aviso
en la consola del servidor.
