package com.alura.ChallengeLiterAlura.principal;

import com.alura.ChallengeLiterAlura.model.Autor;
import com.alura.ChallengeLiterAlura.model.DatosAutor;
import com.alura.ChallengeLiterAlura.model.DatosLibro;
import com.alura.ChallengeLiterAlura.model.Libro;
import com.alura.ChallengeLiterAlura.repository.AutorRepository;
import com.alura.ChallengeLiterAlura.repository.LibroRepository;
import com.alura.ChallengeLiterAlura.service.ConsumoAPI;
import com.alura.ChallengeLiterAlura.service.ConvierteDatos;
import com.alura.ChallengeLiterAlura.service.ConvierteDatosAutor;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private ConsumoAPI consumoAPI =new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();
    private ConvierteDatosAutor conversorDeAutor = new ConvierteDatosAutor();
    private final String URL_BASE = "https://gutendex.com/books/";
    private Scanner entrada =new Scanner(System.in);
    private LibroRepository libroRepository;
    private AutorRepository autorRepository;
    private List<Libro> libros;
    private List<Autor>autores;
    private Optional<Autor>autorBuscado;

    public Principal(LibroRepository libroRepositorio, AutorRepository autorRepositorio){
        this.libroRepository = libroRepositorio;
        this.autorRepository = autorRepositorio;
    }

    public void muestraElMenu() {
        int opcion = -1;
        while (opcion != 0) {
            try {
                mostrarMenu();
                opcion = obtenerOpcionDelUsuario();
                procesarOpcion(opcion);
            } catch (InputMismatchException e) {
                System.out.println("Entrada incorrecta. Por favor, ingrese un número del 0 al 10.");
                entrada.nextLine(); // Limpiar el buffer del scanner
            }
        }
        System.out.println("Cerrando la aplicación...");
        System.exit(0);
    }


    private void mostrarMenu() {
        var menu = """
                
                -----------------------------------------------------------------------------
                                              LiterAlura
                -----------------------------------------------------------------------------
                Por favor, seleccione una opción que desea realizar:
                1- Consultar y guardar libros desde la API 
                2- Listar libros registrados 
                3- Listar autores registrados
                4- Buscar autores vivos en un determinado año de la BD
                5- Buscar libros registrados en la BD por idioma
                6- Buscar autores registrados, por nombre
                7- Buscar los 10 libros más descargados de la API
                8- Buscar los 10 libros más descargados, registrados
                9- Búsqueda de autores registrados, nacidos después de un año específicol  
                10- Buscar autores registrados, fallecidos antes de un año específico 
                0 - Salir
            """;
        System.out.println(menu);
    }


    private int obtenerOpcionDelUsuario() {
        System.out.print("Ingrese su opción: ");
        return entrada.nextInt();
    }


    private void procesarOpcion(int opcion) {
        entrada.nextLine(); // Limpiar el buffer del scanner
        System.out.println(); // Salto de línea después de seleccionar la opción
        switch (opcion) {
            case 1:
                buscarYGuardarLibroAPI();
                break;
            case 2:
                mostrarLibrosBaseDatos();
                break;
            case 3:
                mostrarAutoresBaseDatos();
                break;
            case 4:
                mostrarAutoresVivosEnUnDeterminadoAno();
                break;
            case 5:
                mostrarLibrosPorIdioma();
                break;
            case 6:
                buscarAutorPorNombreEnBD();
                break;
            case 7:
                buscarLibrosTop10EnAPI();
                break;
            case 8:
                buscarLibrosTop10EnLaDB();
                break;
            case 9:
                buscarAutoresFallecidosAntesDeFecha();
                break;
            case 10:
                buscarAutoresNacidosDespuesDeFecha();
                break;
            case 0:
                break;
            default:
                System.out.println("Opción inválida. Por favor, intente nuevamente.");
        }
        System.out.println();
    }

    // Método para obtener datos de un libro desde la API
    private DatosLibro obtenerDatosLibroAPI(String nombreLibro) {
        var json = consumoAPI.obtenerDatos(URL_BASE + "?search=" + nombreLibro.replace(" ", "+"));
        return conversor.obtenerDatos(json, DatosLibro.class);
    }

    // Método para obtener datos de un autor desde la API
    private DatosAutor obtenerDatosAutorAPI(String nombreLibro) {
        var json = consumoAPI.obtenerDatos(URL_BASE + "?search=" + nombreLibro.replace(" ", "+"));
        return conversorDeAutor.obtenerDatos(json, DatosAutor.class);
    }

    // Método para buscar y guardar un libro desde la API
    public void buscarYGuardarLibroAPI() {
        System.out.println("¿Cuál es el título del libro que desea buscar en la API Gutendex?");
        String libroBuscado = entrada.nextLine();
        libros = libros != null ? libros : new ArrayList<>();
        Optional<Libro> optionalLibro = libros.stream()
                .filter(l -> l.getTitulo().toLowerCase().contains(libroBuscado.toLowerCase()))
                .findFirst();
        if (optionalLibro.isPresent()) {
            var libroEncontrado = optionalLibro.get();
            System.out.println("Este libro ya ha sido cargado previamente:");
            System.out.println(libroEncontrado);
            System.out.println("Por favor, pruebe con otro título.");
        } else {
            try {
                DatosLibro datosLibro = obtenerDatosLibroAPI(libroBuscado);
                if (datosLibro != null) {
                    DatosAutor datosAutor = obtenerDatosAutorAPI(libroBuscado);
                    if (datosAutor != null) {
                        List<Autor> autores = autorRepository.findAll();
                        autores = autores != null ? autores : new ArrayList<>();
                        Optional<Autor> autorOptional = autores.stream()
                                .filter(a -> datosAutor.nombre() != null &&
                                        a.getNombre().toLowerCase().contains(datosAutor.nombre().toLowerCase()))
                                .findFirst();
                        Autor autor;
                        if (autorOptional.isPresent()) {
                            autor = autorOptional.get();
                        } else {
                            autor = new Autor(
                                    datosAutor.nombre(),
                                    datosAutor.anoDeNacimiento(),
                                    datosAutor.anoDeFallecimiento()
                            );
                            autorRepository.save(autor);
                        }
                        Libro libro = new Libro(
                                datosLibro.titulo(),
                                autor,
                                datosLibro.idioma() != null ? datosLibro.idioma() : Collections.emptyList(),
                                datosLibro.descargas()
                        );
                        libros.add(libro);
                        autor.setLibros(libros);
                        System.out.println("Se encontró el siguiente libro:");
                        System.out.println(libro);
                        libroRepository.save(libro);
                        System.out.println("El libro ha sido guardado exitosamente.");
                    } else {
                        System.out.println("No se encontró información sobre el autor para este libro.");
                    }
                } else {
                    System.out.println("No se encontró el libro con el título proporcionado.");
                }
            } catch (Exception e) {
                System.out.println("Se produjo una excepción: " + e.getMessage());
            }
        }
    }

    // Método para mostrar libros de la base de datos
    private void mostrarLibrosBaseDatos() {
        System.out.println("----------------------------------------------------------------------------------");
        System.out.println("------------------- Libros registrados en la base de datos -----------------------");
        System.out.println("----------------------------------------------------------------------------------");
        try {
            List<Libro> libros = libroRepository.findAll();
            libros.stream()
                    .sorted(Comparator.comparing(Libro::getDescargas))
                    .forEach(System.out::println);
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
            libros = new ArrayList<>();
        }
    }

    // Método para mostrar autores de la base de datos
    public void mostrarAutoresBaseDatos() {
        System.out.println("----------------------------------------------------------------------------------");
        System.out.println("--------------------- Autores registrados en la Base de Datos --------------------");
        System.out.println("----------------------------------------------------------------------------------");
        autores = autorRepository.findAll();
        autores.stream()
                .forEach(System.out::println);
    }


    public void mostrarAutoresVivosEnUnDeterminadoAno() {
        System.out.println("----------------------------------------------------------------------------------");
        System.out.println("------------- Búsqueda de autores vivos en un año especifico ---------------------");
        System.out.println("----------------------------------------------------------------------------------");
        System.out.print("Ingrese un año: ");
        int anio = entrada.nextInt();
        List<Autor> autores = autorRepository.findAll();
        List<String> autoresNombre = autores.stream()
                .filter(a -> a.getAnoDeFallecimiento() >= anio && a.getAnoDeNacimiento() <= anio)
                .map(Autor::getNombre)
                .collect(Collectors.toList());

        if (autoresNombre.isEmpty()) {
            System.out.println("No se encontraron autores vivos en el año " + anio);
        } else {
            System.out.println("----------------------------------------");
            System.out.println("Autores vivos en el año " + anio + ":");
            System.out.println("----------------------------------------");
            autoresNombre.forEach(System.out::println);
            System.out.println("----------------------------------------");
        }
    }

    // Método para mostrar libros por idioma desde la base de datos
    public void mostrarLibrosPorIdioma() {
        libros = libroRepository.findAll();
        System.out.println("----------------------------------------------------------------------------------");
        System.out.println("-------------- Búsqueda de libros registrados en la BD por idioma ----------------");
        System.out.println("----------------------------------------------------------------------------------");
        System.out.println("Ingrese el idioma del que desea buscar los libros: en (english) o es (español)");
        String idiomaBuscado = entrada.nextLine();
        List<Libro> librosBuscados = libros.stream()
                .filter(l -> l.getIdioma().contains(idiomaBuscado))
                .collect(Collectors.toList());
        librosBuscados.forEach(System.out::println);
    }

    // Método para buscar autor por nombre en la base de datos
    public void buscarAutorPorNombreEnBD() {
        System.out.println("----------------------------------------------------------------------------------");
        System.out.println("------------------ Búsqueda de un autor registrado en la BD ----------------------");
        System.out.println("----------------------------------------------------------------------------------");
        System.out.println("Ingrese el nombre del autor que desea buscar");
        var nombreAutor = entrada.nextLine();
        autorBuscado = autorRepository.findByNombreContainingIgnoreCase(nombreAutor);
        if (autorBuscado.isPresent()) {
            System.out.println(autorBuscado.get());
        } else {
            System.out.println("Autor no encontrado");
        }
    }

    // Método para buscar los 10 libros más descargados de la API
    public void buscarLibrosTop10EnAPI() {
        System.out.println("----------------------------------------------------------------------------------");
        System.out.println("---------------- Top 10 de libros más descargados de la API ----------------------");
        System.out.println("----------------------------------------------------------------------------------");
        try {
            String json = consumoAPI.obtenerDatos(URL_BASE + "?sort");

            List<DatosLibro> datosLibros = conversor.obtenerDatosArray(json, DatosLibro.class);
            List<DatosAutor> datosAutor = conversorDeAutor.obtenerDatosArray(json, DatosAutor.class);

            List<Libro> libros = new ArrayList<>();
            for (int i = 0; i < datosLibros.size(); i++) {
                Autor autor = new Autor(
                        datosAutor.get(i).nombre(),
                        datosAutor.get(i).anoDeNacimiento(),
                        datosAutor.get(i).anoDeFallecimiento());

                Libro libro = new Libro(
                        datosLibros.get(i).titulo(),
                        autor,
                        datosLibros.get(i).idioma(),
                        datosLibros.get(i).descargas());
                libros.add(libro);
            }

            libros.sort(Comparator.comparingDouble(Libro::getDescargas).reversed());

            List<Libro> top10Libros = libros.subList(0, Math.min(10, libros.size()));

            for (int i = 0; i < top10Libros.size(); i++) {
                System.out.println((i + 1) + ". " + top10Libros.get(i));
            }

        } catch (NullPointerException e) {
            System.out.println("Error al buscar los libros: " + e.getMessage());
        }
    }

    // Método para buscar los 10 libros más descargados registrados en la base de datos
    public void buscarLibrosTop10EnLaDB() {
        System.out.println("----------------------------------------------------------------------------------");
        System.out.println("----------- Top 10 de libros más descargados registrados en  la BD ---------------");
        System.out.println("----------------------------------------------------------------------------------");
        try {
            List<Libro> libros = libroRepository.findAll();
            List<Libro> librosOrdenados = libros.stream()
                    .sorted(Comparator.comparingDouble(Libro::getDescargas).reversed())
                    .collect(Collectors.toList());
            List<Libro> topLibros = librosOrdenados.subList(0, Math.min(10, librosOrdenados.size()));
            for (int i = 0; i < topLibros.size(); i++) {
                System.out.println((i + 1) + ". " + topLibros.get(i));
            }
        } catch (NullPointerException e) {
            System.out.println("Error al buscar los libros en la base de datos: " + e.getMessage());
            libros = new ArrayList<>();
        }
    }

    // Método para buscar autores nacidos después de una fecha en la base de datos
    public void buscarAutoresNacidosDespuesDeFecha() {
        try {
            System.out.println("----------------------------------------------------------------------------------");
            System.out.println("------------ Búsqueda de autores nacidos después de un año específico ------------");
            System.out.println("----------------------------------------------------------------------------------");
            System.out.print("Ingrese el año para buscar autores nacidos después de: ");
            int fechaLimite = Integer.parseInt(entrada.nextLine());

            List<Autor> autores = autorRepository.findByFechaNacimientoAfter(fechaLimite);

            if (autores.isEmpty()) {
                System.out.println("No se encontraron autores nacidos después de " + fechaLimite);
            } else {
                System.out.println("----------------------------------------------------------------------------------");
                System.out.println("Autores registrados en la base de datos nacidos después de " + fechaLimite + ":");
                System.out.println("----------------------------------------------------------------------------------");

                for (int i = 0; i < autores.size(); i++) {
                    Autor autor = autores.get(i);
                    System.out.println((i + 1) + ". " + autor.getNombre() + "\n" +
                            "   Año de nacimiento: " + autor.getAnoDeNacimiento());
                }
            }

        } catch (NumberFormatException e) {
            System.out.println("Error: Ingrese un año válido.");
        }
    }

    // Método para buscar autores fallecidos antes de una fecha en la base de datos
    public void buscarAutoresFallecidosAntesDeFecha() {
        try {
            System.out.println("-------------------------------------------------------------------------");
            System.out.println("------- Búsqueda de autores fallecidos antes de un año específico -------");
            System.out.println("-------------------------------------------------------------------------");
            System.out.print("Ingrese el año para buscar autores que fallecieron antes de: ");
            int fechaLimite = Integer.parseInt(entrada.nextLine());

            List<Autor> autores = autorRepository.findByFechaFallecimientoBefore(fechaLimite);

            if (autores.isEmpty()) {
                System.out.println("No se encontraron autores que fallecieron antes de " + fechaLimite);
            } else {
                System.out.println("-----------------------------------------------------------------------------------------");
                System.out.println("Autores registrados en la base de datos que fallecieron antes de " + fechaLimite + ":");
                System.out.println("-----------------------------------------------------------------------------------------");

                for (int i = 0; i < autores.size(); i++) {
                    Autor autor = autores.get(i);
                    System.out.println((i + 1) + ". " + autor.getNombre() + "\n" +
                            "   Año de fallecimiento: " + autor.getAnoDeFallecimiento());
                }
            }

        } catch (NumberFormatException e) {
            System.out.println("Error: Ingrese un año válido.");
        }
    }
}
