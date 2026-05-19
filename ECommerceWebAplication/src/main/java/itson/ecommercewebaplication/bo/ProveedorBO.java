package itson.ecommercewebaplication.bo;

import itson.ecommercewebaplication.dao.ProveedorDAO;
import itson.ecommercewebaplication.models.Proveedor;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Lógica de negocio de proveedores. Valida los datos de contacto y, en
 * particular, el formato del RFC mexicano y que sea único entre proveedores.
 * Las bajas son lógicas (archivar) para conservar la referencia desde los
 * productos que ese proveedor surtió.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
 */
public class ProveedorBO {

    private final ProveedorDAO proveedorDAO = new ProveedorDAO();

    private static final Pattern RFC_PATTERN =
            Pattern.compile("^[A-ZÑ&]{3,4}\\d{6}[A-Z\\d]{3}$");
    private static final Pattern CORREO_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public List<Proveedor> obtenerTodos() {
        return proveedorDAO.obtenerTodos();
    }

    public List<Proveedor> obtenerActivos() {
        return proveedorDAO.obtenerActivos();
    }

    public Proveedor obtenerPorId(int id) {
        return proveedorDAO.obtenerPorId(id);
    }

    public Proveedor crear(Proveedor proveedor) throws Exception {
        validar(proveedor);
        Proveedor existente = proveedorDAO.obtenerPorRfc(proveedor.getRfc());
        if (existente != null)
            throw new Exception("Ya existe un proveedor registrado con el RFC \"" 
                + proveedor.getRfc().toUpperCase() + "\" ("
                + existente.getNombre() + ").");
        return proveedorDAO.guardar(proveedor);
    }

    public Proveedor actualizar(Proveedor proveedor) throws Exception {
        if (proveedorDAO.obtenerPorId(proveedor.getId()) == null)
            throw new Exception("Proveedor no encontrado.");
        validar(proveedor);
        Proveedor conMismoRfc = proveedorDAO.obtenerPorRfc(proveedor.getRfc());
        if (conMismoRfc != null && conMismoRfc.getId() != proveedor.getId())
            throw new Exception("El RFC \"" + proveedor.getRfc().toUpperCase()
                + "\" ya está registrado por otro proveedor ("
                + conMismoRfc.getNombre() + ").");
        return proveedorDAO.actualizar(proveedor);
    }

    public void archivar(int id) throws Exception {
        if (proveedorDAO.obtenerPorId(id) == null) {
            throw new Exception("Proveedor no encontrado");
        }
        proveedorDAO.archivar(id);
    }

    public void reactivar(int id) throws Exception {
        if (proveedorDAO.obtenerPorId(id) == null) {
            throw new Exception("Proveedor no encontrado");
        }
        proveedorDAO.reactivar(id);
    }

    /**
     * Comprueba los campos obligatorios del proveedor y el formato del RFC
     * y del correo. Es el punto único de validación que reusan crear y
     * actualizar.
     *
     * @throws Exception con un mensaje específico del primer campo inválido
     */
    private void validar(Proveedor p) throws Exception {
        if (p.getNombre() == null || p.getNombre().isBlank())
            throw new Exception("El nombre del proveedor es requerido.");
        if (p.getRfc() == null || p.getRfc().isBlank())
            throw new Exception("El RFC es requerido.");
        if (!RFC_PATTERN.matcher(p.getRfc().toUpperCase()).matches())
            throw new Exception("El RFC tiene un formato inválido (ej: ABC010101XYZ).");
        if (p.getTelefono() == null || p.getTelefono().isBlank())
            throw new Exception("El teléfono es requerido.");
        if (p.getCorreo() == null || p.getCorreo().isBlank())
            throw new Exception("El correo de contacto es requerido.");
        if (!CORREO_PATTERN.matcher(p.getCorreo()).matches())
            throw new Exception("El correo tiene un formato inválido.");
        if (p.getDireccion() == null || p.getDireccion().isBlank())
            throw new Exception("La dirección es requerida.");
    }
}
