# Pruebas para obtener Tweets

Dentro del directorio "interests" hay una aplicación Play que almacena los intereses de los usuarios (por ahora solo un usuario dummy). Los intereses son almacenados en una base de datos mysql.  
Dentro del directorio "stream" hay otra aplicación Play a la que se le indica cuales son los intereses actuales y se encarga de conectarse al stream de twitter para almacenarlos en una base de datos. Los tweets son almacenados en una base de datos mongodb.

Para facilitar la creación de las bases de datos para hacer pruebas, existe el archivo docker-compose.yml que tiene configuradas las dos bases de datos (requiere que no haya otro mongodb o mysql corriendo en los puertos default). Para levantar las bases ejecutar el siguiente comando:

```bash
docker-compose up
```

## Intereses

Para levantar la aplicación ejecutar el siguiente comando dentro del directorio "interests":

```bash
sbt -Dplay.evolutions.db.default.autoApply=true run
```

### Agregar un nuevo interés

```bash
curl -i -X POST 'http://localhost:9000/api/interests' -H 'Content-Type: application/json' -d '{"value": "#StartTrek"}'
```

### Listar intereses

```bash
curl -i -X GET 'http://localhost:9000/api/interests'
```

### Obtener un determinado interés por id

```bash
curl -i -X GET 'http://localhost:9000/api/interests/1'
```

### Eliminar un interés por id

```bash
curl -i -X DELETE 'http://localhost:9000/api/interests/1'
```

## Stream Tweets

```
Es necesario tener configurado las siguientes variables de entorno antes de levantar la aplicación.

TWITTER_CUSTOMER_KEY
TWITTER_CUSTOMER_SECRET
TWITTER_ACCESS_KEY
TWITTER_ACCESS_SECRET

O tener un archivo de configuración con el siguiente contenido:

include "application.conf"
  
twitter {
  consumer {
    key = " customer key "
    secret = " customer secret "
  }
  access {
    key = " access key "
    secret = " access secret "
  }
}
```

Para levantar la aplicación ejecutar el siguiente comando dentro del directorio "stream":

_Para esta aplicación usamos el puerto 9001 para no entrar en conflicto con el default (9000) en el que levantamos la aplicación de intereses._

Usando variables de entorno:

```bash
sbt -Dhttp.port=9001 run
```

Usando archivo de configuración:

```bash
sbt -Dconfig.file=/ruta/al/conf -Dhttp.port=9001 run
```

### Indicar los intereses de los que se va a querer obtener tweets

_Esto elimina los que actualmente estan y setea estos._

```bash
curl -i -X POST 'http://localhost:9001/streams' -H 'Content-Type: application/json' -d '{"interests": ["#StarTrek", "#StarWars"]}'
```

### Agregar intereses

```bash
curl -i -X PUT 'http://localhost:9001/streams' -H 'Content-Type: application/json' -d '{"interests": ["#StarTrek", "#StarWars"]}'
```

### Eliminar intereses

```bash
curl -i -X DELETE 'http://localhost:9001/streams' -H 'Content-Type: application/json' -d '{"interests": ["#StarTrek", "#StarWars"]}'
```

### Actualizar stream

Ninguno de los llamados anteriores actualiza el stream, una vez hayamos realizado los cambios, es necesario llamar a la siguiente ruta:

```bash
curl -i -X GET 'http://localhost:9001/streams/run'
```

## Agente

Existe el proyecto "agent" que se encarga de pedir al servicio "interests" los últimos intereses agregados (se configura y por defecto está en los últimos 5 minutos) y luego agregar los mismos al servicio "stream" y luego actualizar el stream.  

Ejecutar el agente con el siguiente comando desde la carpera agent:

```bash
sbt run
```
