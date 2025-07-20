package br.com.csonline.debug;

import jakarta.faces.FactoryFinder;
import jakarta.faces.lifecycle.LifecycleFactory;
import jakarta.faces.application.ApplicationFactory;

public class FactoryFinderTest {
    public static void main(String[] args) {
        try {
            Object lifecycleFactory = FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
            System.out.println("LifecycleFactory: " + lifecycleFactory);

            Object applicationFactory = FactoryFinder.getFactory(FactoryFinder.APPLICATION_FACTORY);
            System.out.println("ApplicationFactory: " + applicationFactory);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
