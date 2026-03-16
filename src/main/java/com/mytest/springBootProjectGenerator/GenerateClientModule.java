package com.mytest.springBootProjectGenerator;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.mytest.springBootProjectGenerator.Utils.*;

public class GenerateClientModule {

    private final String path;
    private final String packageName;
    private final Map<Integer, List<BeanProperties>> allBeans;
    private final Map<Integer, List<BeanProperties>> allRepos;
    private final Map<Integer, List<BeanProperties>> allXmlBeans;
    private final int modulesAmount;
    private final int jpaModulesAmount;

    public GenerateClientModule(String path, String packageName,
                                Map<Integer, List<BeanProperties>> allBeans,
                                Map<Integer, List<BeanProperties>> allRepos,
                                Map<Integer, List<BeanProperties>> allXmlBeans,
                                int modulesAmount, int jpaModulesAmount) {
        this.path = path;
        this.packageName = packageName;
        this.allBeans = allBeans;
        this.allRepos = allRepos;
        this.allXmlBeans = allXmlBeans;
        this.modulesAmount = modulesAmount;
        this.jpaModulesAmount = jpaModulesAmount;
    }

    public void generateAll() throws IOException {
        String moduleDir = createModule(path, "client-module", "./src/main/resources/client_module_pom.txt");

        generateApplicationProperties(moduleDir);

        List<String> allClientInterfaceNames = new ArrayList<>();

        for (int i = 0; i < modulesAmount; i++) {
            String name = generateBeansClientInterface(i, moduleDir);
            allClientInterfaceNames.add(name);
        }
        for (int i = 0; i < jpaModulesAmount; i++) {
            String name = generateJpaClientInterface(i, moduleDir);
            allClientInterfaceNames.add(name);
        }
        for (int i = 0; i < allXmlBeans.size(); i++) {
            String name = generateXmlClientInterface(i, moduleDir);
            allClientInterfaceNames.add(name);
        }

        generateClientApplication(moduleDir, allClientInterfaceNames);
    }

    private void generateApplicationProperties(String moduleDir) throws IOException {
        PrintWriter writer = new PrintWriter(moduleDir + "/src/main/resources/application.properties", "UTF-8");
        writer.println("spring.main.web-application-type=none");
        writer.println("spring.http.serviceclient.default.base-url=http://localhost:8080");
        writer.close();
    }

    private String generateBeansClientInterface(int moduleNumber, String moduleDir) throws IOException {
        String interfaceName = "BeansModule" + moduleNumber + "Client";
        PrintWriter writer = createFile(moduleDir + "/src/main/java/com/mytests/spring/", packageName, interfaceName, ".java");
        if (writer != null) {
            writer.println("package com.mytests.spring." + packageName + ";\n");
            writer.println("""
                    import org.springframework.web.service.annotation.GetExchange;
                    import org.springframework.web.service.annotation.HttpExchange;
                    """);
            writer.println("@HttpExchange(\"/beans-module" + moduleNumber + "\")");
            writer.println("public interface " + interfaceName + " {\n");
            for (BeanProperties bean : allBeans.get(moduleNumber)) {
                String s = capitalize(bean.getBeanName());
                writer.println("    @GetExchange(\"/get" + s + "\")");
                writer.println("    String get" + s + "();\n");
            }
            writer.println("}");
            writer.close();
        }
        return interfaceName;
    }

    private String generateJpaClientInterface(int moduleNumber, String moduleDir) throws IOException {
        String interfaceName = "JpaModule" + moduleNumber + "Client";
        PrintWriter writer = createFile(moduleDir + "/src/main/java/com/mytests/spring/", packageName, interfaceName, ".java");
        if (writer != null) {
            writer.println("package com.mytests.spring." + packageName + ";\n");
            writer.println("""
                    import org.springframework.web.service.annotation.GetExchange;
                    import org.springframework.web.service.annotation.HttpExchange;
                    """);
            writer.println("@HttpExchange(\"/jpa-module" + moduleNumber + "\")");
            writer.println("public interface " + interfaceName + " {\n");
            for (BeanProperties bean : allRepos.get(moduleNumber)) {
                writer.println("    @GetExchange(\"/all" + bean.getEntityName() + "s\")");
                writer.println("    String getAll" + bean.getEntityName() + "s();\n");
            }
            writer.println("}");
            writer.close();
        }
        return interfaceName;
    }

