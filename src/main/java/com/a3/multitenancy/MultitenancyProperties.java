package com.a3.multitenancy;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Esta classe lê o nó <code> multitenancy.mtapp </code> de
 * <code> application.yml </code> e preenche uma lista de
 * {@link org.springframework.boot.autoconfigure.jdbc.DataSourceProperties}
 * objetos, com cada instância contendo os detalhes da fonte de dados sobre o
 * banco de dados como URL, nome de usuário, senha etc
 */
@Configuration
@ConfigurationProperties("multitenancy.mtapp")
public class MultitenancyProperties {

    private List<DataSourceProperties> dataSourcesProps;

    public List<DataSourceProperties> getDataSources() {
        return this.dataSourcesProps;
    }

    public void setDataSources(List<DataSourceProperties> dataSourcesProps) {
        this.dataSourcesProps = dataSourcesProps;
    }

    public static class DataSourceProperties extends org.springframework.boot.autoconfigure.jdbc.DataSourceProperties {

        private String tenantId;

        public String getTenantId() {
            return tenantId;
        }

        public void setTenantId(String tenantId) {
            this.tenantId = tenantId;
        }
    }
}
