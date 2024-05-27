package com.mytest.springBootProjectGenerator;


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mytest.springBootProjectGenerator.Utils.*;

public class GenerateJPAModule {
    private final int moduleNumber;
    private final int entitiesAmount;
    private final int columnsAmount;
    private final String packageName;
    private final String moduleName;
    private final String path;

    public GenerateJPAModule(int moduleNumber, int entitiesAmount, int columnsAmount, String packageName, String moduleName, String path) {
        this.moduleNumber = moduleNumber;
        this.entitiesAmount = entitiesAmount;
        this.columnsAmount = columnsAmount;
        this.packageName = packageName;
        this.moduleName = moduleName;
        this.path = path;
    }

    public List<BeanProperties> generateAll() throws IOException {
        List<BeanProperties> repositories = generateModule();
        File jpaAppClassFile = new File(path + "/" + moduleName + "/src/main/java/com/mytests/spring/" + "jpa/"+packageName + "/JpaModule" + moduleNumber + "Application.java");
        File sourceFile = new File("./src/main/resources/jpaModuleAppClass.txt");
        copyTemplateFile(sourceFile, jpaAppClassFile);
        modifyFile(jpaAppClassFile, "X", String.valueOf(moduleNumber));
        return repositories;
    }

    private List<BeanProperties> generateModule() throws IOException {
        String sourcePom = "./src/main/resources/jpa_module_pom.txt";
        String moduleDir = createModule(path, moduleName, sourcePom);
        copyTemplateFile(new File("./src/main/resources/application.txt"), new File(moduleDir + "/src/main/resources/application.properties"));
        List<BeanProperties> repoList = new ArrayList<>();
        for (int i = 0; i < entitiesAmount; i++) {
            String entity = generateEntity(i, columnsAmount);
            String repo = generateRepository(entity,columnsAmount);
            repoList.add(new BeanProperties("com.mytests.spring.jpa." + packageName, repo, repo.substring(0, 1).toLowerCase()+repo.substring(1), entity));
        }
        return repoList;
    }
    private String generateEntity(int entityNumber, int fieldsAmount){

        String beanClassName = "Entity"+ moduleNumber + "_" + entityNumber;
        PrintWriter writer = createFile(path + "/" + moduleName + "/src/main/java/com/mytests/spring/", "jpa/"+packageName, beanClassName, ".java");
        if (writer != null) {
            writer.println("package com.mytests.spring.jpa." + packageName + ";\n\n");
            writer.println("""
                    import jakarta.persistence.Entity;
                    import jakarta.persistence.GeneratedValue;
                    import jakarta.persistence.Id;
                    
                    @Entity
                    """);
            writer.println("public class " + beanClassName + "  {\n");
            writer.println("""
                    
                        @Id @GeneratedValue
                        private Long id;
                    
                        public void setId(Long id) {
                            this.id = id;
                        }
                    
                        public Long getId() {
                            return id;
                        }
                    """);
            for (int i = 0; i < fieldsAmount; i++) {
                writer.println("   private String column" + i + ";\n");
            }

            for (int i = 0; i < fieldsAmount; i++) {
                writer.println("   public String getColumn" + i + "() {");
                writer.println("     return this.column" + i + "; \n    }\n");
            }
            for (int i = 0; i < fieldsAmount; i++) {
                writer.println("   public void setColumn" + i + "( String c) {");
                writer.println("     this.column" + i + " = c; \n    }\n");
            }
            writer.println("   public " + beanClassName + "() { } \n");
            writer.println(" }");
            writer.close();
        }
       return beanClassName;
    }
    private String generateRepository(String entityName, int fieldsAmount) throws IOException {

        String beanClassName = entityName+"Repository";
        PrintWriter writer = createFile(path + "/" + moduleName + "/src/main/java/com/mytests/spring/", "jpa/"+packageName, beanClassName, ".java");
        if (writer != null) {

            writer.println("package com.mytests.spring.jpa." + packageName + ";\n\n");
            writer.println("""
                    import org.springframework.data.jpa.repository.Query;
                    import org.springframework.data.repository.CrudRepository;
                    import org.springframework.data.repository.query.Param;
                    
                    import java.util.List;
                    
                    """);
            writer.println("public interface " + beanClassName + " extends CrudRepository<" + entityName + ", Integer> {\n");
            writer.println("   List<" + entityName + "> findAll();\n");
            for (int i = 0; i < fieldsAmount; i++) {
                writer.println("   @Query(\"select e from " + entityName + " e where e.column" + i + " = :arg\")");
                writer.println("   List<" + entityName + "> customQuery" + i + "(@Param(\"arg\") String arg) ;\n");
            }
            writer.println(" }");
            writer.close();
        }
        return beanClassName;
    }
}
