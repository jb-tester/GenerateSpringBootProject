The application can be used to generate the multi-module Maven-based Spring Boot project.
It contains of:
- specified (by `modules_amount` property) amount of 'bean' modules that contain:
   - single `@Configuration`-annotated class, named `ConfigX` (Config1, Config2,...ConfigN), where N is the specified modules amount
   - each configuration class above defines the `@Bean`-annotated methods for the specified (by `beans_amount` property) amount of bean classes.
     For each bean class 4 beans are defined, each bean method has the `@Qualifier` annotation set. Thus each module contains `beans_amount`*4 such beans.
   - specified (by `components_amount` property) number of service interfaces, named `ServiceX_Y`, where X is the module number and Y is the service number;
     each service interface has 3 `@Component`-annotated implementations, named `ServiceX_YComponentZ`. Thus each module has `components_amount`*3 components.
- specified (by `jpa_modules_amount` property) amount of 'jpa' modules that contain:
   - the Application class
   - the specified (by `entities_amount`) property number of entity classes, named `EntityX_Y`, where X is the module number and Y is the entity number,
      each entity defining the specified (by `columns_amount`) number of String fields plus single `id`
   - the similar amount of crud repositories, one per entity, named `EntityX_YRepository`. Each repository defines
      the `findAll()` method and the `@Query`-annotated method per each column.
- single module for the main application and controllers, that depends on all the above modules, and contains:
   - the application class (`@SpringBootApplication`) that:
      - scans the packages of jpa modules
      - imports the config classes of all bean modules
   - multiple `@RestController` classes - one per each bean/jpa module. Each controller:
      - injects all beans and components/ all repositories from the corresponding bean/jpa module
      - defines the single mapping method for each bean/component or repository returning the bean id or the results of repository `findAll()` method
     
TODO:
 - one more module for view controller (Thymeleaf): test model attributes defining/injecting
 - add xml configurations to the beans modules
 - client module 
 - `@ConfigurationProperties` (??)
 - Spring Security 
 - reconsider the beans/components naming (use some random ones)