// package com.dev.demo.config;

// import java.util.Collection;
// import java.util.Collections;
// import java.util.List;
// import java.util.stream.Collectors;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
// import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
// import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
// import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.core.GrantedAuthority;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
// import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;
// import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
// import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

// @Configuration
// @EnableWebSecurity
// @EnableGlobalMethodSecurity(prePostEnabled = true)
// public class ResourceServerConfig extends WebSecurityConfigurerAdapter {

//     @Autowired
//     private UserDetailsService userDetailsService;

//     @Override
//     public void configure(HttpSecurity http) throws Exception {

//         http.authorizeRequests()
//         .antMatchers("/categorias")
//         .permitAll()
//         .anyRequest()
//         .authenticated()
//         .and()
//         .sessionManagement()
//         .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//         .and().csrf().disable()
//         .cors().and()
//         .oauth2ResourceServer().jwt()
//         .jwtAuthenticationConverter(jwtAuthenticationConverter());
//     }

//     @Autowired
//     protected void configure(AuthenticationManagerBuilder auth) throws Exception {

//         auth.userDetailsService(userDetailsService).passwordEncoder((passwordEncoder()));
//     }


//     @Override
//     public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
//         resources.stateless(true);
//     }

//     @Bean
//     public PasswordEncoder passwordEncoder() {
//         return new BCryptPasswordEncoder();
//     }

//     @Bean
//     public MethodSecurityExpressionHandler createExpressionHandler(){

//         return new OAuth2MethodSecurityExpressionHandler();
//     }

    
// 	private  jwtAuthenticationConverter() {
// 		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
// 		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
// 			List<String> authorities = jwt.getClaimAsStringList("authorities");

// 			if (authorities == null) {
// 				authorities = Collections.emptyList();
// 			}

// 			JwtGrantedAuthoritiesConverter scopesAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
// 			Collection<GrantedAuthority> grantedAuthorities = scopesAuthoritiesConverter.convert(jwt);

// 			grantedAuthorities.addAll(authorities.stream()
// 					.map(SimpleGrantedAuthority::new)
// 					.collect(Collectors.toList()));

// 			return grantedAuthorities;
// 		});

// 		return jwtAuthenticationConverter;
// 	}
// }
