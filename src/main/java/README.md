#TALLER GARAJE 2


--------------------------------------------
#ARQUITECTURA

##Vista (JSP)
Es la página que ve el usuario. 
Muestra la lista de vehículos y el formulario.

## Controlador (Servlet)
Recibe las peticiones, llama a la lógica y decide a qué JSP ir.

##Reglas de negocio (EJB /Facade)
Aquí están las reglas (por ejemplo: placa única, propietario con mínimo de caracteres, colores permitidos, antigüedad máxima, etc.). 
Usa un DataSource JNDI llamado jdbc/myPool para pedir conexiones a la BD.

##DAO (JDBC)
Habla directamente con MySQL usando SQL. 
Tiene los CRUD típicos: listar, buscarPorId, agregar, actualizar, eliminar, y utilidades como existePlaca.

##Modelo
Es la clase “contenedor” con los campos del vehículo: id, placa, marca, modelo(año), color, propietario.

#Configuración Web:

## web.xml 
Define el nombre de la app, el resource-ref a jdbc/myPool y el welcome-file.

## glassfish-web.xml 
Trae ajustes del contenedor (GlassFish/Payara).

El Servlet también está anotado con @WebServlet("/vehiculos").

## Build y dependencias: pom.xml (Maven)
Empaqueta como WAR, compila con Java 11, usa Jakarta EE 10 (Servlet/JSP/JSTL) y el driver MySQL.

---------------------------------------------------------------------------------------------------------------

# GitHub

se crea el repositorio en github y se crearon las ramas 
colaboración entre Sebastian Arley Jacome Orduz y Liseth Xiomara Guiza Gonzalez
Clonar vehiculos en net beans 

---------------------------------------------------------------------------------------------------------------

# Base de datos (MySQL)

Crea la BD y la tabla:

CREATE DATABASE garaje CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE garaje;

CREATE TABLE vehiculos (
  id INT AUTO_INCREMENT PRIMARY KEY,
  placa VARCHAR(20) NOT NULL UNIQUE,
  marca VARCHAR(50) NOT NULL,
  modelo VARCHAR(4)  NOT NULL, 
  color VARCHAR(20)  NOT NULL,
  propietario VARCHAR(100) NOT NULL
);


Pool de conexiones y creación de JDBC Resource
-----------------------------------------------------------------------------------------------------------------

# Implementación de las reglas de negocio

• No permitir agregar un vehículo con la placa duplicada.
• No aceptar propietario vacío o con menos de 5 caracteres.
• La marca, modelo y placa deben tener al menos 3 caracteres.
• El color solo acepta valores de una lista predefinida (ejemplo: Rojo, Blanco, Negro, Azul, Gris).
• No aceptar vehículos cuyo modelo tenga más de 20 años de antigüedad (por ejemplo, año < actual - 20).
• Las placas deben ser únicas en toda la base.
• No se puede eliminar un vehículo si el propietario es “Administrador”.
• Actualizar solo si el vehículo realmente existe.
• Validar que los campos no contengan SQL Injection (simular validaciones).
• Al agregar un vehículo con marca “Ferrari”, enviar notificación (simulada).


#Ponerle estilo y colores
------------------------------------------------------------------------------------------------------------------

#Compilación y ejecución

