package com.usta.usta.conrollers;

import com.usta.usta.entities.Person;
import com.usta.usta.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/registration")
public class RegistrationController {

    @Autowired
    private PersonService personService;

    @GetMapping
    public String doGet()
    {
        return "registration";
    }

    @PostMapping
    public String doPost(@ModelAttribute Person person, MultipartFile avatarFile, Model model) throws IOException {

        String response=personService.register(person,avatarFile);
        if(response.equals("OK"))
        {
            return "email_was_sent";
        }
        else
        {
            model.addAttribute("error",response);
            return "registration";
        }

    }

    @GetMapping("/activate/{activationCode}")
    public String doGetActivate(@PathVariable String activationCode,Model model)
    {
        if(!personService.activate(activationCode).equals("OK"))
        {
            return "denied";
        }
        model.addAttribute("registered in USTA.AZ");
        return "success";

    }

    @GetMapping("/send/mail/again")
    private String doGetSendMailAgain()
    {
        return "email_form";
    }

    @PostMapping("/send/mail/again")
    public String doPostSendMailAgain(@RequestParam String email, Model model)
    {
        String response=personService.sendRestoringMail(email,"activate your account","/registration/activate/");
        if(!response.equals("OK"))
        {
            model.addAttribute("error",response);
            return "activation_email";
        }

        model.addAttribute("email",email);
        return "email_was_sent";
    }




}
