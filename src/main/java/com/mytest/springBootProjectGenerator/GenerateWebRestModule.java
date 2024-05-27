package com.mytest.springBootProjectGenerator;


import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import static com.mytest.springBootProjectGenerator.Utils.*;

public class GenerateWebRestModule {
    private final String path;
    private final String packageName;
    private final Map<Integer, List<BeanProperties>> allBeans;
    private final Map<Integer, List<BeanProperties>> allRepos;
    private final int modulesAmount;
    private final int jpa_modules_amount;

    public GenerateWebRestModule(String path, String packageName, Map<Integer, List<BeanProperties>> allBeans, Map<Integer, List<BeanProperties>> allRepos, int modulesAmount, int jpa_modules_amount) {
        this.path = path;
        this.packageName = packageName;
        this.allBeans = allBeans;
        this.allRepos = allRepos;
        this.modulesAmount = modulesAmount;
        this.jpa_modules_amount = jpa_modules_amount;
    }

    public void generateAll() throws Exception {
        String sourcePom = "./src/main/resources/web_rest_module_pom.txt";
        String moduleDir = createModule(path, "web-rest-app-module", sourcePom);
        File targetPom = new File(moduleDir + "/pom.xml");
        copyTemplateFile(new File("./src/main/resources/application.txt"), new File(moduleDir + "/src/main/resources/application.properties"));
        StringBuilder dependencies = new StringBuilder();
        for (int beanModuleNumber = 0; beanModuleNumber < modulesAmount; beanModuleNumber++) {
            dependencies.append("""
                            <dependency>
                               <groupId>com.mytests.spring</groupId>
                               <version>0.0.1-SNAPSHOT</version>
                    """);
            dependencies.append("           <artifactId>beans-module").append(beanModuleNumber).append("</artifactId>\n");
            dependencies.append("        </dependency>\n");
        }
        for (int jpaModuleNumber = 0; jpaModuleNumber < jpa_modules_amount; jpaModuleNumber++) {
            dependencies.append("""
                            <dependency>
                               <groupId>com.mytests.spring</groupId>
                               <version>0.0.1-SNAPSHOT</version>
                    """);
            dependencies.append("           <artifactId>jpa-module").append(jpaModuleNumber).append("</artifactId>\n");
            dependencies.append("        </dependency>\n");
        }
        modifyFile(targetPom,"DDD", String.valueOf(dependencies));
        PrintWriter appClassWriter = createFile(moduleDir + "/src/main/java/com/mytests/spring/", packageName, "WebAppModuleApplication", ".java");
        if (appClassWriter != null) {
            appClassWriter.println("""
                    package com.mytests.spring.webRestAppModule;
                    
                    
                    import org.springframework.boot.SpringApplication;
                    import org.springframework.boot.autoconfigure.SpringBootApplication;
                    import org.springframework.context.annotation.Import;
                    
                    @SpringBootApplication(scanBasePackages = {"com.mytests.spring.webRestAppModule", "com.mytests.spring.jpa"})
                    @Import({
                    """);
            for (int i = 0; i < modulesAmount-1; i++) {
                appClassWriter.println("      com.mytests.spring.beansModule" + i +".Config" + i + ".class, ");
            }
            appClassWriter.println("      com.mytests.spring.beansModule" + (modulesAmount-1) + ".Config"+ (modulesAmount-1) +".class }) ");
            appClassWriter.println("""
                    public class WebAppModuleApplication {
                    
                        public static void main(String[] args) {
                            SpringApplication.run(WebAppModuleApplication.class, args);
                        }
                    
                    }
                    """);
            appClassWriter.close();
        }
        for (int i = 0; i < modulesAmount; i++) {
            generateControllerForBeans(i, moduleDir);
        }
        for (int i = 0; i < jpa_modules_amount; i++) {
            generateControllerForRepos(i, moduleDir);
        }
    }
    public void generateControllerForBeans(int moduleNumber, String moduleDir) throws Exception {
        String controllerClassName = "RestControllerForBeansModule"+ moduleNumber;
        PrintWriter writer = createFile(moduleDir + "/src/main/java/com/mytests/spring/", packageName, controllerClassName, ".java");
        if (writer != null) {
            writer.println("package com.mytests.spring." + packageName + ";\n\n");
            writer.println("import " + allBeans.get(moduleNumber).get(0).getPackageName() + "."  + "*;\n");
            writer.println("""
                    import org.springframework.web.bind.annotation.GetMapping;
                    import org.springframework.web.bind.annotation.RequestMapping;
                    import org.springframework.web.bind.annotation.RestController;
                    import org.springframework.beans.factory.annotation.Autowired;
                    import org.springframework.beans.factory.annotation.Qualifier;
                    import java.util.List;
                    
                    @RestController
                    """);
            writer.println("@RequestMapping(\"/beans-module" + moduleNumber + "\")");
            writer.println("public class " + controllerClassName + " {\n\n");
            for (BeanProperties bean : allBeans.get(moduleNumber)) {
                writer.println("    @Autowired");
                writer.println("    @Qualifier(\"" + bean.getBeanName() + "\")");
                writer.println("    private " + bean.getClassName() + " " + bean.getBeanName() + ";\n");
            }
            for (BeanProperties bean : allBeans.get(moduleNumber)) {
                String s = bean.getBeanName().substring(0, 1).toUpperCase() + bean.getBeanName().substring(1);
                writer.println("    @GetMapping(\"/get" + s + "\")");
                writer.println("    public String get" + s + "() {");
                writer.println("       return " + bean.getBeanName() + ".getId();" );
                writer.println("    }");
            }

            writer.println(" }");
            writer.close();
        }
    }
    public void generateControllerForRepos(int moduleNumber, String moduleDir) throws Exception {
        String controllerClassName = "RestControllerForJpaModule"+ moduleNumber;
        PrintWriter writer = createFile(moduleDir + "/src/main/java/com/mytests/spring/", packageName, controllerClassName, ".java");
        if (writer != null) {
            writer.println("package com.mytests.spring." + packageName + ";\n\n");
            writer.println("import " + allRepos.get(moduleNumber).get(0).getPackageName() + "."  + "*;\n");
            writer.println("""
                    import org.springframework.web.bind.annotation.GetMapping;
                    import org.springframework.web.bind.annotation.RequestMapping;
                    import org.springframework.web.bind.annotation.RestController;
                    import org.springframework.beans.factory.annotation.Autowired;
                    import java.util.List;
                    
                    @RestController
                    """);
            writer.println("@RequestMapping(\"/jpa-module" + moduleNumber + "\")");
            writer.println("public class " + controllerClassName + " {\n\n");
            for (BeanProperties bean : allRepos.get(moduleNumber)) {
                writer.println("    @Autowired");
                writer.println("    private " + bean.getClassName() + " " + bean.getBeanName() + ";\n");
            }
            for (BeanProperties bean : allRepos.get(moduleNumber)) {
                writer.println("    @GetMapping(\"/all" + bean.getEntityName() + "s\")");
                writer.println("    public List<" + bean.getEntityName() + "> getAll" + bean.getEntityName() + "s() {");
                writer.println("       return " + bean.getBeanName() + ".findAll();" );
                writer.println("    }");
            }

            writer.println(" }");
            writer.close();
        }
    }
}
