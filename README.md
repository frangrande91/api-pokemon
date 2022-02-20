Instrucciones

Para el back-end se pide construir una API REST usando Java Spring Boot o Node Js que exponga la información al Pokedex.

Esta API debe consumir el servicio externo PokeApi https://pokeapi.co/ para obtener la información. La documentación la puedes encontrar en https://pokeapi.co/docs/v2. 

Se deben exponer los endpoints necesarios para poder montar un frontend (no es parte del entregable) que es una página donde se listan los pokemons inicialmente mostrando la siguiente información (información básica):
- Foto (url)
- Tipo (type)
- Peso (weight)
- Listado de Habilidades (ability)

Cuando el usuario haga click en alguno de los pokemons, se muestra una pantalla con más detalles del pokemon seleccionado:
- Información Básica (devuelta en la lista de pokemons)
- Descripción (en español)
- Lista de Movimientos (campo name en moves)

Debes incluir Swagger en tu API para poder probar los endpoints. Pero si deseas (opcional) puedes hacer un front-end que haga las llamadas mencionadas arriba, y puedes elegir el lenguaje de tu preferencia.  

La aplicación debe ser desplegada en AWS, Azure, GCP o Heroku, a elección. El código de la aplicación debe estar alojado en algún repositorio Git al que debes darnos acceso.
