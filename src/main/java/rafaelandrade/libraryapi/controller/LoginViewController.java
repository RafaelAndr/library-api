package rafaelandrade.libraryapi.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import rafaelandrade.libraryapi.security.CustomAuthentication;

@Controller
public class LoginViewController {

    @GetMapping("/login")
    public String paginaLogin(){
        return "login";
    }

    @GetMapping("/")
    @ResponseBody
    public String homePage(Authentication authentication){
        if(authentication instanceof CustomAuthentication customAuthentication) {
            System.out.println(customAuthentication.getName());
        }
        return "Olá " + authentication.getName() + authentication.getPrincipal();
    }
}