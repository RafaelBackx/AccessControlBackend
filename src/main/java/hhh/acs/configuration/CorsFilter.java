//package hhh.acs.configuration;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.*;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//@Component
//public class CorsFilter implements Filter {
//    private final Logger log = LoggerFactory.getLogger(CorsFilter.class);
//
//    public CorsFilter(){
//        log.info("Corsfilter init");
//    }
//
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//        log.info("Corsfilter init");
//    }
//
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//        HttpServletRequest request = (HttpServletRequest)servletRequest;
//        HttpServletResponse response = (HttpServletResponse)servletResponse;
//        response.setHeader("Access-Control-Allow-Origin","*");
//        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
//        response.setHeader("Access-Control-Allow-Methods", "*");
//        log.info(response.toString());
//    }
//
//    @Override
//    public void destroy() {
//
//    }
//}
