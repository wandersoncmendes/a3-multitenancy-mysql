/**
 * Copyright 2018 onwards - Sunit Katkar (sunitkatkar@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.a3.filters;

import com.a3.util.TenantContextHolder;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Este é o filtro que é chamado uma vez por request.
   * Esse filtro extrai do header o nome do inquilino.
   * Esse valor é usado para criar a instancia usada para acessar o banco de dados
 */
@Configuration
public class CustomFilter extends OncePerRequestFilter {

    public static final String SPRING_SECURITY_FORM_TENANT_NAME_KEY = "tenant";

    /**
     * @param request
     * @return
     */
    private String obtainTenant(HttpServletRequest request) {
        return request.getHeader(SPRING_SECURITY_FORM_TENANT_NAME_KEY);
    }

    /**
     * Este método busca o tenant da requisição e salva o mesmo no contexto da thread
     * @param httpServletRequest
     * @param httpServletResponse
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String tenant = obtainTenant(httpServletRequest);
        TenantContextHolder.setTenantId(tenant);
        doFilter(httpServletRequest, httpServletResponse, filterChain);
    }
}
