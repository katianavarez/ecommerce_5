package itson.ecommercewebaplication.util;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Utilidad para aplicar paginación a una lista en memoria
 * y configurar los atributos del request necesarios para el JSP.
 */
public class PaginacionUtil {

    public static final int TAMANO_ADMIN = 10;

    /**
     * Aplica paginación a la lista y setea los atributos en el request.
     * @param req       el request donde se setearán los atributos
     * @param lista     la lista completa sin paginar
     * @param atributo  nombre del atributo en el request (ej: "productos")
     * @param tamano    tamaño de página
     * @return          la sublista de la página actual
     */
    public static <T> List<T> paginar(HttpServletRequest req,
                                       List<T> lista,
                                       String atributo,
                                       int tamano) {
        int total = lista.size();
        int totalPaginas = (total == 0) ? 1 : (int) Math.ceil((double) total / tamano);

        String paginaStr = req.getParameter("pagina");
        int pagina = 1;
        if (paginaStr != null && !paginaStr.isBlank()) {
            try { pagina = Integer.parseInt(paginaStr); } catch (NumberFormatException ignored) {}
        }
        if (pagina < 1) pagina = 1;
        if (pagina > totalPaginas) pagina = totalPaginas;

        int desde = (pagina - 1) * tamano;
        int hasta = Math.min(desde + tamano, total);
        List<T> pagina_lista = lista.subList(desde, hasta);

        req.setAttribute(atributo,        pagina_lista);
        req.setAttribute("paginaActual",  pagina);
        req.setAttribute("totalPaginas",  totalPaginas);
        req.setAttribute("totalRegistros", total);
        return pagina_lista;
    }
}
