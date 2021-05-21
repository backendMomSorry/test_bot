//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories
//import org.springframework.jdbc.datasource.DriverManagerDataSource
//import org.springframework.orm.jpa.JpaTransactionManager
//import org.springframework.orm.jpa.JpaVendorAdapter
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
//import org.springframework.orm.jpa.vendor.Database
//import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
//import javax.sql.DataSource
//
//@Configuration
//@EnableJpaRepositories//"packages.to.scan")
//open class JpaConfiguration {
//
//    @Bean
//    open fun dataSource(): DataSource {
//        val dataSource = DriverManagerDataSource()
//        dataSource.setDriverClassName("org.postgresql.Driver")
//        dataSource.url = "jdbc:postgresql://localhost:5432/sopt"
//        dataSource.username = "sopt-user"
//        dataSource.password = "sopt-password"
//        return dataSource
//    }
//
////    @Bean
////    open fun transactionManager(emf: EntityManagerFactory?): JpaTransactionManager {
////        return JpaTransactionManager(emf)
////    }
//
//    @Bean
//    open fun jpaVendorAdapter(): JpaVendorAdapter {
//        val jpaVendorAdapter = HibernateJpaVendorAdapter()
//        jpaVendorAdapter.setDatabase(Database.MYSQL)
//        jpaVendorAdapter.setGenerateDdl(true)
//        return jpaVendorAdapter
//    }
//
//    @Bean
//    open fun entityManagerFactory(): LocalContainerEntityManagerFactoryBean {
//        val lemfb = LocalContainerEntityManagerFactoryBean()
//        lemfb.dataSource = dataSource()
//        lemfb.jpaVendorAdapter = jpaVendorAdapter()
//        lemfb.setPackagesToScan("packages.containing.entity.classes")
//        return lemfb
//    }
//}