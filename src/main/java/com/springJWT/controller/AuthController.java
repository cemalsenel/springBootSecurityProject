package com.springJWT.controller;

import com.springJWT.model.ERoller;
import com.springJWT.model.Kisi;
import com.springJWT.model.KisiRole;
import com.springJWT.repository.KisiRepository;
import com.springJWT.repository.RoleRepository;
import com.springJWT.request_response.LoginRequest;
import com.springJWT.request_response.MesajResponse;
import com.springJWT.request_response.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    KisiRepository kisiRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<?> girisYap(@RequestBody LoginRequest loginRequest){
        //Kimlik denetiminin yapılması
       Authentication authentication = authenticationManager
               .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        return null;
    }










    @PostMapping("/register")
    public ResponseEntity<?> kayitOl(@RequestBody RegisterRequest registerRequest){
        //kayıt olan kullanıcının username'ini kontrol et. Daha önceden kullanılmış ise hata döndür
        if(kisiRepository.existsByUsername(registerRequest.getUsername())){
            return ResponseEntity.badRequest().body(new MesajResponse("Hata : Username Kullanılıyor."));
        }

        //kayıt olan kullanıcının email'ini kontrol et. Daha önceden kullanılmış ise hata döndür
        if(kisiRepository.existsByEmail(registerRequest.getEmail())){
            return ResponseEntity.badRequest().body(new MesajResponse("Hata : Email Kullanılıyor."));
        }

        //Yeni kullaıcıyı kaydet
        Kisi kisi = new Kisi(registerRequest.getUsername(), passwordEncoder.encode(registerRequest.getPassword()), registerRequest.getEmail());
        Set<String> stringRoller = registerRequest.getRole();
        Set<KisiRole> roller = new HashSet<>();

        if(stringRoller == null){
            KisiRole userRole = roleRepository.findByName(ERoller.ROLE_USER).orElseThrow(() -> new RuntimeException("hata: Veritabanında Role kayıtlı değil"));

            roller.add(userRole);
        }else {
            stringRoller.forEach(role -> {
                switch (role){
                    case "admin":
                        KisiRole adminRole = roleRepository.findByName(ERoller.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Hata: Role mevcut değil."));
                        roller.add(adminRole);
                        break;
                    case "mod":
                        KisiRole modRole = roleRepository.findByName(ERoller.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Hata: Role mevcut değil."));
                        roller.add(modRole);
                        break;
                    default:
                        KisiRole userRole = roleRepository.findByName(ERoller.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Hata: Role mevcut değil."));
                        roller.add(userRole);
                            }
            });
            kisi.setRoller(roller);

            //Veritabanına yeni kişi ekle
            kisiRepository.save(kisi);
        }


        return ResponseEntity.ok(new MesajResponse("Kullanıcı başarı ile kaydedildi."));
    }
}
