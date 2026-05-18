\# ReservaMS - Auth Service



\## Descripcion



Este microservicio se encarga de la autenticacion de usuarios dentro del sistema ReservaMS.



Permite registrar usuarios, iniciar sesion, cifrar contrasenas con BCrypt y generar tokens JWT.



\## Responsabilidades



\- Registrar usuarios.

\- Iniciar sesion.

\- Guardar credenciales.

\- Asociar usuarios a roles.

\- Generar token JWT.

\- Cifrar contrasenas con BCrypt.



\## Puerto



8081



\## Base de datos



reservams\_auth\_db



\## Endpoints principales



\- POST /api/v1/auth/register

\- POST /api/v1/auth/login



\## Ejecucion



1\. Crear la base de datos reservams\_auth\_db.

2\. Ejecutar el script SQL ubicado en la carpeta database.

3\. Levantar Eureka Server.

4\. Ejecutar el auth-service.

5\. Probar los endpoints desde Postman o desde el API Gateway.



