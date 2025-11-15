# 🏛️ Arquitectura del Sistema

## Índice
1. [Visión General](#visión-general)
2. [Capas de la Aplicación](#capas-de-la-aplicación)
3. [Flujo de Datos](#flujo-de-datos)
4. [Patrones de Diseño](#patrones-de-diseño)
5. [Modelo de Base de Datos](#modelo-de-base-de-datos)
6. [Manejo de Transacciones](#manejo-de-transacciones)
7. [Consideraciones de Diseño](#consideraciones-de-diseño)

---

## 📐 Visión General

Este sistema implementa una **arquitectura en capas** (layered architecture) siguiendo los principios de **separación de responsabilidades** y **bajo acoplamiento**.

```
┌─────────────────────────────────────┐
│       Capa de Presentación          │
│         (main.AppMenu)              │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│       Capa de Servicio              │
│    (service.PedidoService)          │
│   [Lógica de Negocio + TX]          │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│       Capa de Acceso a Datos        │
│      (dao.PedidoDAO/EnvioDAO)       │
│         [CRUD + SQL]                │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│       Capa de Entidades             │
│    (entities.Pedido/Envio)          │
│         [POJOs]                     │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│       Base de Datos MySQL           │
│     (tpi_prog2_pedido_envio)        │
└─────────────────────────────────────┘
```

---

## 🎯 Capas de la Aplicación

### 1. Capa de Entidades (entities)

**Responsabilidad:** Modelar las entidades del dominio.

```java
entities/
├── Pedido.java    // Entidad principal (A)
└── Envio.java     // Entidad relacionada (B)
```

**Características:**
- POJOs (Plain Old Java Objects)
- Sin lógica de negocio
- Getters y Setters
- Constructores completos y vacíos
- toString() sin recursión

**Ejemplo:**
```java
public class Pedido {
    private Long id;
    private String numero;
    private Envio envio;  // Relación 1→1 unidireccional
    // ... getters, setters, constructores
}
```

---

### 2. Capa de Acceso a Datos (dao)

**Responsabilidad:** Interactuar con la base de datos mediante JDBC.

```java
dao/
├── PedidoDAO.java          // Interfaz
├── EnvioDAO.java           // Interfaz
└── impl/
    ├── PedidoDAOImpl.java  // Implementación
    └── EnvioDAOImpl.java   // Implementación
```

**Características:**
- Patrón DAO (Data Access Object)
- Interfaces para abstracción
- PreparedStatement para seguridad
- Connection injection para transacciones
- Try-with-resources para manejo de recursos

**Operaciones CRUD:**
```java
public interface PedidoDAO {
    Integer crear(Pedido a, Connection conn) throws SQLException;
    Pedido buscarPorId(int id, Connection conn) throws SQLException;
    List<Pedido> listarTodos(Connection conn) throws SQLException;
    boolean actualizar(Pedido a, Connection conn) throws SQLException;
    boolean eliminarLogico(int id, Connection conn) throws SQLException;
}
```

**Beneficios:**
- ✅ Reutilización de Connection para transacciones
- ✅ Aislamiento de lógica SQL
- ✅ Fácil testing con mocks
- ✅ Prevención de SQL Injection

---

### 3. Capa de Servicio (service)

**Responsabilidad:** Lógica de negocio y orquestación de transacciones.

```java
service/
├── PedidoService.java          // Interfaz
└── impl/
    └── PedidoServiceImpl.java  // Implementación
```

**Características:**
- Orquestación de múltiples DAOs
- Manejo de transacciones ACID
- Validaciones de negocio
- Propagación de excepciones

**Ejemplo de Transacción:**
```java
@Override
public void crearPedidoCompleto(Pedido pedido, Envio envio) throws Exception {
    Connection conn = null;
    try {
        conn = DatabaseConnection.getConnection();
        conn.setAutoCommit(false);  // Iniciar transacción
        
        // Operación 1: Crear envío
        Integer idEnvio = envioDAO.crear(envio, conn);
        
        // Operación 2: Asociar y crear pedido
        pedido.setEnvio(envio);
        Integer idPedido = pedidoDAO.crear(pedido, conn);
        
        conn.commit();  // ✅ Confirmar
    } catch (Exception e) {
        if (conn != null) conn.rollback();  // ❌ Revertir
        throw e;
    } finally {
        if (conn != null) {
            conn.setAutoCommit(true);
            conn.close();
        }
    }
}
```

---

### 4. Capa de Presentación (main)

**Responsabilidad:** Interacción con el usuario.

```java
main/
└── AppMenu.java  // Menú de consola interactivo
```

**Características:**
- Menú basado en consola
- Validación de entrada
- Manejo de excepciones UI-friendly
- Delegación a capa de servicio

---

### 5. Capa de Configuración (config)

**Responsabilidad:** Configuración de conexión a BD.

```java
config/
└── DatabaseConnection.java
```

**Características:**
- Factory de conexiones
- Centralización de credenciales
- Fácil cambio de configuración

---

## 🔄 Flujo de Datos

### Caso: Crear Pedido Completo

```
Usuario → AppMenu → PedidoService → EnvioDAO → MySQL
                         ↓              ↓
                    PedidoDAO ────────→ MySQL
                         ↓
                    commit() ─────────→ MySQL
```

**Secuencia detallada:**

1. **Usuario** ingresa datos en AppMenu
2. **AppMenu** valida entrada y llama a `service.crearPedidoCompleto()`
3. **PedidoService**:
   - Obtiene Connection
   - Inicia transacción
   - Llama a `envioDAO.crear()`
   - Llama a `pedidoDAO.crear()`
   - Hace commit
4. **DAOs** ejecutan SQL con PreparedStatement
5. **MySQL** persiste datos
6. **Response** fluye de vuelta al usuario

---

## 🎨 Patrones de Diseño

### 1. DAO Pattern
**Propósito:** Separar lógica de acceso a datos.

### 2. Service Layer Pattern
**Propósito:** Encapsular lógica de negocio.

### 3. Factory Pattern
**Propósito:** Crear conexiones a BD (DatabaseConnection).

### 4. Template Method (implícito)
**Propósito:** Estructura común en métodos DAO.

### 5. Dependency Injection (manual)
**Propósito:** Inyectar Connection en DAOs.

---

## 🗄️ Modelo de Base de Datos

### Diagrama ER

```
┌─────────────────┐         ┌─────────────────┐
│     PEDIDO      │    1→1  │      ENVIO      │
├─────────────────┤─────────├─────────────────┤
│ id (PK)         │         │ id (PK)         │
│ eliminado       │         │ eliminado       │
│ numero (UNIQUE) │         │ tracking(UNIQUE)│
│ fecha           │         │ empresa         │
│ clienteNombre   │         │ tipo            │
│ total           │         │ costo           │
│ estado          │         │ fechaDespacho   │
│ id_envio (FK,UK)│─────────│ fechaEstimada   │
└─────────────────┘         │ estado          │
                            └─────────────────┘

UK = UNIQUE KEY (garantiza 1→1)
```

### Restricciones

```sql
-- Foreign Key con UNIQUE para 1→1
ALTER TABLE pedido 
ADD CONSTRAINT fk_pedido_envio
FOREIGN KEY (id_envio) REFERENCES envio(id)
ON DELETE NO ACTION
ON UPDATE CASCADE;

-- UNIQUE constraint
ALTER TABLE pedido 
ADD CONSTRAINT uk_pedido_envio 
UNIQUE (id_envio);
```

### Eliminación Lógica

```sql
-- En lugar de DELETE
UPDATE pedido SET eliminado = TRUE WHERE id = ?;

-- Todos los SELECTs filtran
SELECT * FROM pedido WHERE eliminado = FALSE;
```

**Beneficios:**
- ✅ Preserva integridad referencial
- ✅ Permite auditoría
- ✅ Recuperación de datos
- ✅ Sin violaciones de FK

---

## 🔒 Manejo de Transacciones

### Propiedades ACID

| Propiedad | Implementación |
|-----------|----------------|
| **Atomicity** | `conn.rollback()` en caso de error |
| **Consistency** | Validaciones + FK constraints |
| **Isolation** | Nivel por defecto de MySQL |
| **Durability** | `conn.commit()` persiste cambios |

### Niveles de Aislamiento (por defecto: REPEATABLE READ)

```java
// Si se necesita cambiar:
conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
```

### Manejo de Errores

```java
try {
    // Operaciones
    conn.commit();
} catch (SQLException e) {
    conn.rollback();
    throw new Exception("Error en TX: " + e.getMessage());
} finally {
    conn.setAutoCommit(true);
    conn.close();
}
```

---

## 💡 Consideraciones de Diseño

### 1. Relación 1→1 Unidireccional

**Decisión:** Pedido conoce a Envío, pero no viceversa.

**Razones:**
- ✅ Simplifica el modelo
- ✅ Evita recursión en toString()
- ✅ FK solo en tabla Pedido
- ✅ Queries más simples

### 2. Connection Injection

**Decisión:** Pasar Connection como parámetro a DAOs.

**Razones:**
- ✅ Permite transacciones multi-DAO
- ✅ Control explícito de TX en Service
- ✅ Fácil testing con mocks

### 3. Interfaces para DAOs y Services

**Decisión:** Usar interfaces + implementaciones.

**Razones:**
- ✅ Abstracción
- ✅ Testabilidad
- ✅ Flexibilidad para cambiar implementaciones
- ✅ Principio de inversión de dependencias

### 4. Eliminación Lógica

**Decisión:** Flag `eliminado` en lugar de DELETE.

**Razones:**
- ✅ Auditoría
- ✅ Recuperación de datos
- ✅ Sin violaciones FK
- ✅ Historial completo

---

## 📊 Diagramas Adicionales

### Diagrama de Secuencia: Crear Pedido

```
Usuario    AppMenu    Service    EnvioDAO   PedidoDAO    MySQL
  │          │          │           │          │          │
  │─crear────▶│          │           │          │          │
  │          │─crear────▶│           │          │          │
  │          │          │─begin TX──┼──────────┼─────────▶│
  │          │          │           │          │          │
  │          │          │─crear─────▶│          │          │
  │          │          │           │─INSERT───┼─────────▶│
  │          │          │           │◀─id──────┼──────────│
  │          │          │◀──envio───│          │          │
  │          │          │           │          │          │
  │          │          │───────crear──────────▶│          │
  │          │          │           │          │─INSERT──▶│
  │          │          │           │          │◀─id─────│
  │          │          │◀──────────pedido──────│          │
  │          │          │           │          │          │
  │          │          │─commit TX─┼──────────┼─────────▶│
  │          │◀─OK──────│           │          │          │
  │◀─success─│          │           │          │          │
```

---

## 🚀 Escalabilidad y Mejoras Futuras

### Posibles Mejoras

1. **Connection Pooling** (HikariCP)
2. **ORM** (JPA/Hibernate)
3. **Logging** (SLF4J + Logback)
4. **Testing** (JUnit + Mockito)
5. **API REST** (Spring Boot)
6. **DTO Pattern** para separar entidades de datos de transferencia
7. **Cache** (Redis/Ehcache)
8. **Métricas** (Prometheus)

---

## 📚 Referencias

- [Java JDBC Tutorial](https://docs.oracle.com/javase/tutorial/jdbc/)
- [Martin Fowler - PoEAA](https://martinfowler.com/eaaCatalog/)
- [Clean Architecture - Uncle Bob](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)

---

*Última actualización: Noviembre 2025*