    private String generateXmlClientInterface(int moduleNumber, String moduleDir) throws IOException {
        String interfaceName = "XmlModule" + moduleNumber + "Client";
        PrintWriter writer = createFile(moduleDir + "/src/main/java/com/mytests/spring/", packageName, interfaceName, ".java");
        if (writer != null) {
            writer.println("package com.mytests.spring." + packageName + ";\n");
            writer.println("""
                    import org.springframework.web.service.annotation.GetExchange;
                    import org.springframework.web.service.annotation.HttpExchange;
                    """);
            writer.println("@HttpExchange(\"/xml-module" + moduleNumber + "\")");
            writer.println("public interface " + interfaceName + " {\n");
            for (BeanProperties bean : allXmlBeans.get(moduleNumber)) {
                String s = capitalize(bean.getBeanName());
                writer.println("    @GetExchange(\"/get" + s + "\")");
                writer.println("    String get" + s + "();\n");
            }
            writer.println("}");
            writer.close();
        }
        return interfaceName;
    }

    private void generateClientApplication(String moduleDir, List<String> clientInterfaceNames) throws IOException {
        PrintWriter writer = createFile(moduleDir + "/src/main/java/com/mytests/spring/", packageName, "ClientApplication", ".java");
        if (writer == null) return;

        writer.println("package com.mytests.spring." + packageName + ";\n");
        writer.println("""
                import org.springframework.boot.CommandLineRunner;
                import org.springframework.boot.SpringApplication;
                import org.springframework.boot.autoconfigure.SpringBootApplication;
                import org.springframework.context.annotation.Bean;
                import org.springframework.web.service.registry.ImportHttpServices;
                """);

        writer.print("@ImportHttpServices(types = {");
        for (int i = 0; i < clientInterfaceNames.size(); i++) {
            writer.print("\n        " + clientInterfaceNames.get(i) + ".class");
            if (i < clientInterfaceNames.size() - 1) writer.print(",");
        }
        writer.println("})");

        writer.println("@SpringBootApplication");
        writer.println("public class ClientApplication {\n");

        writer.println("""
                    public static void main(String[] args) {
                        SpringApplication.run(ClientApplication.class, args);
                    }

                """);

        // CommandLineRunner parameters
        writer.print("    @Bean\n    public CommandLineRunner commandLineRunner(");
        for (int i = 0; i < clientInterfaceNames.size(); i++) {
            String interfaceName = clientInterfaceNames.get(i);
            writer.print("\n            " + interfaceName + " " + decapitalize(interfaceName));
            if (i < clientInterfaceNames.size() - 1) writer.print(",");
        }
        writer.println(") {");
        writer.println("        return args -> {");

        // Call all beans-module methods
        for (int i = 0; i < modulesAmount; i++) {
            String clientParam = decapitalize("BeansModule" + i + "Client");
            for (BeanProperties bean : allBeans.get(i)) {
                String s = capitalize(bean.getBeanName());
                writer.println("            System.out.println(" + clientParam + ".get" + s + "());");
            }
        }
        // Call all jpa-module methods
        for (int i = 0; i < jpaModulesAmount; i++) {
            String clientParam = decapitalize("JpaModule" + i + "Client");
            for (BeanProperties bean : allRepos.get(i)) {
                writer.println("            System.out.println(" + clientParam + ".getAll" + bean.getEntityName() + "s());");
            }
        }
        // Call all xml-module methods
        for (int i = 0; i < allXmlBeans.size(); i++) {
            String clientParam = decapitalize("XmlModule" + i + "Client");
            for (BeanProperties bean : allXmlBeans.get(i)) {
                String s = capitalize(bean.getBeanName());
                writer.println("            System.out.println(" + clientParam + ".get" + s + "());");
            }
        }

        writer.println("        };");
        writer.println("    }");
        writer.println("}");
        writer.close();
    }

    private static String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    private static String decapitalize(String s) {
        return s.substring(0, 1).toLowerCase() + s.substring(1);
    }
}
