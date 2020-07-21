package com.usta.usta.conrollers;

import com.usta.usta.entities.Person;
import com.usta.usta.entities.Role;
import com.usta.usta.services.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/")
public class MainController {
    @Autowired
    private PersonService personService;


    @GetMapping
    public String mainPage(@RequestParam(defaultValue = "multiple",name = "category") String category, @PageableDefault (size=21,sort={"id"},direction = Sort.Direction.ASC) Pageable pageable, Model model)
    {
        Page<Person> masters;
        if(category.equals("multiple")) {
             masters=personService.getAllMasters(pageable);
        }
        else {
             masters = personService.getPeopleByCategory(category, pageable);
        }
        List<Person> listMain=new ArrayList<>(masters.getContent());
        Collections.shuffle(listMain);
        List<Person> listRight,listMiddle,listLeft;
        listLeft=listMain.subList(listMain.size()*2/3,listMain.size());
        listMiddle=listMain.subList(listMain.size()/3,listMain.size()*2/3);
        listRight=listMain.subList(0,listMain.size()/3);
        model.addAttribute("listLeft",listLeft);
        model.addAttribute("listMiddle",listMiddle);
        model.addAttribute("listRight",listRight);
        return "main";
    }

    @GetMapping("/details/{id}")
    public String masterDetails(@PathVariable Long id,Model model)
    {
        Person master=personService.getPersonById(id);
        model.addAttribute("master",master);
        return "details";

    }


    @GetMapping("/main")
    @ResponseBody
    public String hello(@AuthenticationPrincipal Person person)
    {
        return "Hello "+person.getUsername();
    }

    @PostMapping("/main/change/avatar")
    @ResponseBody
    public String doChangeAvatar(@AuthenticationPrincipal Person principal,@RequestParam MultipartFile avatar) throws IOException {
        personService.changeAvatar(principal,avatar);
        return "OK";
    }

    @GetMapping("/main/change/information")
    public String doGetChangeInformation(@AuthenticationPrincipal Person person,Model model)
    {
        model.addAttribute("person",person);
        return "edit_page";
    }

    @PostMapping("/main/change/information")
    public String doPostChangeInformation(@RequestParam Long id,
                                          @RequestParam String name,
                                          @RequestParam String surname,
                                          @RequestParam String description,
                                          @RequestParam String city,
                                          @RequestParam String phoneNumber)
    {
        Person person=personService.getPersonById(id);
        person.setName(name);
        person.setSurname(surname);
        person.setDescription(description);
        person.setCity(city);
        person.setPhoneNumber(phoneNumber);
        personService.savePerson(person);
        return "redirect:/main";

    }




}
