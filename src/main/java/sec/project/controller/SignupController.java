package sec.project.controller;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sec.project.domain.Signup;
import sec.project.repository.SignupRepository;
import java.io.PrintWriter;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

@Controller
public class SignupController {
    private String name;
    
    public SignupController() {
        this.name = "Pamsu";
    }

    @Autowired
    private SignupRepository signupRepository;

    @RequestMapping("*")
    public String defaultMapping() {
        return "redirect:/form";
    }

    @RequestMapping(value = "/form", method = RequestMethod.GET)
    public String loadForm() {
        return "form";
    }   
  
    //this method is "A 10 Unvalidated redirects and forwards."
    //this is done in an unsecure way and can be changed by hackers
    @RequestMapping(value = "/twitter", method = RequestMethod.GET)
    public void redirectToTwitter(HttpServletResponse httpServletResponse) throws IOException {
        httpServletResponse.sendRedirect("https://twitter.com");
    }
    
    @RequestMapping(value = "/form", method = RequestMethod.POST)
    public String submitForm(Model model, @RequestParam String name, @RequestParam String address) {
        signupRepository.save(new Signup(name, address));
        //A9-Using Components with Known Vulnerabilities. Abuse of the Expression Language implementation 
        //in Spring allowed attackers to execute arbitrary code, effectively taking over the server.
        ExpressionParser parser = new SpelExpressionParser();
        Expression exp = parser.parseExpression("new String('" + name + "').toUpperCase()");
        String username = exp.getValue(String.class);
        model.addAttribute("name", username);
        /*"A4-Insecure Direct Object References" 
        In real life we would create a real class which writes files
        and stores those on server. User can now see the number of the ticket
        and just change it to download someone else's ticket. 
        One way to fix it is to create name with GUID. */
        String downloadDir = System.getProperty("user.dir") + "\\src\\test\\resources\\tiket-12345.txt";
        model.addAttribute("link", downloadDir);
        return "done";
    }
}
