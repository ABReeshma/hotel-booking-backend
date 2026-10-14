
package com.traveleasy.hotel_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(
            PasswordEncoder passwordEncoder) {

        UserDetails traveller = User.builder()
                .username("traveller")
                .password(passwordEncoder.encode("Traveller@123"))
                .roles("TRAVELLER")
                .build();

        UserDetails partner = User.builder()
                .username("partner")
                .password(passwordEncoder.encode("Partner@123"))
                .roles("HOTEL_PARTNER")
                .build();

        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("Admin@123"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(
                traveller, partner, admin);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {


                http
    .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                        // Hotel read and search operations
                        .requestMatchers(HttpMethod.GET, "/api/hotels/search")
                        .hasAnyRole("TRAVELLER", "HOTEL_PARTNER", "ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/hotels/**")
                        .hasAnyRole("TRAVELLER", "HOTEL_PARTNER", "ADMIN")

                        // Hotel management
                        .requestMatchers(HttpMethod.POST, "/api/hotels")
                        .hasAnyRole("HOTEL_PARTNER", "ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/api/hotels/**")
                        .hasAnyRole("HOTEL_PARTNER", "ADMIN")

                        .requestMatchers(HttpMethod.DELETE, "/api/hotels/**")
                        .hasRole("ADMIN")

                        // Room read operations
                        .requestMatchers(HttpMethod.GET, "/api/rooms/**")
                        .hasAnyRole("TRAVELLER", "HOTEL_PARTNER", "ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/hotels/*/rooms/**")
                        .hasAnyRole("TRAVELLER", "HOTEL_PARTNER", "ADMIN")

                        // Room management
                        .requestMatchers(HttpMethod.POST, "/api/hotels/*/rooms")
                        .hasAnyRole("HOTEL_PARTNER", "ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/api/rooms/**")
                        .hasAnyRole("HOTEL_PARTNER", "ADMIN")

                        .requestMatchers(HttpMethod.DELETE, "/api/rooms/**")
                        .hasAnyRole("HOTEL_PARTNER", "ADMIN")

                        // Booking operations
                        .requestMatchers(HttpMethod.POST, "/api/bookings/**")
                        .hasRole("TRAVELLER")

                        .requestMatchers(HttpMethod.GET, "/api/bookings/**")
                        .hasAnyRole("TRAVELLER", "HOTEL_PARTNER", "ADMIN")

                        .requestMatchers(HttpMethod.PATCH, "/api/bookings/**")
                        .hasRole("TRAVELLER")

                        .requestMatchers(HttpMethod.PATCH, "/api/rooms/*/status")
                        .hasAnyRole("HOTEL_PARTNER", "ADMIN")
                        // Reject all other unmatched requests
                        .anyRequest().denyAll()
                )
                .httpBasic(Customizer.withDefaults())
                .formLogin(form -> form.disable());

        return http.build();
    }
}
