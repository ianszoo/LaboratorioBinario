# Formato binario .edt

Formato propio del editor. Todos los enteros van en big-endian (orden de red), que es el orden que usan `DataOutputStream` y `DataInputStream` de Java. El archivo no depende de ninguna biblioteca externa: se escribe y se lee campo por campo, en el orden fijo que se describe abajo.

Las clases que implementan el formato están en el package `Binario`:

| Clase | Responsabilidad |
|---|---|
| `FormatoEDT` | Constantes (firma, versión, marcadores, límites) y lectura/escritura de cadenas |
| `CabeceraArchivo` | Cabecera de 20 bytes |
| `FormatoTexto` | Atributos de formato (fuente, tamaño, color, estilos) |
| `FragmentoTexto` | Un tramo de texto con un mismo formato |
| `TablaDatos` | Una tabla: posición, dimensiones, celdas con contenido y formato |
| `Documento` | Lista de fragmentos y lista de tablas |
| `GestorArchivo` | Escribe y lee el archivo completo, valida y lanza las excepciones |

## Estructura general

```
+----------------------+
| CABECERA (20 bytes)  |
+----------------------+
| CUERPO               |
|   Sección de texto   |
|   Sección de tablas  |
|   Marcador EOF       |
+----------------------+
```

El checksum y la longitud declarados en la cabecera se calculan sobre el cuerpo completo (las dos secciones más el marcador EOF).

## Cabecera (20 bytes, tamaño fijo)

| Offset | Tamaño | Tipo | Campo | Valor |
|---|---|---|---|---|
| 0 | 4 | bytes | Firma (magic number) | `45 44 54 31` = "EDT1" |
| 4 | 4 | int | Versión del formato | `1` |
| 8 | 4 | int | Longitud del cuerpo en bytes | N |
| 12 | 8 | long | Checksum CRC32 del cuerpo | valor de `java.util.zip.CRC32` |

## Cuerpo

### Sección de texto

| Tamaño | Tipo | Campo |
|---|---|---|
| 1 | byte | Marcador de sección: `0xA1` |
| 4 | int | Cantidad de fragmentos (F) |
| variable | Fragmento × F | Fragmentos en el orden en que aparecen en el documento |

El texto del documento se guarda como una secuencia de fragmentos. Cada fragmento es un tramo consecutivo de caracteres que comparten exactamente el mismo formato; al concatenar el texto de todos los fragmentos se obtiene el texto completo, saltos de línea incluidos. Donde hay una tabla insertada el texto contiene un espacio (`0x20`) que actúa como marcador de posición; la tabla que va en ese lugar se guarda en la sección de tablas con ese offset.

**Fragmento**

| Tamaño | Tipo | Campo |
|---|---|---|
| 4 | int | Longitud en bytes del texto (L) |
| L | bytes | Texto codificado en UTF-8 |
| variable | Formato | Formato del fragmento (ver abajo) |

**Formato** (se usa igual en fragmentos y en celdas de tabla)

| Tamaño | Tipo | Campo |
|---|---|---|
| 2 + n | UTF (`writeUTF`) | Familia tipográfica, por ejemplo "Arial" |
| 4 | int | Tamaño de fuente en puntos (1 a 400) |
| 4 | int | Color en formato ARGB (`Color.getRGB()`) |
| 1 | byte | Banderas de estilo |

Banderas de estilo, un bit por estilo, el resto debe ser cero:

| Bit | Valor | Estilo |
|---|---|---|
| 0 | `0x01` | Negrita |
| 1 | `0x02` | Cursiva |
| 2 | `0x04` | Subrayado |
| 3 | `0x08` | Tachado |

Un fragmento "rojo, negrita, tamaño 16, Arial" se escribe con familia "Arial", tamaño 16, color `0xFFFF0000` y banderas `0x01`.

### Sección de tablas

| Tamaño | Tipo | Campo |
|---|---|---|
| 1 | byte | Marcador de sección: `0xA2` |
| 4 | int | Cantidad de tablas (T) |
| variable | Tabla × T | Tablas en orden de aparición en el documento |

**Tabla**

