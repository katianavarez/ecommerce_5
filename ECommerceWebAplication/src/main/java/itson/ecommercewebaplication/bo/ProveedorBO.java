package itson.ecommercewebaplication.bo;

import itson.ecommercewebaplication.dao.ProveedorDAO;
import itson.ecommercewebaplication.models.Proveedor;
import java.util.List;
import java.util.regex.Pattern;

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
        return proveedorDAO.guardar(proveedor);
    }

    public Proveedor actualizar(Proveedor proveedor) throws Exception {
        if (proveedorDAO.obtenerPorId(proveedor.getId()) == null) {
            throw new Exception("Proveedor no encontrado");
        }
        validar(proveedor);
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

    private void validar(Proveedor p) throws Exception {
        if (p.getNombre() == null || p.getNombre().isBlank()) {
            throw new Exception("El nombre del proveedor es requerido");
        }
        if (p.getRfc() != null && !p.getRfc().isBlank()
                && !RFC_PATTERN.matcher(p.getRfc().toUpperCase()).matches()) {
            throw new Exception("El RFC tiene un formato inválido");
        }
        if (p.getCorreo() != null && !p.getCorreo().isBlank()
                && !CORREO_PATTERN.matcher(p.getCorreo()).matches()) {
            throw new Exception("El correo tiene un formato inválido");
        }
    }
}
