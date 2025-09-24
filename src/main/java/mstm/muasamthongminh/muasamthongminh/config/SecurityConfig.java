package mstm.muasamthongminh.muasamthongminh.config;

import mstm.muasamthongminh.muasamthongminh.modules.auth.model.User;
import mstm.muasamthongminh.muasamthongminh.modules.auth.repository.AuthUserRepository;
import mstm.muasamthongminh.muasamthongminh.modules.auth.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final AuthUserRepository authUserRepository;

    @Autowired
    public SecurityConfig(@Lazy JwtAuthenticationFilter jwtFilter, AuthUserRepository authUserRepository) {
        this.jwtFilter = jwtFilter;
        this.authUserRepository = authUserRepository;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return email -> {
            User user = authUserRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            return new CustomUserDetails(user);
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService())
                .passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {
                })
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("api/auth/update-email").authenticated()
                        .requestMatchers("/api/addresses/**").authenticated()

                        .requestMatchers("/api/shop-requests/detail").authenticated()
                        .requestMatchers("/api/shop-requests/").hasRole("ADMIN")
                        .requestMatchers("api/shop-requests/my-shop").authenticated()
                        .requestMatchers("/api/shop-requests/create").authenticated()
                        .requestMatchers("/api/shop-requests/pending/").hasRole("ADMIN")
                        .requestMatchers("api/shop-requests/pending/approve-all").hasRole("ADMIN")
                        .requestMatchers("/api/shop-requests/\\\\d+/approved").hasRole("ADMIN")
                        .requestMatchers("/api/shop-requests/\\\\d+/rejected").hasRole("ADMIN")
                        .requestMatchers("/api/shop/").hasRole("ADMIN")
                        .requestMatchers("api/shop/create").authenticated()
                        .requestMatchers("api/shop/**").authenticated()

                        .requestMatchers("/api/bank-account/").hasRole("ADMIN")

                        .requestMatchers("/api/card/").authenticated()

                        .requestMatchers("/api/bank-account/create").authenticated()
                        .requestMatchers("/api/bank-account/user").authenticated()
                        .requestMatchers("/api/bank-account/shop/").authenticated()
                        .requestMatchers("/api/bank-account/\\\\d+/create-shop").authenticated()

                        .requestMatchers("/api/user/**").authenticated()
                        .requestMatchers("/api/user/user/").hasRole("ADMIN")
                        .requestMatchers("/api/user/\\\\d+/update-role").hasRole("ADMIN")
                        .requestMatchers("/api/user/\\\\d+/update-details").hasRole("ADMIN")
                        .requestMatchers("/api/user/\\\\d+/delete-user").hasRole("ADMIN")
                        .requestMatchers("/api/user/admin/**").hasRole("ADMIN")

                        .requestMatchers("api/categories/**").permitAll()
                        .requestMatchers("api/categories/search").authenticated()
                        .requestMatchers("/api/categories/create").hasAnyRole("ADMIN", "SELLER")
                        .requestMatchers("api/categories/update/\\\\d+").hasAnyRole("ADMIN", "SELLER")
                        .requestMatchers("api/categories/\\\\d+").hasAnyRole("ADMIN", "SELLER")

                        .requestMatchers("api/brands/**").permitAll()
                        .requestMatchers("api/brands/created").hasAnyRole("ADMIN", "SELLER")
                        .requestMatchers("/api/new/**").hasAnyRole("ADMIN", "SELLER")

                        .requestMatchers("/api/products/public/**").permitAll()
                        .requestMatchers("/api/products/**").authenticated()

                        .requestMatchers("/api/orders/all").hasRole("ADMIN")

                        .requestMatchers("/api/shippings/**").hasRole("ADMIN")

                        .requestMatchers("api/reports/system/**").hasRole("ADMIN")
                        .requestMatchers("api/reports/export/**").hasAnyRole("ADMIN", "SELLER")

                        .anyRequest().authenticated()
                )
                .sessionManagement(sm -> sm
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
