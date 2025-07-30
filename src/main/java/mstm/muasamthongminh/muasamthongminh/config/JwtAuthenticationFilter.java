package mstm.muasamthongminh.muasamthongminh.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    public JwtAuthenticationFilter(JwtUtils jwtUtils, UserDetailsService userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();
        logger.debug("JwtFilter → Request to '{}'", path);

        // Bỏ qua auth endpoints
        if (path.startsWith("/api/auth")) {
            logger.debug("JwtFilter → Skipping auth path");
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = null;

        // Ưu tiên lấy từ header
        final String authHeader = request.getHeader("Authorization");
        logger.debug("JwtFilter → Authorization header = {}", authHeader);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
        } else {
            // Nếu không có header, lấy từ cookie
            if (request.getCookies() != null) {
                for (var cookie : request.getCookies()) {
                    if ("token".equals(cookie.getName())) {
                        jwt = cookie.getValue();
                        logger.debug("JwtFilter → Token found in cookie: {}", jwt);
                        break;
                    }
                }
            }
        }

        // Nếu có JWT
        if (jwt != null) {
            try {
                if (jwtUtils.validateToken(jwt)) {
                    String email = jwtUtils.getEmailFromToken(jwt);
                    logger.debug("JwtFilter → Valid token for email {}", email);

                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    logger.debug("JwtFilter → Authentication set for {}", email);
                } else {
                    logger.warn("JwtFilter → Invalid JWT");
                }
            } catch (Exception ex) {
                logger.warn("JwtFilter → Error parsing token", ex);
            }
        } else {
            logger.debug("JwtFilter → No token found in header or cookie");
        }

        filterChain.doFilter(request, response);
    }

}
