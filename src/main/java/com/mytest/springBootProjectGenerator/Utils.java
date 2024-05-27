package com.mytest.springBootProjectGenerator;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;

public class Utils {
    public static void copyTemplateFile(File source, File dest) throws IOException {
        try {
            Files.copy(source.toPath(), dest.toPath());
        } catch (FileAlreadyExistsException e){
            System.out.println("file already exists: "+e.getLocalizedMessage());
        }

    }
    public static void modifyFile(File fileToBeModified, String oldString, String newString)
    {
        StringBuilder oldContent = new StringBuilder();
        BufferedReader reader = null;
        FileWriter writer = null;
        try
        {
            reader = new BufferedReader(new FileReader(fileToBeModified));
            String line = reader.readLine();
            while (line != null)
            {
                oldContent.append(line).append(System.lineSeparator());
                line = reader.readLine();
            }
            String newContent = oldContent.toString().replaceAll(oldString, newString);
            writer = new FileWriter(fileToBeModified);
            writer.write(newContent);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                assert reader != null;
                reader.close();
                assert writer != null;
                writer.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
    public static PrintWriter createFile(String prefix, String packageName, String className, String extension) {
        PrintWriter writer = null;
        try {
            File mypackage = new File(prefix + packageName);
            mypackage.mkdir();
            writer = new PrintWriter(mypackage.getPath() + "/" + className + extension, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return writer;
    }
    public static String createModule(String modulePath, String moduleName, String sourcePom) throws IOException {
        String moduleDir = modulePath + "/" + moduleName;
        try {
            if(!new File(moduleDir).mkdirs() ){
                System.out.println("make sure that the target directory doesn't exist: "+ moduleDir);
            }
            if(!new File(moduleDir + "/src/main/java/com/mytests/spring/jpa/").mkdirs()){
                System.out.println("error creating src directories in " + moduleDir);
            };
            if(!new File(moduleDir + "/src/main/resources").mkdirs()){
                System.out.println("error creating src directories in " + moduleDir);
            };
        } catch (Exception e){
            e.printStackTrace();
        }
        File targetPom = new File(moduleDir + "/pom.xml");
        copyTemplateFile(new File(sourcePom), targetPom);
        modifyFile(targetPom, "XXX", moduleName);

        return moduleDir;
    }
}
