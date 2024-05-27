package com.mytest.springBootProjectGenerator;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.mytest.springBootProjectGenerator.Utils.copyTemplateFile;
import static com.mytest.springBootProjectGenerator.Utils.modifyFile;

/**
 * *
 * <p>Created by irina on 5/23/2024.</p>
 * <p>Project: GenerateSpringBootProject</p>
 * *
 */
public class Main {
    private static String projectPath = "C:/GeneratedSpringBootProject";
    private static int modules_amount = 3;
    private static int jpa_modules_amount = 3;
    private static int entitiesAmount = 3;
    private static int columnsAmount = 3;
    private static int componentsAmount = 3;
    private static int beansAmount = 3;

    public static void main(String[] args) throws Exception {
        ResourceBundle rb = ResourceBundle.getBundle("config");
        projectPath = rb.getString("project_path");
        System.out.println(projectPath);
        modules_amount = Integer.parseInt(rb.getString("modules_amount"));
        jpa_modules_amount = Integer.parseInt(rb.getString("jpa_modules_amount"));
        entitiesAmount = Integer.parseInt(rb.getString("entities_amount"));
        columnsAmount = Integer.parseInt(rb.getString("columns_amount"));
        componentsAmount = Integer.parseInt(rb.getString("components_amount"));
        beansAmount = Integer.parseInt(rb.getString("beans_amount"));
        generateProject();
    }

    private static void generateProject() throws Exception {
        Map<Integer, List<BeanProperties>> allBeansByModule = new HashMap<>();
        Map<Integer, List<BeanProperties>> allReposByModule = new HashMap<>();

        try {
            if (!new File(projectPath).mkdirs()) {
                System.out.println("make sure that the target directory doesn't exist: " + projectPath);
            }
            new File(projectPath + "/beans-modules").mkdirs();
            new File(projectPath + "/jpa-modules").mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
        }

        copyTemplateFile(new File("./src/main/resources/main_pom.txt"), new File(projectPath + "/pom.xml"));

        File beansModulesPom = new File(projectPath + "/beans-modules/pom.xml");
        copyTemplateFile(new File("./src/main/resources/beans_modules_pom.txt"), beansModulesPom);
        StringBuilder beansModules = new StringBuilder();
        for (int i = 0; i < modules_amount; i++) {
            beansModules.append("       <module>" + "beans-module").append(i).append("</module>\n");
            GenerateBeansModule beanModule = new GenerateBeansModule(i, componentsAmount, beansAmount, "beansModule" + i, "beans-module" + i, projectPath + "/beans-modules");
            allBeansByModule.put(i, beanModule.generateAll());
        }
        modifyFile(beansModulesPom, "XXX", String.valueOf(beansModules));


        File jpaModulesPom = new File(projectPath + "/jpa-modules/pom.xml");
        copyTemplateFile(new File("./src/main/resources/jpa_modules_pom.txt"), jpaModulesPom);

        StringBuilder jpaBeansModules = new StringBuilder();
        for (int i = 0; i < jpa_modules_amount; i++) {
            jpaBeansModules.append("       <module>" + "jpa-module").append(i).append("</module>\n");
            GenerateJPAModule jpaModule = new GenerateJPAModule(i, entitiesAmount, columnsAmount, "jpaModule" + i, "jpa-module" + i, projectPath + "/jpa-modules");
            allReposByModule.put(i, jpaModule.generateAll());
        }
        modifyFile(jpaModulesPom, "XXX", String.valueOf(jpaBeansModules));

        GenerateWebRestModule webRestModule = new GenerateWebRestModule(projectPath,
                "webRestAppModule", allBeansByModule, allReposByModule, modules_amount, jpa_modules_amount);
        webRestModule.generateAll();

       // allBeansByModule.forEach((k, v) -> System.out.println((k + ":" + v)));
       //allReposByModule.forEach((k, v) -> System.out.println((k + ":" + v.iterator().next().getBeanName() + " " + v.iterator().next().getClassName() + " " + v.iterator().next().getEntityName())));

    }
}
