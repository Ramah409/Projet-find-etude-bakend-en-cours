package com.example.projetfind.etude.dto;

// Ce fichier sert uniquement aux objets qui circulent dans l’API
public class UserDto {

    // Champs = données transmises via l’API
    private Long id;
    private String name;
    private String email;
    private String username;
    private String password;

    // Constructeur vide obligatoire (Spring en a besoin pour transformer JSON -> Objet)
    public UserDto() {}

    // Constructeur pratique pour créer un UserDto directement
    public UserDto(Long id, String name, String email, String username, String password){
        this.id = id;
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
    }

    // ====== Getters / Setters ======

    public Long getId(){
        return id;
    }
    public void setId(Long id){
        this.id = id;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email = email; 
    }

    public String getUsername(){   
        return username;
    }
    public void setUsername(String username){  
        this.username = username;
    }

    public String getPassword(){
        return password;
    }
    public void setPassword(String password){
        this.password = password;
    }
}
