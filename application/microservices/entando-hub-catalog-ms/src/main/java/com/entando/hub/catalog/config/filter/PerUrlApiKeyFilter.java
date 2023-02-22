package com.entando.hub.catalog.config.filter;

import com.entando.hub.catalog.service.CategoryService;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;


//@Component
public class PerUrlApiKeyFilter extends OncePerRequestFilter {

    // change this with the proper service
    private CategoryService service;

    public PerUrlApiKeyFilter(CategoryService service) {
        this.service = service;
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws IOException, ServletException {

        System.out.println("DENTRO");

        String apiKey = request.getHeader("entando-api-key");

        // change this to check the real permission
        if (service.getCategory(apiKey).isPresent()) {
            filterChain.doFilter(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You are trying to access a private catalog without the required permissions");
        }
    }
}
