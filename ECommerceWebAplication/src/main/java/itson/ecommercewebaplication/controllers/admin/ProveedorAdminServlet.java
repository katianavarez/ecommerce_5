package itson.ecommercewebaplication.controllers.admin;

import itson.ecommercewebaplication.bo.ProveedorBO;
import itson.ecommercewebaplication.models.Proveedor;
import itson.ecommercewebaplication.util.PaginacionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

/**
 * Controlador del CRUD de proveedores en el panel admin. Enruta el alta,
 * edición, archivado (baja lógica) y reactivación, armando el {@link Proveedor}
 * desde el formulario y delegando las validaciones (RFC, correo, campos
 * obligatorios) en {@code ProveedorBO}. Si el guardado falla, vuelve a
 * mostrar el formulario con los datos capturados y el mensaje de error.
 *
 * @author Hector Javier Alonso Zaragoza
 * @author Freddy Ali Castro Roman
 * @author Katia Ximena Navarez Espinoza
 * @author Alejandro Rodriguez Lugo
 */
@WebServlet(name = "ProveedorAdminServlet", urlPatterns = {"/admin/proveedores"})
public class ProveedorAdminServlet extends HttpServlet {

    private ProveedorBO proveedorBO;

    @Override
    public void init() throws ServletException {
        proveedorBO = new ProveedorBO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String accion = req.getParameter("accion");
        try {
            if ("nuevo".equals(accion)) {
                req.getRequestDispatcher("/views/admin/admin-proveedor-form.jsp").forward(req, res);
            } else if ("editar".equals(accion)) {
                int id = Integer.parseInt(req.getParameter("id"));
                req.setAttribute("proveedor", proveedorBO.obtenerPorId(id));
                req.getRequestDispatcher("/views/admin/admin-proveedor-form.jsp").forward(req, res);
            } else {
                listar(req, res);
            }
        } catch (Exception e) {
            req.setAttribute("error", "No se pudo completar la operación. " + e.getMessage());
            listar(req, res);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String accion = req.getParameter("accion");
        try {
            if ("crear".equals(accion)) {
                proveedorBO.crear(buildProveedorFromRequest(req));
                res.sendRedirect(req.getContextPath() + "/admin/proveedores?success=created");
            } else if ("actualizar".equals(accion)) {
                int id = Integer.parseInt(req.getParameter("id"));
                Proveedor existente = proveedorBO.obtenerPorId(id);
                Proveedor p = buildProveedorFromRequest(req);
                p.setId(id);
                p.setActivo(existente != null ? existente.isActivo() : true);
                proveedorBO.actualizar(p);
                res.sendRedirect(req.getContextPath() + "/admin/proveedores?success=updated");
            } else if ("archivar".equals(accion)) {
                proveedorBO.archivar(Integer.parseInt(req.getParameter("id")));
                res.sendRedirect(req.getContextPath() + "/admin/proveedores?success=archived");
            } else if ("reactivar".equals(accion)) {
                proveedorBO.reactivar(Integer.parseInt(req.getParameter("id")));
                res.sendRedirect(req.getContextPath() + "/admin/proveedores?success=reactivated");
            }
        } catch (Exception e) {
            req.setAttribute("error", "No se pudo guardar el proveedor. " + e.getMessage());
            Proveedor pError = buildProveedorFromRequest(req);
            String idParam = req.getParameter("id");
            if (idParam != null && !idParam.isBlank()) {
                try { pError.setId(Integer.parseInt(idParam)); } catch (NumberFormatException ignored) {}
            }
            req.setAttribute("proveedor", pError);
            req.getRequestDispatcher("/views/admin/admin-proveedor-form.jsp").forward(req, res);
        }
    }

    private void listar(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        PaginacionUtil.paginar(req, proveedorBO.obtenerTodos(), "proveedores", PaginacionUtil.TAMANO_ADMIN);
        req.getRequestDispatcher("/views/admin/admin-proveedores.jsp").forward(req, res);
    }

    private Proveedor buildProveedorFromRequest(HttpServletRequest req) {
        Proveedor p = new Proveedor();
        p.setNombre(trimOrNull(req.getParameter("nombre")));
        p.setRfc(trimOrNull(req.getParameter("rfc")));
        p.setTelefono(trimOrNull(req.getParameter("telefono")));
        p.setCorreo(trimOrNull(req.getParameter("correo")));
        p.setDireccion(trimOrNull(req.getParameter("direccion")));
        p.setActivo(true);
        return p;
    }

    private String trimOrNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
