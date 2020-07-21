package com.usta.usta.services;

import com.usta.usta.entities.Person;
import com.usta.usta.entities.Role;
import com.usta.usta.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.UriComponentsBuilderMethodArgumentResolver;

import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(rollbackFor = Exception.class)
public class PersonService implements UserDetailsService {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private RegistrationMailSender mailSender;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Person person=personRepository.findByUsername(username);
        if(person==null)
        {
            throw new UsernameNotFoundException("User not found");
        }

        return person;
    }

    public Page<Person> getAllMasters(Pageable pageable)
    {
        return personRepository.findAll(pageable);
    }

    public Person getPersonById(Long id)
    {
        return personRepository.findById(id).orElse(null);
    }
    public Person getPersonByUsername(String username)
    {
        return personRepository.findByUsername(username);
    }

    public Person getPersonByEmail(String email)
    {
        return personRepository.findByEmail(email);
    }

    public Person getPersonByActivationCode(String activationCode)
    {
        return personRepository.findByActivationCode(activationCode);
    }

    public void savePerson(Person person)
    {
        personRepository.save(person);
    }

    public void deletePersonById(Long id)
    {
        personRepository.deleteById(id);
    }



    public String register(Person person, MultipartFile avatarFile) throws IOException {


        if(this.getPersonByUsername(person.getUsername())!=null)
        {
            return "Username is taken";
        }

        if(this.getPersonByEmail(person.getEmail())!=null)
        {
            return "Email is taken";
        }

        if(!person.getPassword().equals(person.getPasswordConfirm()))
        {
            return "Passwords are not equal";
        }

        person.setActivated(false);
        person.setAvatar(Base64.getEncoder().encodeToString(avatarFile.getBytes()));
        person.setActivationCode(UUID.randomUUID().toString());
        person.setRoles(Collections.singleton(new Role(1L,"ROLE_USER")));
        person.setPassword(bCryptPasswordEncoder.encode(person.getPassword()));
        this.savePerson(person);
        String message= String.format("Hello %s.\n\nWelcome to usta.az. " +
                        "Go to this reference to activate your account https://usta.az/registration/activate/%s\n\nIf you didn't try to register, please just ignore this message",
                person.getUsername(),person.getActivationCode());
        mailSender.send(person.getEmail(),"Registration",message);

        return "OK";

    }

    public String activate(String activationCode)
    {
        Person person=this.getPersonByActivationCode(activationCode);
        if(person==null)
        {
            return "FAILED";
        }

        person.setActivationCode("");
        person.setActivated(true);
        this.savePerson(person);
        return "OK";
    }

    public Page<Person> getPeopleByCategory(String category, Pageable pageable)
    {
        return personRepository.findByCategory(category,pageable);
    }

    public String sendRestoringMail(String email,String purposeOfTheEmailMessage,String uri) {
        Person person=this.getPersonByEmail(email);
        if(person==null)
        {
            return "There is no account with this email";
        }

        person.setActivationCode(UUID.randomUUID().toString());
        this.savePerson(person);
        String message= String.format("Hello %s.\n\nWelcome to usta.az. " +
                        "Go to this reference to %s https://usta.az%s%s\n\nIf you didn't try to register, please just ignore this message",
                person.getUsername(),purposeOfTheEmailMessage,uri,person.getActivationCode());
        mailSender.send(person.getEmail(),"Registration",message);

        return "OK";
    }

    public String checkActivationCodeForRestoringPassword(String activationCode) {

        Person person=this.getPersonByActivationCode(activationCode);
        if(person==null)
        {
            return "DENIED";
        }

        return "OK";


    }

    public String restorePassword(String activationCode,String password, String passwordConfirm) {
        if(!password.equals(passwordConfirm))
        {
            return "Passwords are different";
        }

        Person person=this.getPersonByActivationCode(activationCode);
        person.setActivationCode("");
        person.setPassword(bCryptPasswordEncoder.encode(password));
        this.savePerson(person);
        return "OK";

    }

    public void changeAvatar(Person principal, MultipartFile avatar) throws IOException {
        Person person=this.getPersonById(principal.getId());
        person.setAvatar(Base64.getEncoder().encodeToString(avatar.getBytes()));
        this.savePerson(person);
    }
}