| Tamaño | Tipo | Campo |
|---|---|---|
| 4 | int | Posición: offset del carácter marcador dentro del texto |
| 4 | int | Filas (R), de 1 a 1000 |
| 4 | int | Columnas (C), de 1 a 100 |
| variable | Celda × (R·C) | Celdas por filas, de izquierda a derecha y de arriba a abajo |

**Celda**

| Tamaño | Tipo | Campo |
|---|---|---|
| 4 | int | Longitud en bytes del contenido (L) |
| L | bytes | Contenido en UTF-8 |
| variable | Formato | Formato de la celda (misma estructura que en los fragmentos) |

### Marcador de fin

| Tamaño | Tipo | Campo |
|---|---|---|
| 1 | byte | `0xFF` |

Después del marcador no puede haber más bytes.

## Ejemplo real (primeros bytes de un archivo)

```
45 44 54 31              firma "EDT1"
00 00 00 01              versión 1
00 00 02 4f              cuerpo de 591 bytes
00 00 00 00 28 40 74 4c  CRC32 del cuerpo
a1                       inicio de sección de texto
00 00 00 09              9 fragmentos
00 00 00 11              primer fragmento: 17 bytes de texto
49 6e 66 6f 72 6d 65 20 64 65 20 76 65 6e 74 61 73   "Informe de ventas"
00 09 53 61 6e 73 53 65 72 69 66   fuente "SansSerif" (writeUTF: 2 bytes de longitud + texto)
00 00 00 14              tamaño 20
ff 00 46 8c              color ARGB (0, 70, 140)
01                       banderas: negrita
...
```

## Proceso de lectura y manejo de errores

`GestorArchivo.leer` valida en este orden y lanza una excepción distinta para cada situación:

| Situación | Cómo se detecta | Excepción |
|---|---|---|
| Extensión equivocada | El nombre no termina en `.edt` | `ExtensionInvalidaException` |
| El archivo no existe | `File.exists()` / `isFile()` | `ArchivoNoEncontradoException` |
| Archivo vacío o con menos de 4 bytes | No alcanza para leer la firma | `ArchivoCorruptoException` |
| Firma distinta de "EDT1" | Comparación byte a byte | `ArchivoCorruptoException` |
| Cabecera incompleta (entre 4 y 19 bytes) | `EOFException` al leer versión, longitud o checksum | `ArchivoTruncadoException` |
| Versión desconocida | Mayor que `VERSION_ACTUAL` o menor que 1 | `ArchivoCorruptoException` |
| Archivo truncado a la mitad | El cuerpo real es más corto que la longitud declarada en la cabecera | `ArchivoTruncadoException` |
| Bytes de más al final | El cuerpo real es más largo que la longitud declarada | `ArchivoCorruptoException` |
| Contenido alterado | El CRC32 calculado no coincide con el de la cabecera | `ArchivoCorruptoException` |
| Marcadores de sección o EOF fuera de lugar | Comparación con `0xA1`, `0xA2`, `0xFF` | `ArchivoCorruptoException` |
| Valores fuera de rango | Longitudes negativas, tamaño de fuente, filas/columnas, banderas con bits desconocidos | `ArchivoCorruptoException` |
| Error de disco o permisos | `IOException` de Java | `IOException` |

Todas se muestran al usuario en un cuadro de diálogo desde `BinarioManager.abrirArchivo`, y el documento que estaba abierto no se toca si la lectura falla.

## Proceso de escritura

1. `TextEditor.extractRuns` recorre el `StyledDocument` elemento por elemento y genera los fragmentos.
2. `Tablas.exportTablesData` recorre el mismo documento buscando los componentes insertados y genera una `TablaDatos` por cada tabla, con su offset.
3. `GestorArchivo.escribir` serializa el cuerpo en memoria, calcula longitud y CRC32, escribe la cabecera y luego el cuerpo.

Al abrir, `TextEditor.applyRuns` reconstruye el texto con sus atributos y luego `Tablas.insertarDesdeDatos` reemplaza cada espacio marcador por la tabla correspondiente. Guardar de nuevo el documento recién abierto produce un archivo idéntico byte a byte al original.
