package contextListener;

import dao.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.ws.rs.core.Context;

@WebListener
public class ContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();
        String ctxPath = ctx.getRealPath("");

        ctx.setAttribute("orderDAO",new OrderDAO(ctxPath));
        ctx.setAttribute("rentObjectDAO",new RentACarObjectDAO(ctxPath));
        ctx.setAttribute("shopingCartDAO",new ShopingCartDAO(ctxPath));
        ctx.setAttribute("vehicleDAO",new VehicleDAO(ctxPath));
        ctx.setAttribute("userDAO",new UserDAO(ctxPath));
        ctx.setAttribute("commentDAO",new CommentDAO(ctxPath));
        ctx.setAttribute("trackerDAO",new CancelTrackerDAO(ctxPath));

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

}
