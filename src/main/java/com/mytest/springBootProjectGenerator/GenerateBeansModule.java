package com.mytest.springBootProjectGenerator;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mytest.springBootProjectGenerator.Utils.*;


public class GenerateBeansModule {

    private final int moduleNumber;
    private final int componentsAmount;
    private final int beansAmount;
    private final String packageName;
    private final String moduleName;
    private final String path;

    public GenerateBeansModule(int moduleNumber, int componentsAmount, int beansAmount, String packageName, String moduleName, String path) {
        this.moduleNumber = moduleNumber;
        this.componentsAmount = componentsAmount;
        this.beansAmount = beansAmount;
        this.packageName = packageName;
        this.moduleName = moduleName;
        this.path = path;
    }
    public List<BeanProperties> generateAll() throws IOException {
        List<BeanProperties> beansList = new ArrayList<>();
        generateModule();
        beansList.addAll(generateModuleConfig());
        for (int i = 0; i < componentsAmount; i++) {
            beansList.addAll(generateServiceAndImplementationComponents(i));
        }

        return beansList;
    }

    private void generateModule() throws IOException {
        String sourcePom = "./src/main/resources/beans_module_pom.txt";
        String moduleDir = createModule(path, moduleName, sourcePom);
    }

    private List<BeanProperties> generateModuleConfig() throws IOException {
        String className = "Config" + moduleNumber;
        List<BeanProperties> beansList = new ArrayList<>();
        PrintWriter writer = createFile(path + "/" + moduleName + "/src/main/java/com/mytests/spring/", packageName, className, ".java");
        if (writer != null) {

            writer.println("package com.mytests.spring." + packageName + ";\n\n" +
                           "import org.springframework.context.annotation.Configuration;\n" +
                           "import org.springframework.context.annotation.ComponentScan;\n" +
                           "import org.springframework.beans.factory.annotation.Qualifier;\n" +
                           "import org.springframework.context.annotation.Bean;\n");


            writer.println("@Configuration(\"" + className.toLowerCase() + "\")\n" +
                           "@ComponentScan\n" +
                           "public class " + className + " {\n");

            for (int i = 0; i < beansAmount; i++) {
                String beanClassName = generateBeanClass(i);
                String beanName = beanClassName.substring(0, 1).toLowerCase() + beanClassName.substring(1);
                for (int j = 0; j <= 3; j++) {
                    String qualifier = beanName + "Qualifier" + j;
                    writer.println("      @Qualifier(\"" + qualifier + "\")");
                    writer.println("      @Bean");

                    writer.println("      public " + beanClassName + " " + beanClassName.toLowerCase() + "_" + j + "() {\n" +
                                   "         return new " + beanClassName + "(\"" + beanClassName.toLowerCase() + "\");\n" +
                                   "      }\n");
                    beansList.add(new BeanProperties("com.mytests.spring." + packageName, beanClassName, qualifier, ""));
                }
            }
            writer.println("}");
            writer.close();
        }
        return beansList;
    }

    private String generateBeanClass(int count) throws IOException {
        String beanClassName = "Bean"+ moduleNumber + "_" + count;
        PrintWriter writer = createFile(path + "/" + moduleName + "/src/main/java/com/mytests/spring/", packageName, beanClassName, ".java");
        if (writer != null) {
            writer.println("package com.mytests.spring." + packageName + ";\n\n" +
                           "import org.springframework.beans.factory.annotation.Autowired;\n"+
                           "import org.springframework.beans.factory.annotation.Qualifier;\n\n" +
                           "public class " + beanClassName + " {\n" +
                           "    private final String id;\n" +
                           "    public String getId() {\n" +
                           "      return id;\n  }");
            writer.println("    public " + beanClassName + "(String id) {\n" +
                           "       this.id = id;\n  }");

            writer.println(" }");
            writer.close();
        }
        return beanClassName;
    }

    private List<BeanProperties> generateServiceAndImplementationComponents(int count) throws IOException {
        List<BeanProperties> beansList = new ArrayList<>();
        String serviceName = "Service" + moduleNumber + "_" + count;
        PrintWriter serviceWriter = createFile(path + "/" + moduleName + "/src/main/java/com/mytests/spring/",
                packageName, serviceName, ".java");
        if (serviceWriter != null) {
            serviceWriter.println("package com.mytests.spring." + packageName + ";\n\n");
            serviceWriter.println("public interface " + serviceName + " {\n");
            serviceWriter.println("""
                        public String getId();
                    }
                    """);

            serviceWriter.close();
        }
        for (int i = 0; i < 3; i++) {
            String componentName = serviceName + "Component" + i;
            String beanName = componentName.substring(0, 1).toLowerCase() + componentName.substring(1);
            PrintWriter writer = createFile(path + "/" + moduleName + "/src/main/java/com/mytests/spring/",
                    packageName, componentName, ".java");
            writer.println("package com.mytests.spring." + packageName + ";\n\n");
            writer.println("""
                    import org.springframework.stereotype.Component;
                    
                    @Component
                    """);
            writer.println("public class " + componentName + " implements " + serviceName + " {\n");
            writer.println("""
                        @Override
                        public String getId() {
                    """);
            writer.println("       return \"" + beanName +"\"; ");
            writer.println("""
                        }
                    }
                    """);
            writer.close();
            beansList.add(new BeanProperties("com.mytests.spring." + packageName, serviceName, beanName,""));
        }

        return beansList;
    }
}
