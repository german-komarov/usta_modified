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

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/")
public class MainController {
    @Autowired
    private PersonService personService;


    @GetMapping
    public String mainPage(@RequestParam(defaultValue = "multiple",name = "category") String category, @PageableDefault (size=20,sort={"id"},direction = Sort.Direction.ASC) Pageable pageable, Model model)
    {
        Page<Person> people=personService.getPeopleByCategory(category,pageable);
        model.addAttribute("num",people.getTotalElements());
        return "main";

    }

    @GetMapping("/fill")
    @ResponseBody
    public String fghj()
    {
        for(int i=0;i<100;i++)
        {
            Person person=new Person();
            person.setCategory("multiple");
            person.setName("German" +i);
            person.setRoles(Collections.singleton(new Role(1L,"ROLE_USER")));
            personService.savePerson(person);
        }
        return "OK";
    }
}
