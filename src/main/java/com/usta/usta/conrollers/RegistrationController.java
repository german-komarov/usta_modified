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
    @ResponseBody
    public String doActivate(@PathVariable String activationCode)
    {
        return personService.activate(activationCode);


    }
}
