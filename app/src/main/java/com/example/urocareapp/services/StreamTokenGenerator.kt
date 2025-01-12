package com.example.urocareapp.services

import com.google.type.Date
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm


object StreamTokenGenerator {





    fun generateToken(email: String, secretKey: String): String {
        return Jwts.builder()
            .claim("sub", email) // Usa claim para establecer el 'sub' (subject)
            .setIssuedAt(java.util.Date()) // Establece la fecha de emisión
            .setExpiration(java.sql.Date(System.currentTimeMillis() + 3600000)) // Establece la expiración (1 hora)
            .signWith(SignatureAlgorithm.HS256, secretKey) // Firma el token con el algoritmo HS256
            .compact() // Devuelve el token JWT
    }


}