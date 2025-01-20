package com.alura.ChallengeLiterAlura.controller;

import com.alura.ChallengeLiterAlura.DTO.LibroDTO;
import com.alura.ChallengeLiterAlura.service.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/libros")
public class LibroController {
    @Autowired
    private LibroService servicio;

    @GetMapping()
    public List<LibroDTO> obtenerTodosLosLibros(){

        return servicio.obtenerTodosLosLibros();
    }
}
