//package com.cooler.ai.dm.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.PropertySource;
//import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
//import org.springframework.jdbc.datasource.DataSourceTransactionManager;
//import org.springframework.jdbc.datasource.DriverManagerDataSource;
//import org.mybatis.spring.SqlSessionFactoryBean;
//import org.mybatis.spring.mapper.MapperScannerConfigurer;
//import javax.sql.DataSource;
//
//@Slf4j
//@Configuration
//@PropertySource("classpath:properties/jdbc.properties")
//public class DataSourceConfig {
//
//    @Value("${spring.jdbc.driverClassName}")
//    private String driverClassName;
//
//    @Value("${spring.jdbc.url}")
//    private String url;
//
//    @Value("${spring.jdbc.username}")
//    private String username;
//
//    @Value("${spring.jdbc.password}")
//    private String password;
//
//    @Bean
//    public DriverManagerDataSource dataSource() {
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        log.info("初始化dataSource参数: " + driverClassName + "\t" + url);
//        dataSource.setDriverClassName(driverClassName);
//        dataSource.setUrl(url);
//        dataSource.setUsername(username);
//        dataSource.setPassword(password);
//        return dataSource;
//    }
//
//    @Bean
//    public DataSourceTransactionManager transactionManager(DataSource dataSource) {
//        return new DataSourceTransactionManager(dataSource);
//    }
//
//    @Bean
//    public SqlSessionFactoryBean sqlSessionFactory(DataSource dataSource) throws Exception {
//        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
//        sessionFactory.setDataSource(dataSource);
//        sessionFactory.setConfigLocation(new PathMatchingResourcePatternResolver().getResource("classpath:mybatis-configuration.xml"));
//        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml"));
//        return sessionFactory;
//    }
//
//    @Bean
//    public MapperScannerConfigurer mapperScannerConfigurer() {
//        MapperScannerConfigurer mScannerConfigurer = new MapperScannerConfigurer();
//        mScannerConfigurer.setSqlSessionFactoryBeanName("sqlSessionFactory");
//        mScannerConfigurer.setBasePackage("com.cooler.ai.platform.dao");
//        return mScannerConfigurer;
//    }
//}