package com.mytest.springBootProjectGenerator;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static com.mytest.springBootProjectGenerator.Utils.*;

public class GenerateXmlBeansModule {

    private final int moduleNumber;
    private final int classesAmount;
    private final String packageName;
    private final String moduleName;
    private final String path;

    public GenerateXmlBeansModule(int moduleNumber, int classesAmount, String packageName, String moduleName, String path) {
        this.moduleNumber = moduleNumber;
        this.classesAmount = classesAmount;
        this.packageName = packageName;
        this.moduleName = moduleName;
        this.path = path;
    }

    public List<BeanProperties> generateAll() throws IOException {
        List<BeanProperties> beansList = new ArrayList<>();
        String moduleDir = createModule(path, moduleName, "./src/main/resources/templates/xml_module_pom.txt");

        for (int i = 0; i < classesAmount; i++) {
            String className = generateBeanClass(i);
            String beanId = className.substring(0, 1).toLowerCase() + className.substring(1);
            beansList.add(new BeanProperties("com.mytests.spring." + packageName, className, beanId, ""));
        }

        generateSpringXmlConfig(beansList, moduleDir);

        return beansList;
    }

    private String generateBeanClass(int count) throws IOException {
        String className = "XmlBean" + moduleNumber + "_" + count;
        PrintWriter writer = createFile(path + "/" + moduleName + "/src/main/java/com/mytests/spring/", packageName, className, ".java");
        if (writer != null) {
            writer.println("package com.mytests.spring." + packageName + ";\n");
            writer.println("public class " + className + " {\n");
            writer.println("    private String id;\n");
            writer.println("    public " + className + "() {}");
            writer.println("    public String getId() { return id; }");
            writer.println("    public void setId(String id) { this.id = id; }");
            writer.println("}");
            writer.close();
        }
        return className;
    }

    private void generateSpringXmlConfig(List<BeanProperties> beansList, String moduleDir) throws IOException {
        new File(moduleDir + "/src/main/resources").mkdirs();
        PrintWriter writer = new PrintWriter(moduleDir + "/src/main/resources/applicationContext-xmlBeansModule" + moduleNumber + ".xml", "UTF-8");

        writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        writer.println("<beans xmlns=\"http://www.springframework.org/schema/beans\"");
        writer.println("       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
        writer.println("       xsi:schemaLocation=\"http://www.springframework.org/schema/beans");
        writer.println("                           http://www.springframework.org/schema/beans/spring-beans.xsd\">");
        writer.println();

        for (BeanProperties bean : beansList) {
            String beanId = bean.getBeanName();
            String fullClassName = bean.getPackageName() + "." + bean.getClassName();
            writer.println("    <bean id=\"" + beanId + "\" class=\"" + fullClassName + "\">");
            writer.println("        <property name=\"id\" value=\"" + beanId + "\"/>");
            writer.println("    </bean>");
        }

        writer.println("</beans>");
        writer.close();
    }
}
