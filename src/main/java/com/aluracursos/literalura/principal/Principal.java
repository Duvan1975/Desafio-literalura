package com.aluracursos.literalura.principal;

import com.aluracursos.literalura.modelo.Autor;
import com.aluracursos.literalura.modelo.Datos;
import com.aluracursos.literalura.modelo.DatosLibros;
import com.aluracursos.literalura.modelo.Libro;
import com.aluracursos.literalura.repositorio.LibroRepositorio;
import com.aluracursos.literalura.service.ConsumoAPI;
import com.aluracursos.literalura.service.ConvierteDatos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Component
public class Principal {
    @Autowired
    private LibroRepositorio libroRepositorio;

    private Scanner teclado = new Scanner(System.in);
    private static final String URL_BASE = "https://gutendex.com/books/";
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();

    public void muestraMenu(){
        var json = consumoAPI.obtenerDatos(URL_BASE);
        var datos = conversor.obtenerDatos(json, Datos.class);

        System.out.println("Por favor, digite el número del título");
        var tituloLibro = teclado.nextLine();
        json = consumoAPI.obtenerDatos(URL_BASE+"?search="
                +tituloLibro.replace(" ", "+"));
        var datosBusqueda = conversor.obtenerDatos(json, Datos.class);
        Optional<DatosLibros> libroBuscado = datosBusqueda.resultados().stream()
                .filter(l -> l.titulo().toUpperCase().contains(tituloLibro.toUpperCase()))
                .findFirst();
        if (libroBuscado.isPresent()){
            DatosLibros datosLibros = libroBuscado.get();
            System.out.println("Libro encontrado: ");
            System.out.println("Título: " + datosLibros.titulo());
            System.out.println("Autor:");

            datosLibros.autor().forEach(autor ->{
                System.out.println("    Nombre: " + autor.nombre());
                System.out.println("    Año de Nacimiento: " + autor.fechaDeNacimiento());
                System.out.println("    Año de Fallecimiento: " + autor.fechaDeFallecimiento());
            });

            System.out.println("    Idiomas: " + datosLibros.idiomas());
            System.out.println("    Numero de Descargas: " + datosLibros.numeroDescargas());

            Libro libro = new Libro();
            libro.setTitulo(datosLibros.titulo());
            libro.setNumeroDescargas(datosLibros.numeroDescargas());
            libro.setIdiomas(datosLibros.idiomas());

            // **Transformación de autores**
            List<Autor> autores = datosLibros.autor().stream().map(autor -> {
                Autor nuevoAutor = new Autor();
                nuevoAutor.setNombre(autor.nombre());
                nuevoAutor.setFechaDeNacimiento(autor.fechaDeNacimiento());
                nuevoAutor.setFechaDeFallecimiento(autor.fechaDeFallecimiento());
                return nuevoAutor;
            }).toList();
            libro.setAutores(autores);

            // **Guardar en la base de datos**
            libroRepositorio.save(libro);
            System.out.println("El libro se ha guardado en la base de datos.");
        } else {
            System.out.println("Libro NO encontrado");
        }
    }
}
