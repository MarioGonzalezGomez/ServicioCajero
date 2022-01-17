# ServicioCajero 💸

En esencia, es un cliente servidor multihilo

Tenemos que convertirlo en Servicio. Necesitaremos buscar info de ello, porque un servicio
se ejucuta en segundo plano. Debe valer para linux y recomandablemente para Windows. 

En el programa, tendremos que meter el email/pin
(el pin debe ir cifrado en SHA 512)

, seleccionar lo que queremos hacer
	Sacar efectivo (siempre y cuando no supere un límite diario y tengamos saldo)
	Realizar ingreso (máx 1000 euros)
	Consultar saldo

El servidor tiene un log, un fichero con la información que saldría por consola.
Se puede usar la librería log4J.

Tendremos un Usuario con email, pin, saldo, límite diario
	Movimientos: fecha, tipo, cantidad (si es ingreso o retirada), usuario


Opciones recomendadas:
Que haya una lista de usuarios registrados
O usar una base de datos SQL o NoSql o ficheros

---

## Antes de iniciar :rocket:

Importante ejecutar en consola el siguiente comando, estando situado en nuestra carpeta docker con el yml
`docker-compose up -d`

