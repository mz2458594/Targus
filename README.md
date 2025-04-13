# 💻 Web integrado

Un sistema E-commerce combinado con un gestor de inventario para empleados

---

## 📁 Estructura del Proyecto

```plaintext
src/main/java
├── config/         → Configuraciones de seguridad o emails
├── controllers/ 
    ├──inventario   →  Controladores para el inventario
    ├──rest         →  Controlador para APIs (no usar)
    ├──web          →  Controladores para la web
├── dto/            → Formato para transporte de datos entre conntroladores
├── models/        
    ├──entities     →  las clases del proyecto
├── repositories/          → Las interfaces o modelo de datos de las entidades en la base de datos
├── services/       → Lógica de negocio (métodos CRUD)

src/main/resources
├── static/
    ├──css          →  Archivos css 
    ├──images       →  Imagenes en formato png
    ├──js           →  Archivos js
├── templates/
    ├──commerce     →  HTMLs de la pagina web 
    ├──venta        →  HTMLs del gestor de inventario

```

## 👨‍💻 Flujo de trabajo en equipo

1.  **Clonar el proyecto:**
    Crea tu carpeta local y clona el proyecto dentro de el.

    ```bash
    git clone LINK DEL REPOSITORIO


2.  **Dirigite a la rama Develop**
    Para empezar a realizar cambios dirigirse a la rama de desarrollo (develop).

    ```bash
    git status              -----> para ver en que rama te encuentras 
    git checkout develop    -----> para cambiar de la rama principal a la de Develop
    
3.  **Dirigirse al archivo appplication.properties y configurar la base de datos**
    Realiza los cambios necesarios en el username y password en base a tu configuracion en MySql.

    ```bash
    spring.datasource.username=INGRESA TU USERNAME
    spring.datasource.password=INGRESA TU PASSWORD

4.  **Tener abierto el Mysql:**
    Antes de ejecutar el proyecto por primera vez tener abierto MySql en su Local Instance.


5.  **Ejecutar el proyecto:**
    Dirigirse al archivo EcommerceApplication.java para ejecutar el proyecto.


## 👨‍💻 Subir cambios a la rama de develop

1.  **Con la terminal en Visual Studio Code:**
    ```bash
    git status                             -----> para ver todos los archivos que se han modificado
    git add .                              -----> mueve los cambios del directorio de trabajo al área del entorno de ensayo
    git commit --message "MENSAJE"         -----> confirmando los cambios dejando un mensaje descriptivo
    git push origin develop                -----> subir tus cambios a la rama Develop
    

2.  **Con las herramientas de Visual Studio Code**

   
