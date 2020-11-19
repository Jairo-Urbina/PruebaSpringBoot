package com.bezkoder.springjwt.repository;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import com.bezkoder.springjwt.models.User;

@Service
public class UserRepository {

	private ArrayList<User> usuarios;
	
	public UserRepository() {
		
		usuarios = new ArrayList<User>();
		
		//Leer desde txt
		
		
		File archivo = null;
	      FileReader fr = null;
	      BufferedReader br = null;

	      try {
	         // Apertura del fichero y creacion de BufferedReader para poder
	         // hacer una lectura comoda (disponer del metodo readLine()).
	         archivo = new File ("usuarios.txt");
	         fr = new FileReader (archivo);
	         br = new BufferedReader(fr);

	         // Lectura del fichero
	         String linea;
	         while((linea=br.readLine())!=null) {
	        	String[] a = linea.split(","); 
	        	//creación de usuarios
	    		User user = new User(a[0], encoder().encode(a[1]));
	    		usuarios.add(user);
	         }
	      }
	      catch(Exception e){
	         e.printStackTrace();
	      }finally{
	         // En el finally cerramos el fichero, para asegurarnos
	         // que se cierra tanto si todo va bien como si salta 
	         // una excepcion.
	         try{                    
	            if( null != fr ){   
	               fr.close();     
	            }                  
	         }catch (Exception e2){ 
	            e2.printStackTrace();
	         }
	      }
	}

	public User findByUsername(String username) {
		User buscado = null;
		for (int i = 0; i < usuarios.size(); i++) {
			if(usuarios.get(i).getUsername().equals(username)) {
				buscado = usuarios.get(i);
			}
		}
		return buscado;
	}

	public boolean existsByUsername(String username) {
		boolean existe = false;
		for (int i = 0; i < usuarios.size(); i++) {
			if(usuarios.get(i).getUsername().equals(username)) {
				 existe =  true;
			}
		}
		return existe;
	}

	public void save(User user) {
		usuarios.add(user);
		
	}
	@Bean
	public PasswordEncoder encoder() {
	    return new BCryptPasswordEncoder();
	}


}
