package com.num.management.config;

import com.num.management.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Autowired
        private CustomUserDetailsService userDetailsService;

        // Bean to encode passwords using BCrypt
        @Bean
        public static PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        // Configure security filter chain: URL permissions, login, and logout
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests((authorize) -> authorize
                                                // Allow access to static resources and public pages
                                                .requestMatchers("/login", "/signup", "/css/**", "/js/**", "/images/**",
                                                                "/webjars/**")
                                                .permitAll()
                                                // Restrict specific paths to roles (ADMIN, TEACHER, STUDENT)
                                                // Restrict specific paths to roles (ADMIN, TEACHER, STUDENT)
                                                .requestMatchers("/admins/**").hasRole("ADMIN")

                                                // Teachers Management determines who is a teacher -> Admin only
                                                .requestMatchers("/teachers/**").hasRole("ADMIN")

                                                // Student Management -> Admin and Teacher
                                                .requestMatchers("/students/**").hasAnyRole("ADMIN", "TEACHER")

                                                // Attendance
                                                .requestMatchers("/attendance/mark/**").hasAnyRole("TEACHER", "ADMIN")
                                                .requestMatchers("/attendance/**")
                                                .hasAnyRole("ADMIN", "TEACHER", "STUDENT")

                                                // Subjects -> Admin and Teacher (Teacher uploads materials)
                                                .requestMatchers("/subjects/**").hasAnyRole("ADMIN", "TEACHER")

                                                // Classes -> Admin manages, others view
                                                .requestMatchers("/classes/new", "/classes/save", "/classes/edit/**",
                                                                "/classes/update/**",
                                                                "/classes/delete/**")
                                                .hasRole("ADMIN")
                                                .requestMatchers("/classes/**")
                                                .hasAnyRole("ADMIN", "TEACHER", "STUDENT")

                                                .anyRequest().authenticated()) // All other requests require
                                                                               // authentication
                                .formLogin(
                                                form -> form
                                                                .loginPage("/login")
                                                                .loginProcessingUrl("/login")
                                                                .defaultSuccessUrl("/", true)
                                                                .permitAll())
                                .logout(
                                                logout -> logout
                                                                .logoutRequestMatcher(
                                                                                new AntPathRequestMatcher("/logout"))
                                                                .permitAll());
                return http.build();
        }

        // Bean to provide authentication logic using custom user details service
        @Bean
        public DaoAuthenticationProvider authenticationProvider() {
                DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
                auth.setUserDetailsService(userDetailsService);
                auth.setPasswordEncoder(passwordEncoder());
                return auth;
        }
}
