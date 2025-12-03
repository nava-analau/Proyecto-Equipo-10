# Sky Runner: Carrera en las Nubes

## Descripción
Sky Runner es un emocionante juego arcade de desplazamiento lateral donde controlas a Aero, un piloto intrépido que navega a través de mundos flotantes llenos de peligros. Esquiva obstáculos, derrota enemigos y recoge power-ups mientras compites por alcanzar la meta con la mayor puntuación.

## Características Principales

### 🎮 Mecánicas de Juego
- **Control intuitivo**: Usa las flechas del teclado para mover tu nave
- **Sistema de disparos**: Presiona ESPACIO para disparar y eliminar enemigos
- **Power-ups**: Recoge turbos para aumentar velocidad y estrellas para bonus de puntuación
- **Sistema de vidas**: Tienes 3 vidas para completar todos los niveles
- **Progresión de dificultad**: Los niveles se vuelven más desafiantes a medida que avanzas

### 🌍 Mundos del Juego
1. **Reino de las Nubes**: Un mundo etéreo con torres de roca flotante y tormentas eléctricas
2. **Cañón de Cristal**: Un paisaje brillante con cristales afilados y puentes de luz
3. **Ciudad Flotante**: Una metrópolis aérea con plataformas y turbinas industriales

### 🎯 Objetivos
- **Primario**: Llegar a la meta antes que tus rivales
- **Secundario**: Obtener la puntuación más alta posible
- **Desafío**: Completar todos los niveles en dificultad "Difícil"

## Controles

| Tecla | Acción |
|-------|---------|
| ⬅️➡️⬆️⬇️ | Mover la nave |
| ESPACIO | Disparar |
| ESC | Pausar/Continuar |
| ENTER | Confirmar selección |
| R | Reiniciar (en Game Over) |

## Instalación y Ejecución

### Opción 1: Ejecutar el archivo JAR
1. Asegúrate de tener Java instalado (versión 8 o superior)
2. Haz doble clic en `SkyRunner.jar`
3. ¡El juego se iniciará automáticamente!

### Opción 2: Compilar desde el código fuente
1. Abre una terminal en la carpeta del proyecto
2. Ejecuta: `compilar.bat`
3. Se creará el archivo `SkyRunner.jar`
4. Ejecuta el archivo generado

### Opción 3: Ejecutar con línea de comandos
```bash
java -jar SkyRunner.jar
```

## Sistema de Puntuación

### Puntos por acciones:
- **Distancia recorrida**: +1 punto por cada 2 píxeles
- **Enemigo derrotado**: +100 puntos
- **Obstáculo esquivado**: +10 puntos
- **Power-up recogido**: +50 puntos
- **Completar nivel**: +Puntuación objetivo del nivel
- **Bonus por dificultad**: +500 (Normal), +1000 (Difícil)

### Multiplicadores:
- **Racha de enemigos**: Cada enemigo consecutivo da más puntos
- **Sin daño**: Bonus por completar niveles sin recibir daño
- **Velocidad**: Bonus por velocidad promedio alta

## Dificultades

### 🟢 Fácil
- Menos obstáculos y enemigos
- Velocidad de desplazamiento lenta
- Más power-ups disponibles
- Ideal para principiantes

### 🟡 Normal
- Balance entre desafío y diversión
- Velocidad de desplazamiento media
- Cantidad estándar de obstáculos
- Experiencia clásica de arcade

### 🔴 Difícil
- Muchos obstáculos y enemigos
- Velocidad de desplazamiento rápida
- Menos power-ups disponibles
- Para jugadores experimentados

## Tipos de Obstáculos

### 🗿 Torres de Roca Flotante
- Obstáculos estáticos que bloquean el camino
- Causan daño moderado al impactar
- Se pueden esquivar con habilidad

### ⚡ Tormentas Eléctricas
- Áreas peligrosas que desestabilizan tu nave
- Reducen temporalmente el control
- Causan daño continuo mientras estás dentro

### 🌪️ Turbinas
- Obstáculos que empujan tu nave
- Pueden empujarte hacia otros peligros
- Requieren precisión para navegar

## Enemigos

### ✈️ Cazas Básicos
- Enemigos estándar con movimiento predecible
- Disparan proyectiles ocasionalmente
- Fáciles de derrotar

### 🛩️ Cazas Avanzados
- Enemigos más rápidos y maniobrables
- Disparan con mayor frecuencia
- Requieren más disparos para derrotar

### 🚁 Jefes de Combate
- Enemigos poderosos con mucha salud
- Patrones de ataque complejos
- Otorgan muchos puntos al derrotarlos

## Power-ups

### 🔥 Turbo
- Aumenta temporalmente tu velocidad
- Te permite esquivar obstáculos más fácilmente
- Dura 5 segundos

### ⭐ Estrella de Puntuación
- Otorga 200 puntos instantáneamente
- Bonus adicional si se recoge en combo
- Ayuda a alcanzar puntuaciones altas

## Consejos y Estrategias

### Para Principiantes
1. **Mantén la calma**: No necesitas moverte constantemente
2. **Aprende los patrones**: Los obstáculos siguen patrones predecibles
3. **Usa los disparos con moderación**: Disparar reduce tu velocidad
4. **Prioriza la supervivencia**: Es mejor esquivar que arriesgarse

### Para Jugadores Avanzados
1. **Combo de enemigos**: Derrota enemigos rápidamente para multiplicadores
2. **Turbo estratégico**: Usa el turbo en secciones difíciles
3. **Rutas óptimas**: Aprende las rutas más seguras por cada nivel
4. **Gestión de riesgo**: A veces vale la pena arriesgarse por power-ups

## Requisitos del Sistema

### Mínimos:
- Java 8 o superior
- 512 MB de RAM
- Procesador de 1 GHz
- Tarjeta gráfica básica

### Recomendados:
- Java 11 o superior
- 1 GB de RAM
- Procesador de 2 GHz
- Tarjeta gráfica dedicada

## Solución de Problemas

### El juego no se inicia
1. Verifica que Java esté instalado: `java -version`
2. Asegúrate de que el archivo JAR no esté corrupto
3. Intenta ejecutar desde la línea de comandos para ver errores

### Problemas de rendimiento
1. Cierra otras aplicaciones
2. Reduce la resolución de pantalla
3. Verifica que tu sistema cumpla los requisitos mínimos

### Controles no responden
1. Asegúrate de que la ventana del juego tenga el foco
2. Intenta reiniciar el juego
3. Verifica que tu teclado funcione correctamente

## Créditos

**Desarrollado por**: [Tu nombre]
**Versión**: 1.0
**Fecha de lanzamiento**: 2024

### Tecnologías utilizadas:
- Java Swing
- Java 2D Graphics
- Programación orientada a objetos
- Sistema de física básico
- Generación procedural de niveles

## Futuras Actualizaciones

### Planeadas:
- 🎵 Más pistas de música para cada mundo
- 🏆 Sistema de logros y recompensas
- 👥 Modo multijugador local
- 🎨 Nuevos diseños de naves
- 📊 Tabla de puntuaciones en línea
- 🌟 Nuevos tipos de power-ups
- 🎯 Modos de juego adicionales

### Contribuciones
¡Las sugerencias y reportes de errores son bienvenidos! Por favor, incluye:
- Versión del juego
- Sistema operativo
- Descripción del problema
- Pasos para reproducir el error

---

**¡Gracias por jugar Sky Runner: Carrera en las Nubes!**

*"El cielo no es el límite, es solo el principio de la aventura"*