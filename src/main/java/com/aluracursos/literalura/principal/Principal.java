package com.aluracursos.literalura.principal;

import com.aluracursos.literalura.modelo.Datos;
import com.aluracursos.literalura.modelo.DatosLibros;
import com.aluracursos.literalura.service.ConsumoAPI;
import com.aluracursos.literalura.service.ConvierteDatos;

import java.util.Optional;
import java.util.Scanner;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private static final String URL_BASE = "https://gutendex.com/books/";
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();
    public void muestraMenu(){
        var json = consumoAPI.obtenerDatos(URL_BASE);
        System.out.println(json);
        var datos = conversor.obtenerDatos(json, Datos.class);
        System.out.println(datos);
        System.out.println("Por favor, digite el número del título");
        var tituloLibro = teclado.nextLine();
        json = consumoAPI.obtenerDatos(URL_BASE+"?search="
                +tituloLibro.replace(" ", "+"));
        var datosBusqueda = conversor.obtenerDatos(json, Datos.class);
        Optional<DatosLibros> libroBuscado = datosBusqueda.resultados().stream()
                .filter(l -> l.titulo().toUpperCase().contains(tituloLibro.toUpperCase()))
                .findFirst();
        if (libroBuscado.isPresent()){
            DatosLibros libro = libroBuscado.get();
            System.out.println("Libro encontrado: ");
            System.out.println("Título: " + libro.titulo());
            System.out.println("Autor:");
            libro.autor().forEach(autor ->{
                System.out.println("    Nombre: " + autor.nombre());
                System.out.println("    Año de Nacimiento: " + autor.fechaDeNacimiento());
                System.out.println("    Año de Fallecimiento: " + autor.fechaDeFallecimiento());
            });

            System.out.println("    Idiomas: " + libro.idiomas());
            System.out.println("    Numero de Descargas: " + libro.numeroDescargas());
        } else {
            System.out.println("Libro NO encontrado");
        }
    }
}
