<h1 align =center>Literalura</h1> 
Es un gestor de libros, que utiliza la API GUTENDEX para hacer busquedas y guardarlos en una DB, para hacer consultas por autor, nombre del libro. De igual forma para consultar los autores registrados.

<h2>Características ✨</h2>
<ul>Cuenta con las siguientes opciones:
 <li>Consultar y guardar libros desde la API </li>
 <li>Listar libros registrados </li>
 <li>Listar autores registrados</li>
 <li> Buscar autores vivos en un determinado año de la BD</li>
 <li> Buscar libros registrados en la BD por idioma</li>
 <li> Buscar autores registrados, por nombre</li>
 <li>Buscar los 10 libros más descargados de la API</li>
 <li> Buscar los 10 libros más descargados, registrados</li>
 <li>Búsqueda de autores registrados, nacidos después de un año específicol </li> 
 <li>Buscar autores registrados, fallecidos antes de un año específico </li>
</ul>

<h2>Requerimientos 📋</h2>
<ul>
<li>Java 17 o superior.</li>
<li>Un entorno de desarrollo como IntelliJ IDEA, Eclipse, o VS Code con extensiones para Java.</li>
<li>Conexión a internet para consumir la API.</li>
<li>Postman</li>
</ul>

<h2>Uso 🖥️</h2>
Ejecuta el archivo Principal para iniciar el programa.
En la consola, selecciona la conversión que deseas realizar ingresando el número correspondiente al menú.
Digita la opción que deseas realizar.
Si deseas salir, selecciona la opción "Salir" en el menú.

<h2>API Utilizada </h2>
Este proyecto utiliza GUTENDEX API para obtener los datos de los libros.

<h2>Estructura del Proyecto 📂</h2>
<h3>Paquetes:</h3> Controller, DTO, model, principal, repository, service.
<h3>Clases en controller:</h3> LibroController.
<h3>Clases en DTO</h3>:AutorDTO, LibroDTO.
<h3>Clases en model</h3>: Autor, DatosAutor, Libro, DatosLibro.
<h3>Clases en principal</h3>: Principal.
<h3>Clases en repository</h3>: LibroRepository, AutorRepository.
<h3>Clases en service</h3>: AutorService, ConsumoAPI, ConvierteDatos, ConvierteDatosAutor, IConvierteDatos, LibroService.
              
