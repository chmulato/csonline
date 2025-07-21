package br.com.mulato.cso.view.listener;


import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestListener implements ServletContextListener {
    private static final Logger logger = LogManager.getLogger(TestListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("TestListener: contextInitialized");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("TestListener: contextDestroyed");
    }
}
