package com.example.projetfind.etude.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.projetfind.etude.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    

    // sert a vérifier un mail existe 
    boolean existsByEmail(String email);
    // meme chose si le nom na jamais été utiliser dans la base lors de la creation
    boolean existsByUsername(String username);

    // renvoie directement l'utilisateur si trv
    Optional<UserEntity> findByEmailOrUsername(String email, String username);


}
