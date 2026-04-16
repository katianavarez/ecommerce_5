/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.velour.velourappweb.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 *
 * @author katia
 */
public class JPAUtil {

    private static JPAUtil instance;
    private EntityManagerFactory emf;

    private JPAUtil() {
        try {
            emf = Persistence.createEntityManagerFactory("VelourPU");
        } catch (Exception e) {
            throw new RuntimeException("Error al crear EntityManagerFactory", e);
        }
    }

    public static synchronized JPAUtil getInstance() {
        if (instance == null) {
            instance = new JPAUtil();
        }
        return instance;
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
