package com.aluracursos.literalura.service;

import com.aluracursos.literalura.modelo.Libro;
import com.aluracursos.literalura.repositorio.LibroRepositorio;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LibroService {
    @Autowired
    private LibroRepositorio libroRepositorio;
    @Transactional
    public List<Libro> listarLibros(){
        // Usamos el método con JOIN FETCH para traer los idiomas
        return libroRepositorio.findAllWithDetails();
    }
}
