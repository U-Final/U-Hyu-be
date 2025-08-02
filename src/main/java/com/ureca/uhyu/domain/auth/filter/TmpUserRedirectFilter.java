package com.ureca.uhyu.domain.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Slf4j
@Component
public class TmpUserRedirectFilter extends OncePerRequestFilter {

    private static final Set<String> ALLOWED_PATHS = Set.of(
            "/user/check-email",
            "/user/onboarding"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() &&
                hasRole(authentication, "ROLE_TMP_USER")) {

            String uri = request.getRequestURI();
            if (!isAllowedPath(uri)) {
                log.debug("TMP_USER 리다이렉트: {} -> /user/extra-info", uri);
                response.sendRedirect("/user/extra-info");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean hasRole(Authentication auth, String role) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(role));
    }

    private boolean isAllowedPath(String uri) {
        return ALLOWED_PATHS.contains(uri) || uri.startsWith("/static/");
    }
}
