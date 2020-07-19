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
            return "activate_account";
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
        personService.activate(activationCode);
        model.addAttribute("registered in USTA.AZ");
        return "successfully";

    }

    @GetMapping("/send/mail/again")
    private String doGetSendMailAgain()
    {
        return "email_form";
    }

    @PostMapping("/send/mail/again")
    public String doPostSendMailAgain(@RequestParam String email, Model model)
    {
        String response=personService.sendRestoringMail(email,"activate your account");
        if(!response.equals("OK"))
        {
            model.addAttribute("error",response);
            return "email_form";
        }

        model.addAttribute("email",email);
        return "email_was_sent";
    }




}
