#!/bin/bash

# Script de configuración para TPI Programación 2
# Sistema de Gestión de Pedidos y Envíos

echo "================================================"
echo "   TPI Programación 2 - Script de Setup"
echo "   Sistema de Gestión de Pedidos y Envíos"
echo "================================================"
echo ""

# Colores para output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Función para imprimir con colores
print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

# Verificar Java
echo "Verificando Java..."
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
    print_success "Java encontrado: versión $JAVA_VERSION"
else
    print_error "Java no encontrado. Por favor instala JDK 21 o superior."
    exit 1
fi

# Verificar MySQL
echo ""
echo "Verificando MySQL..."
if command -v mysql &> /dev/null; then
    MYSQL_VERSION=$(mysql --version | awk '{print $5}' | sed 's/,$//')
    print_success "MySQL encontrado: versión $MYSQL_VERSION"
else
    print_error "MySQL no encontrado. Por favor instala MySQL 8.0 o superior."
    exit 1
fi

# Crear estructura de directorios
echo ""
echo "Creando estructura de directorios..."

mkdir -p src/config
mkdir -p src/entities
mkdir -p src/dao
mkdir -p src/dao/impl
mkdir -p src/service
mkdir -p src/service/impl
mkdir -p src/main
mkdir -p out
mkdir -p lib
mkdir -p docs

print_success "Estructura de directorios creada"

# Verificar MySQL Connector
echo ""
echo "Verificando MySQL Connector/J..."
if [ -f "lib/mysql-connector-j-8.3.0.jar" ]; then
    print_success "MySQL Connector/J encontrado"
else
    print_warning "MySQL Connector/J no encontrado en lib/"
    echo "Descarga el driver desde:"
    echo "https://dev.mysql.com/downloads/connector/j/"
    echo "y colócalo en la carpeta lib/"
fi

# Configurar base de datos
echo ""
read -p "¿Deseas configurar la base de datos ahora? (s/n): " SETUP_DB

if [ "$SETUP_DB" = "s" ] || [ "$SETUP_DB" = "S" ]; then
    echo ""
    read -p "Usuario MySQL: " MYSQL_USER
    read -sp "Contraseña MySQL: " MYSQL_PASS
    echo ""
    
    echo ""
    echo "Creando base de datos..."
    
    if mysql -u "$MYSQL_USER" -p"$MYSQL_PASS" < database.sql 2>/dev/null; then
        print_success "Base de datos creada exitosamente"
        
        echo ""
        echo "Insertando datos de prueba..."
        if mysql -u "$MYSQL_USER" -p"$MYSQL_PASS" < data.sql 2>/dev/null; then
            print_success "Datos de prueba insertados"
        else
            print_error "Error al insertar datos de prueba"
        fi
    else
        print_error "Error al crear la base de datos"
        echo "Por favor verifica tus credenciales y que MySQL esté corriendo"
    fi
    
    # Actualizar DatabaseConnection.java
    echo ""
    echo "Actualizando configuración de conexión..."
    if [ -f "src/config/DatabaseConnection.java" ]; then
        sed -i.bak "s/private static final String USER = \".*\";/private static final String USER = \"$MYSQL_USER\";/" src/config/DatabaseConnection.java
        sed -i.bak "s/private static final String PASS = \".*\";/private static final String PASS = \"$MYSQL_PASS\";/" src/config/DatabaseConnection.java
        print_success "Configuración actualizada en DatabaseConnection.java"
    fi
fi

# Compilar proyecto
echo ""
read -p "¿Deseas compilar el proyecto ahora? (s/n): " COMPILE_NOW

if [ "$COMPILE_NOW" = "s" ] || [ "$COMPILE_NOW" = "S" ]; then
    echo ""
    echo "Compilando proyecto..."
    
    if [ -f "lib/mysql-connector-j-8.3.0.jar" ]; then
        javac -d out -cp "lib/mysql-connector-j-8.3.0.jar" src/**/*.java
        
        if [ $? -eq 0 ]; then
            print_success "Proyecto compilado exitosamente"
            echo ""
            echo "Para ejecutar el proyecto usa:"
            echo "java -cp \"out:lib/mysql-connector-j-8.3.0.jar\" main.AppMenu"
        else
            print_error "Error al compilar el proyecto"
        fi
    else
        print_error "No se puede compilar sin el MySQL Connector/J"
    fi
fi

# Resumen final
echo ""
echo "================================================"
echo "   Setup Completado"
echo "================================================"
echo ""
echo "Próximos pasos:"
echo "1. Verifica que el archivo DatabaseConnection.java tenga las credenciales correctas"
echo "2. Coloca mysql-connector-j-8.3.0.jar en la carpeta lib/ si no está"
echo "3. Compila: javac -d out -cp lib/mysql-connector-j-8.3.0.jar src/**/*.java"
echo "4. Ejecuta: java -cp \"out:lib/mysql-connector-j-8.3.0.jar\" main.AppMenu"
echo ""
print_success "¡Todo listo para comenzar!"
echo ""
