//package uz.department.uai.config;
//
//import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//
//import javax.sql.DataSource;
//
////@Configuration
//public class DataSourceConfig {
//
//    // 1. PostgreSQL uchun sozlamalarni o'qish
//    @Bean
//    @Primary // Asosiy ma'lumotlar bazasi sifatida belgilash
//    @ConfigurationProperties("spring.datasource.postgres")
//    public DataSourceProperties postgresDataSourceProperties() {
//        return new DataSourceProperties();
//    }
//
//    // 2. PostgreSQL uchun DataSource yaratish
//    @Bean
//    @Primary
//    public DataSource postgresDataSource() {
//        return postgresDataSourceProperties()
//                .initializeDataSourceBuilder()
//                .build();
//    }
//
//    // 3. Oracle uchun sozlamalarni o'qish
//    //    @Bean
//    //    @ConfigurationProperties("spring.datasource.oracle")
//    //    public DataSourceProperties oracleDataSourceProperties() {
//    //        return new DataSourceProperties();
//    //    }
//    //
//    //    // 4. Oracle uchun DataSource yaratish
//    //    @Bean("oracleDataSource") // Bean'ga maxsus nom berish
//    //    public DataSource oracleDataSource() {
//    //        return oracleDataSourceProperties()
//    //                .initializeDataSourceBuilder()
//    //                .build();
//    //    }
//}

package uz.department.uai.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder; // Import this
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    // 1. PostgreSQL Properties Bean (no changes here)
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.postgres")
    public DataSourceProperties postgresDataSourceProperties() {
        return new DataSourceProperties();
    }

    // 2. PostgreSQL DataSource Bean (UPDATED)
    @Bean("postgresDataSource")
    @Primary
    public DataSource postgresDataSource() {
        // Use the properties to build the DataSource
        DataSourceProperties properties = postgresDataSourceProperties();
        System.out.println("Postgresql config");
        return DataSourceBuilder.create()
                .driverClassName(properties.determineDriverClassName()) // <-- The fix is here
                .url(properties.getUrl())
                .username(properties.getUsername())
                .password(properties.getPassword())
                .build();
    }

//    // 3. Oracle Properties Bean (no changes here)
//    @Bean
//    @ConfigurationProperties("spring.datasource.oracle")
//    public DataSourceProperties oracleDataSourceProperties() {
//        return new DataSourceProperties();
//    }
//
//    // 4. Oracle DataSource Bean (UPDATED)
//    @Bean("oracleDataSource")
//    public DataSource oracleDataSource() {
//        // Use the properties to build the DataSource
//        DataSourceProperties properties = oracleDataSourceProperties();
//        return DataSourceBuilder.create()
//                .driverClassName(properties.determineDriverClassName()) // <-- The fix is here
//                .url(properties.getUrl())
//                .username(properties.getUsername())
//                .password(properties.getPassword())
//                .build();
//    }
}