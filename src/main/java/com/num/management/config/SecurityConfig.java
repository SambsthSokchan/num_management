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

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/signup", "/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                        .requestMatchers("/teachers/**").hasRole("ADMIN")
                        .requestMatchers("/students/**").hasAnyRole("ADMIN", "TEACHER")
                        .requestMatchers("/attendance").hasAnyRole("ADMIN", "TEACHER", "STUDENT")
                        .requestMatchers("/attendance/new", "/attendance/edit/**", "/attendance/delete/**")
                        .hasAnyRole("ADMIN", "TEACHER")
                        .requestMatchers("/subjects/new", "/subjects/edit/**", "/subjects/delete/**").hasRole("ADMIN")
                        .requestMatchers("/classes/new", "/classes/save", "/classes/edit/**", "/classes/update/**",
                                "/classes/delete/**")
                        .hasRole("ADMIN")
                        .requestMatchers("/classes/**").hasAnyRole("ADMIN", "TEACHER", "STUDENT")
                        .anyRequest().authenticated())
                .formLogin(
                        form -> form
                                .loginPage("/login")
                                .loginProcessingUrl("/login")
                                .defaultSuccessUrl("/", true)
                                .permitAll())
                .logout(
                        logout -> logout
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                .permitAll());
        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }
}
