package com.aluracursos.literalura.repositorio;

import com.aluracursos.literalura.modelo.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LibroRepositorio extends JpaRepository<Libro, Long> {
    boolean existsByTitulo(String titulo);

    @Query("SELECT l FROM Libro l LEFT JOIN FETCH l.idiomas LEFT JOIN FETCH l.autores")
    List<Libro> findAllWithDetails(); // Cambia el nombre a algo significativo
}
