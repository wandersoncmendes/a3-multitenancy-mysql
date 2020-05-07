package com.a3.util;

/**
 * Quando o usuário final envia uma requisição, é necessário o ID do inquilino para
 * determinar qual banco de dados se conectar. Isso precisa ser capturado no
 * filtro da requisição, especificamente no
 * {@link org.springframework.web.filter.OncePerRequestFilter} implementado por
 * {@link com.a3.filters.CustomFilter}. Esse ID de inquilino é exigido pelo
 * {@link org.hibernate.context.spi.CurrentTenantIdentifierResolver} implementado pelo
 * {@link com.a3.multitenancy.CurrentTenantIdentifierResolverImpl}
 *
 * <br/>
 * <br/>
 * <b> Explicação: </b> O Thread Local pode ser considerado como um escopo de acesso, como
 * um escopo de solicitação ou escopo de sessão. É um escopo de thread. Você pode definir qualquer objeto
 * no Thread Local e esse objeto será global e local para o específico
 * thread que está acessando este objeto. Global e local ao mesmo tempo? :
 *
 * <ul>
 * <li> Os valores armazenados no Thread Local são globais para o thread, o que significa que eles
 * pode ser acessado de qualquer lugar dentro desse segmento. Se um encadeamento chamar métodos
 * de várias classes, todos os métodos podem ver a variável Thread Local
 * definido por outros métodos (porque eles estão executando no mesmo thread). O valor que
 * não precisa ser passado explicitamente. É como você usa variáveis ​​globais. </li>
 * <li> Os valores armazenados no Thread Local são locais para o thread, o que significa que cada
 * thread terá sua própria variável Thread Local. Um segmento não pode
 * acessar / modificar as variáveis ​​locais de threads de outros threads. </li>
 * </ul>
 */
public class TenantContextHolder {

    private static final ThreadLocal<String> CONTEXT = new ThreadLocal<>();

    public static void setTenantId(String tenant) {
        CONTEXT.set(tenant);
    }

    public static String getTenant() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}