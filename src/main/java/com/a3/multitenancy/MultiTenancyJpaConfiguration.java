package com.a3.multitenancy;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hibernate.MultiTenancyStrategy;
import org.hibernate.cfg.Environment;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.a3.model.Produto;
import com.a3.multitenancy.MultitenancyProperties.DataSourceProperties;

/**
 * Esta classe define as fontes de dados a serem usadas para acessar os diferentes
 * bancos de dados (um banco de dados por inquilino). Ele gera a sessão do Hibernate e
 * um bean de entidade para acesso ao banco de dados via Spring JPA, bem como o Transaction
 * proprio a ser usado.
 */
@Configuration
@EnableConfigurationProperties({ MultitenancyProperties.class, JpaProperties.class })
@EnableTransactionManagement
public class MultiTenancyJpaConfiguration {

    @Autowired
    private JpaProperties jpaProperties;

    @Autowired
    private MultitenancyProperties multitenancyProperties;

    /**
     * Constrói um mapa de todas as fontes de dados definidas no arquivo application.yml
     * caso desejar ue sua fonte para os bancos seja um banco master, aqui deve ficar a
     * busca no banco master
     * 
     * @return
     */
    @Primary
    @Bean(name = "dataSourcesMtApp")
    public Map<String, DataSource> dataSourcesMtApp() {
        Map<String, DataSource> result = new HashMap<>();
        for (DataSourceProperties dsProperties : this.multitenancyProperties.getDataSources()) {

            DataSourceBuilder factory = DataSourceBuilder.create().url(dsProperties.getUrl())
                    .username(dsProperties.getUsername()).password(dsProperties.getPassword())
                    .driverClassName(dsProperties.getDriverClassName());

            result.put(dsProperties.getTenantId(), factory.build());
        }
        return result;
    }

    /**
     * Conecta automaticamente as fontes de dados para que possam ser usadas pelo Spring JPA para
     * acessar o banco de dados
     * 
     * @return
     */
    @Bean
    public MultiTenantConnectionProvider multiTenantConnectionProvider() {
        // Autowires dataSourcesMtApp
        return new DataSourceBasedMultiTenantConnectionProviderImpl();
    }

    /**
     * Por se tratar de um aplicativo multilocatário, o Hibernate exige que o atual
     * identificador de inquilino resolvido com o uso do
     * {@link org.hibernate.context.spi.CurrentSessionContext} e
     * {@link org.hibernate.SessionFactory#getCurrentSession()}
     * 
     * @return
     */
    @Bean
    public CurrentTenantIdentifierResolver currentTenantIdentifierResolver() {
        return new CurrentTenantIdentifierResolverImpl();
    }

    /**
     * org.springframework.beans.factory.FactoryBean que cria uma JPA
     * {@link javax.persistence.EntityManagerFactory} de acordo com o padrão da JPA
     * contrato de inicialização do contêiner. Essa é a maneira mais poderosa de configurar um
     * JPA EntityManagerFactory compartilhado em um contexto de aplicativo Spring; a
     * EntityManagerFactory pode ser passado para DAOs baseados em JPA via
     * injeção de dependência. Observe que a mudança para uma consulta JNDI ou para um
     * {@link org.springframework.orm.jpa.LocalEntityManagerFactoryBean} definição
     * é apenas uma questão de configuração!
     * 
     * @param multiTenantConnectionProvider
     * @param currentTenantIdentifierResolver
     * @return
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(
            MultiTenantConnectionProvider multiTenantConnectionProvider,
            CurrentTenantIdentifierResolver currentTenantIdentifierResolver) {

        Map<String, Object> hibernateProps = new LinkedHashMap<>();
        hibernateProps.putAll(this.jpaProperties.getProperties());
        hibernateProps.put("hibernate.hbm2ddl.auto", "none");
        hibernateProps.put(Environment.MULTI_TENANT, MultiTenancyStrategy.DATABASE);
        hibernateProps.put(Environment.MULTI_TENANT_CONNECTION_PROVIDER, multiTenantConnectionProvider);
        hibernateProps.put(Environment.MULTI_TENANT_IDENTIFIER_RESOLVER, currentTenantIdentifierResolver);

        // Nenhum dataSource está definido como resultante entityManagerFactoryBean
        LocalContainerEntityManagerFactoryBean result = new LocalContainerEntityManagerFactoryBean();
        result.setPackagesToScan(new String[] { Produto.class.getPackage().getName() });
        result.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        result.setJpaPropertyMap(hibernateProps);

        return result;
    }

    /**
     * Interface usada para interagir com a fábrica do gerenciador de entidades para o
     * unidade de persistência.
     * 
     * @param entityManagerFactoryBean
     * @return
     */
    @Bean
    public EntityManagerFactory entityManagerFactory(LocalContainerEntityManagerFactoryBean entityManagerFactoryBean) {
        return entityManagerFactoryBean.getObject();
    }

    /**
     * Cria um novo
     * {@link org.springframework.orm.jpa.JpaTransactionManager#JpaTransactionManager(EntityManagerFactory emf)}
     * instance.
     * 
     * {@link org.springframework.transaction.PlatformTransactionManager} é a
     * interface central na infraestrutura de transações do Spring. Os aplicativos podem
     * usar isso diretamente, mas não se destina principalmente a API: normalmente,
     * os aplicativos funcionarão com TransactionTemplate ou declarative
     * demarcação de transação através de AOP.
     * @param entityManagerFactory
     * @return
     */
    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}