package com.alura.ChallengeLiterAlura.service;

import com.alura.ChallengeLiterAlura.DTO.AutorDTO;
import com.alura.ChallengeLiterAlura.model.Autor;
import com.alura.ChallengeLiterAlura.repository.AutorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AutorService {
    private AutorRepository repository;

    // Método para obtener todos los autores y convertirlos en DTO
    public List<AutorDTO> obtenerTodosLosAutores() {
        List<Autor> autores = repository.findAll();
        return convierteDatos(autores);
    }

    public List<AutorDTO> convierteDatos(List<Autor> autores) {
        return autores.stream()
                .map(a -> new AutorDTO(
                        a.getId(),
                        a.getNombre(),
                        a.getAnoDeNacimiento(),
                        a.getAnoDeFallecimiento()
                ))
                .collect(Collectors.toList());
    }

}
