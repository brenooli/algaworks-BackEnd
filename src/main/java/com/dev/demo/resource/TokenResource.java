// package com.dev.demo.resource;

// import javax.servlet.http.Cookie;
// import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpServletResponse;

// import org.springframework.http.HttpStatus;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// @RestController
// @RequestMapping("/tokens")
// public class TokenResource {

//     @DeleteMapping("/revoke")
//     public void revoke(HttpServletRequest req, HttpServletResponse resp) {

//         Cookie cookie = new Cookie("refreshToken", null);
//         cookie.setHttpOnly(true);
//         cookie.setSecure(false); //Mudar para true em produção
//         cookie.setPath(req.getContextPath() + "/usuarios/token/refresh");
//         cookie.setMaxAge(0);

//         resp.addCookie(cookie);
//         resp.setStatus(HttpStatus.NO_CONTENT.value());
//     }

// }
