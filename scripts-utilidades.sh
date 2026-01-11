#!/bin/bash

# ============================================
# Scripts de Utilidades - Sistema Financiero
# ============================================

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# ============================================
# Función: Encriptar código único
# ============================================
encrypt_code() {
    echo -e "${BLUE}=== Encriptar Código Único ===${NC}"
    echo -n "Ingrese el código a encriptar (ej: CLI001): "
    read code
    encrypted=$(echo -n "$code" | base64)
    echo -e "${GREEN}Código encriptado: $encrypted${NC}"
    echo ""
}

# ============================================
# Función: Desencriptar código único
# ============================================
decrypt_code() {
    echo -e "${BLUE}=== Desencriptar Código Único ===${NC}"
    echo -n "Ingrese el código encriptado: "
    read encrypted
    decrypted=$(echo "$encrypted" | base64 -d)
    echo -e "${GREEN}Código desencriptado: $decrypted${NC}"
    echo ""
}

# ============================================
# Función: Obtener token OAuth2
# ============================================
get_token() {
    echo -e "${BLUE}=== Obtener Token OAuth2 ===${NC}"
    
    response=$(curl -s -X POST http://localhost:8080/oauth/token \
      -H "Content-Type: application/x-www-form-urlencoded" \
      -d "grant_type=client_credentials&client_id=financial-client&client_secret=financial-secret")
    
    token=$(echo $response | jq -r '.access_token')
    
    if [ "$token" != "null" ] && [ ! -z "$token" ]; then
        echo -e "${GREEN}Token obtenido exitosamente:${NC}"
        echo "$token"
        echo ""
        echo -e "${YELLOW}Guardando token en archivo token.txt${NC}"
        echo "$token" > token.txt
    else
        echo -e "${RED}Error al obtener token${NC}"
        echo "$response"
    fi
    echo ""
}

# ============================================
# Función: Consultar cliente
# ============================================
query_client() {
    echo -e "${BLUE}=== Consultar Cliente ===${NC}"
    
    # Leer token del archivo si existe
    if [ -f token.txt ]; then
        token=$(cat token.txt)
    else
        echo -e "${YELLOW}No se encontró token guardado. Obteniendo nuevo token...${NC}"
        get_token
        token=$(cat token.txt)
    fi
    
    echo -n "Ingrese el código del cliente (ej: CLI001): "
    read code
    
    # Encriptar código
    encrypted=$(echo -n "$code" | base64)
    echo -e "${YELLOW}Código encriptado: $encrypted${NC}"
    
    # Realizar consulta
    echo -e "${YELLOW}Consultando cliente...${NC}"
    
    response=$(curl -s -X GET "http://localhost:8080/api/v1/clientes/info/$encrypted" \
      -H "Authorization: Bearer $token" \
      -H "X-Trace-Id: $(uuidgen)")
    
    echo -e "${GREEN}Respuesta:${NC}"
    echo "$response" | jq '.'
    echo ""
}

# ============================================
# Función: Verificar salud de servicios
# ============================================
check_health() {
    echo -e "${BLUE}=== Estado de Servicios ===${NC}"
    
    services=("BFF:8080" "Cliente:8081" "Productos:8082")
    
    for service in "${services[@]}"; do
        IFS=':' read -r name port <<< "$service"
        
        echo -n "Verificando $name Service (puerto $port)... "
        
        status=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:$port/actuator/health)
        
        if [ "$status" == "200" ]; then
            echo -e "${GREEN}✓ OK${NC}"
        else
            echo -e "${RED}✗ FAIL (HTTP $status)${NC}"
        fi
    done
    echo ""
}

