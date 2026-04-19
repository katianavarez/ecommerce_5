package com.velour.velourappweb.servlets.admin;

import com.velour.velourappweb.models.Usuario;
import com.velour.velourappweb.services.IUsuarioService;
import com.velour.velourappweb.services.UsuarioService;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author katia
 */
@WebServlet(name = "ClientesAdminServlet", urlPatterns = {"/admin/clientes"})
public class ClientesAdminServlet extends HttpServlet {

    private final IUsuarioService usuarioService = new UsuarioService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        int pagina = 1;
        int tamanioPagina = 10;

        String paginaParam = request.getParameter("pagina");

        if (paginaParam != null && !paginaParam.isBlank()) {
            try {
                pagina = Integer.parseInt(paginaParam);
            } catch (Exception e) {
                pagina = 1;
            }
        }

        List<Usuario> clientes = usuarioService.obtenerClientes(pagina, tamanioPagina);
        long totalClientes = usuarioService.contarClientes();

        long totalPaginas = (long) Math.ceil((double) totalClientes / tamanioPagina);

        request.setAttribute("clientes", clientes);
        request.setAttribute("totalClientes", totalClientes);
        request.setAttribute("totalActivos", totalClientes);
        request.setAttribute("totalVip", 0);
        request.setAttribute("totalSinCompras", 0);
        request.setAttribute("paginaActual", pagina);
        request.setAttribute("totalPaginas", totalPaginas);

        request.getRequestDispatcher("/views/admin/admin-clientes.jsp").forward(request, response);
    }
}
