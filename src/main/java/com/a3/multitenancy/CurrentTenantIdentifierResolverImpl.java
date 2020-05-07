package com.a3.multitenancy;

import com.a3.util.TenantContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

/**
 * O Hibernate precisa saber qual banco de dados usar, ou seja, qual inquilino se conectar,
 * esta classe fornece um mecanismo para fornecer a fonte de dados correta em tempo de execução
 */
@Component
public class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver {

    private static final String DEFAULT_TENANT_ID = "tenant_1";

    /*
     * (non-Javadoc)
     *
     * @see org.hibernate.context.spi.CurrentTenantIdentifierResolver#
     * resolveCurrentTenantIdentifier()
     */
    @Override
    public String resolveCurrentTenantIdentifier() {
        // O inquilino é armazenado em um ThreadLocal antes das informações da request
        // serem enviadas para a requisição solicitada.
        String tenant = TenantContextHolder.getTenant();
        return StringUtils.isNotBlank(tenant) ? tenant : DEFAULT_TENANT_ID;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.hibernate.context.spi.CurrentTenantIdentifierResolver#
     * validateExistingCurrentSessions()
     */
    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }

}
