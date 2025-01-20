package com.alura.ChallengeLiterAlura.DTO;

import com.alura.ChallengeLiterAlura.model.Autor;

public record LibroDTO(Long Id,
                       String titulo,
                       Autor autor,
                       String idioma,
                       Double descargas) {

}
