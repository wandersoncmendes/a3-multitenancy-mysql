package com.a3.multitenancy;

import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Map;

/**
 * Esta classe seleciona o banco de dados correto com base no inquilino
 * ID encontrado pelo {@link CurrentTenantIdentifierResolverImpl}
 */
@Component
public class DataSourceBasedMultiTenantConnectionProviderImpl
        extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl {

    private static final long serialVersionUID = 1L;

    @Autowired
    private Map<String, DataSource> dataSourcesMtApp;

    /*
     * (non-Javadoc)
     * 
     * @see org.hibernate.engine.jdbc.connections.spi.
     * AbstractDataSourceBasedMultiTenantConnectionProviderImpl#selectAnyDataSource(
     * )
     */
    @Override
    protected DataSource selectAnyDataSource() {
        return this.dataSourcesMtApp.values().iterator().next();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.hibernate.engine.jdbc.connections.spi.
     * AbstractDataSourceBasedMultiTenantConnectionProviderImpl#selectDataSource(
     * java.lang.String)
     */
    @Override
    protected DataSource selectDataSource(String tenantIdentifier) {
        return this.dataSourcesMtApp.get(tenantIdentifier);
    }
}
