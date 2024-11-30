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
        while(true){
            System.out.println("Seleccione una opción");
            System.out.println("1. Buscar un libro");
            System.out.println("2. Listar todos los libros");
            System.out.println("3. Salir");
            int opcion = Integer.parseInt(teclado.nextLine());

            switch (opcion){
                case 1:
                    buscarLibro();
                    break;
                case 2:
                    listarLibros();
                    break;
                case 3:
                    System.out.println("Saliendo del sistema...");
                    return;
                default:
                    System.out.println("Opción Inválida, ¡Inténtalo de nuevo!");
            }
        }
    }
    public void buscarLibro(){
        var json = consumoAPI.obtenerDatos(URL_BASE);
        var datos = conversor.obtenerDatos(json, Datos.class);

        System.out.println("Por favor, digite el nombre del título");
        var tituloLibro = teclado.nextLine();
        json = consumoAPI.obtenerDatos(URL_BASE+"?search="
                +tituloLibro.replace(" ", "+"));
        var datosBusqueda = conversor.obtenerDatos(json, Datos.class);
        Optional<DatosLibros> libroBuscado = datosBusqueda.resultados().stream()
                .filter(l -> l.titulo().toUpperCase().contains(tituloLibro.toUpperCase()))
                .findFirst();
        if (libroBuscado.isPresent()){
            DatosLibros datosLibros = libroBuscado.get();
            if (libroRepositorio.existsByTitulo(datosLibros.titulo())){
                System.out.println("El libro ya existe en la DB");
            }else{
                Libro libro = new Libro();
                libro.setTitulo(datosLibros.titulo());
                libro.setNumeroDescargas(datosLibros.numeroDescargas());
                libro.setIdiomas(datosLibros.idiomas());
                List<Autor> autores = datosLibros.autor().stream().map(autor->{
                    Autor nuevoAutor = new Autor();
                    nuevoAutor.setNombre(autor.nombre());
                    nuevoAutor.setFechaDeNacimiento(autor.fechaDeNacimiento());
                    nuevoAutor.setFechaDeFallecimiento(autor.fechaDeFallecimiento());
                    return nuevoAutor;
                }).toList();
                libro.setAutores(autores);
                libroRepositorio.save(libro);
                System.out.println("El libro se ha guardado en la DB");
            }
        }else {
            System.out.println("Libro no encontrado");
        }
    }
    public void listarLibros(){
        System.out.println("Lista de libros guardados en la DB");
        List<Libro> libros = libroRepositorio.findAll();
        if (libros.isEmpty()){
            System.out.println("No hay libros guardados");
        }else {
            libros.forEach(libro -> {
                System.out.println("Título: " + libro.getTitulo());
                System.out.println("Idiomas: " + String.join(", " + libro.getIdiomas()));
                System.out.println("Número de Descargas: " + libro.getNumeroDescargas());
                System.out.println("Autores:");
                /*libro.getAutores().forEach(autor -> {
                    System.out.println("Nombre: " + autor.getNombre());
                    System.out.println("Fecha de Nacimiento: " + autor.getFechaDeNacimiento());
                    System.out.println("Fecha de Fallecimiento: " + autor.getFechaDeFallecimiento());
                });*/

            });
        }
    }
    /*public void muestraMenu(){
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

            if (libroRepositorio.existsByTitulo(datosLibros.titulo())){
                System.out.println("El libro ya existe en la DB");
            } else {
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
            }

        } else {
            System.out.println("Libro NO encontrado");
        }
    }*/
}
