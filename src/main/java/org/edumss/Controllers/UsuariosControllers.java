package org.edumss.Controllers;

import lombok.var;
import org.edumss.domain.users.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.Optional;

@RestController
public class UsuariosControllers {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    //Obter Usuários

    @GetMapping("/login")
    public ResponseEntity getLogin() {
        return ResponseEntity.ok("getLogin");
    }

    @GetMapping(value = "/user/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getUserName(@PathVariable String name) {
        var users = repository.findByNameStartsWithIgnoreCase(name);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/all")
    public ResponseEntity getAll() {
        var allLogins = repository.findAll();
        return ResponseEntity.ok(allLogins);
    }

    //Login Usuários

    @PostMapping("/login")
    public ResponseEntity postLogin(@RequestBody @Valid AuthenticationDTO data) {
        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(data.getName(), data.getPassword());
            var auth = authenticationManager.authenticate(usernamePassword);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok("Login feito com sucesso!");
    }

    //Registrar Usuários

    @PostMapping("/register")
    public ResponseEntity postRegister(@RequestBody @Valid RegisterDTO data) {
        if (this.repository.findByName(data.getName()).isPresent()) return ResponseEntity.badRequest().build();

        String encryptedPassword = passwordEncoder.encode(data.getPassword());
        UserModel user = new UserModel(data.getName(), encryptedPassword, data.getRole(), data.getActive());

        this.repository.save(user);

        return ResponseEntity.ok("Cadastro feito com sucesso!");
    }

    //Editar Usuário

    @PostMapping("/edit/{id}")
    @Transactional
    public ResponseEntity postEdit(@PathVariable String id, @RequestBody RegisterDTO data) {
        Optional<UserModel> Optionaluser = repository.findById(id);

        if (Optionaluser.isPresent()) {
            UserModel user = Optionaluser.get();
            if (data.getName() != null || !data.getName().isEmpty() || !data.getName().equals("")) {
                user.setName(data.getName());
            }
            if (data.getPassword() != null || !data.getPassword().isEmpty() || !data.getPassword().equals("")) {
                String encryptedPassword = passwordEncoder.encode(data.getPassword());
                user.setPassword(encryptedPassword);
            }
            if (data.getRole() != null || data.getRole().getRole().isEmpty() || !data.getRole().getRole().equals("")) {
                user.setRole(data.getRole());
            }
            if (data.getActive() != null) {
                user.setActive(data.getActive());
            }
            return ResponseEntity.ok().build();
        } else {
            throw new EntityNotFoundException();
        }
    }

    @PostMapping("/updateUserName/{id}")
    @Transactional
    public ResponseEntity postUpdateUserName(@PathVariable String id, @RequestBody UpdateUsernameDTO data) {
        UserModel user = repository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        if (!data.getName().isEmpty() || data.getName() != null) {
            user.setName(data.getName());
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/updatePassword/{id}")
    @Transactional
    public ResponseEntity postUpdatePassword(@PathVariable String id, @RequestBody UpdatePasswordDTO data) {
        UserModel user = repository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        if (!data.getPassword().isEmpty() || data.getPassword() != null) {
            String encryptedPassword = passwordEncoder.encode(data.getPassword());
            user.setPassword(encryptedPassword);
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/updateRole/{id}")
    @Transactional
    public ResponseEntity postUpdateRole(@PathVariable String id, @RequestBody UpdateRoleDTO data) {
        UserModel user = repository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        if (!data.getRole().getRole().isEmpty() || data.getRole() != null) {
            user.setRole(data.getRole());
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/updateActive/{id}")
    @Transactional
    public ResponseEntity postUpdateActive(@PathVariable String id, @RequestBody UpdateActiveDTO data) {
        UserModel user = repository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        if (data.getActive() != null) {
            user.setActive(data.getActive());
        }

        return ResponseEntity.ok().build();
    }

    //Deletar e Desativar Usuários Usuário

    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteUser(@PathVariable String id) {
        repository.deleteById(id);
        return ResponseEntity.ok("Usuário Excluido");
    }

    @DeleteMapping("/desativar/{id}")
    @Transactional
    public ResponseEntity desativarUser(@PathVariable String id) {
        Optional<UserModel> user = repository.findById(id);
        if (user.isPresent()) {
            UserModel userModel = user.get();
            userModel.setActive(false);
            return ResponseEntity.ok("Usuário desativado");
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/deleteAllDesable")
    @Transactional
    public ResponseEntity deleteAllDesable(){
        repository.deleteByActiveFalse();
        return ResponseEntity.ok("Todos Usuários desativados foram apagados!");
    }
}