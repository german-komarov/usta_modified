package com.usta.usta.conrollers;


import com.usta.usta.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/restore/password")
public class PasswordRestoringController {
    @Autowired
    private PersonService personService;

    @GetMapping
    public String doGet()
    {
        return "email_form";
    }

    @PostMapping
    public String doPost(@RequestParam String email, Model model)
    {
        String response=personService.sendRestoringMail(email,"restore your password");
        if(!response.equals("OK"))
        {
            model.addAttribute("error",response);
            return "email_form";
        }

        model.addAttribute("email",email);
        return "email_was_sent";
    }


    @GetMapping("/new/password/{activationCode}")
    public String doGetNewPassword(@PathVariable String activationCode,Model model)
    {
        String response=personService.checkActivationCodeForRestoringPassword(activationCode);
        if(!response.equals("OK"))
        {
            return "denied_page";
        }

        model.addAttribute("activationCode",activationCode);
        return "new_password";
    }

    @PostMapping("/new/password/{activationCode}")
    public String doPostNewPassword(@PathVariable String activationCode,@RequestParam String password, @RequestParam String passwordConfirm,Model model)
    {
        String response=personService.restorePassword(activationCode,password,passwordConfirm);

        if(!response.equals("OK"))
        {
            model.addAttribute("error",response);
            return "new_password";
        }
        model.addAttribute("message","restored your password");
        return "successfully";
    }



}