# ============================================
# Función: Ver logs en tiempo real
# ============================================
view_logs() {
    echo -e "${BLUE}=== Ver Logs en Tiempo Real ===${NC}"
    echo "Seleccione el servicio:"
    echo "1. BFF Service"
    echo "2. Cliente Service"
    echo "3. Productos Service"
    echo "4. Todos los servicios"
    echo -n "Opción: "
    read option
    
    case $option in
        1)
            docker-compose logs -f bff-service
            ;;
        2)
            docker-compose logs -f cliente-service
            ;;
        3)
            docker-compose logs -f productos-service
            ;;
        4)
            docker-compose logs -f
            ;;
        *)
            echo -e "${RED}Opción inválida${NC}"
            ;;
    esac
}

# ============================================
# Función: Reconstruir servicios
# ============================================
rebuild_services() {
    echo -e "${BLUE}=== Reconstruir Servicios ===${NC}"
    echo -e "${YELLOW}Deteniendo contenedores...${NC}"
    docker-compose down
    
    echo -e "${YELLOW}Compilando proyectos Maven...${NC}"
    mvn clean package -DskipTests
    
    echo -e "${YELLOW}Construyendo imágenes Docker...${NC}"
    docker-compose build
    
    echo -e "${YELLOW}Iniciando servicios...${NC}"
    docker-compose up -d
    
    echo -e "${GREEN}Servicios reconstruidos exitosamente${NC}"
    echo ""
}

# ============================================
# Función: Limpiar todo
# ============================================
clean_all() {
    echo -e "${BLUE}=== Limpiar Todo ===${NC}"
    echo -e "${RED}Esta acción eliminará todos los contenedores, volúmenes e imágenes.${NC}"
    echo -n "¿Está seguro? (s/n): "
    read confirm
    
    if [ "$confirm" == "s" ] || [ "$confirm" == "S" ]; then
        echo -e "${YELLOW}Limpiando...${NC}"
        docker-compose down -v
        docker system prune -af
        echo -e "${GREEN}Limpieza completada${NC}"
    else
        echo -e "${YELLOW}Operación cancelada${NC}"
    fi
    echo ""
}

# ============================================
# Función: Generar datos de prueba
# ============================================
generate_test_data() {
    echo -e "${BLUE}=== Códigos de Cliente Encriptados ===${NC}"
    
    codes=("CLI001" "CLI002" "CLI003" "CLI004" "CLI005")
    
    for code in "${codes[@]}"; do
        encrypted=$(echo -n "$code" | base64)
        echo "$code -> $encrypted"
    done
    echo ""
}

# ============================================
# Menú Principal
# ============================================
show_menu() {
    echo -e "${BLUE}╔════════════════════════════════════════╗${NC}"
    echo -e "${BLUE}║  Sistema Financiero - Utilidades      ║${NC}"
    echo -e "${BLUE}╚════════════════════════════════════════╝${NC}"
    echo ""
    echo "1.  Encriptar código único"
    echo "2.  Desencriptar código único"
    echo "3.  Obtener token OAuth2"
    echo "4.  Consultar cliente"
    echo "5.  Verificar salud de servicios"
    echo "6.  Ver logs en tiempo real"
    echo "7.  Reconstruir servicios"
    echo "8.  Limpiar todo"
    echo "9.  Generar datos de prueba"
    echo "0.  Salir"
    echo ""
    echo -n "Seleccione una opción: "
}

# ============================================
# Main Loop
# ============================================
main() {
    while true; do
        show_menu
        read option
        echo ""
        
        case $option in
            1) encrypt_code ;;
            2) decrypt_code ;;
            3) get_token ;;
            4) query_client ;;
            5) check_health ;;
            6) view_logs ;;
            7) rebuild_services ;;
            8) clean_all ;;
            9) generate_test_data ;;
            0) 
                echo -e "${GREEN}¡Hasta luego!${NC}"
                exit 0
                ;;
            *)
                echo -e "${RED}Opción inválida${NC}"
                ;;
        esac
        
        echo -e "${YELLOW}Presione Enter para continuar...${NC}"
        read
        clear
    done
}

# Ejecutar programa principal
main