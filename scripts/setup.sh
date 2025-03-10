#!/bin/bash

# Script thiết lập tự động cho dự án AquaPure
# Script này sẽ cài đặt và cấu hình môi trường phát triển cho người mới

# Cài đặt màu sắc cho terminal
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Đường dẫn tới thư mục gốc của dự án
PROJECT_ROOT=$(pwd)

echo -e "${BLUE}=== Thiết lập môi trường phát triển cho dự án AquaPure ===${NC}"
echo ""

# Kiểm tra các yêu cầu cần thiết
echo -e "${BLUE}1. Kiểm tra các công cụ cần thiết...${NC}"

# Kiểm tra Java
if type -p java > /dev/null; then
    JAVA_VER=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [[ "$JAVA_VER" -ge 17 ]]; then
        echo -e "  ${GREEN}✓ Java ${JAVA_VER} đã được cài đặt${NC}"
    else
        echo -e "  ${YELLOW}⚠ Java phiên bản ${JAVA_VER} đã được cài đặt, nhưng khuyến nghị sử dụng Java 21 trở lên${NC}"
    fi
else
    echo -e "  ${RED}✗ Java chưa được cài đặt. Vui lòng cài đặt JDK 21 trở lên${NC}"
    echo "    Xem hướng dẫn tại: https://www.oracle.com/java/technologies/downloads/"
fi

# Kiểm tra Maven
if type -p mvn > /dev/null || [ -f "./mvnw" ]; then
    echo -e "  ${GREEN}✓ Maven đã được cài đặt${NC}"
else
    echo -e "  ${RED}✗ Maven chưa được cài đặt. Dự án có thể sử dụng Maven Wrapper (./mvnw)${NC}"
fi

# Kiểm tra PostgreSQL
if type -p psql > /dev/null; then
    echo -e "  ${GREEN}✓ PostgreSQL đã được cài đặt${NC}"
else
    echo -e "  ${YELLOW}⚠ PostgreSQL chưa được cài đặt trên máy local. Bạn vẫn có thể sử dụng Docker để chạy PostgreSQL${NC}"
fi

# Kiểm tra Node.js và npm (cần thiết cho Prettier)
if type -p node > /dev/null; then
    NODE_VER=$(node -v | cut -d'v' -f2 | cut -d'.' -f1)
    if [[ "$NODE_VER" -ge 14 ]]; then
        echo -e "  ${GREEN}✓ Node.js ${NODE_VER} đã được cài đặt${NC}"
    else
        echo -e "  ${YELLOW}⚠ Node.js phiên bản ${NODE_VER} đã được cài đặt, nhưng khuyến nghị sử dụng Node.js 14 trở lên${NC}"
    fi
    
    if type -p npm > /dev/null; then
        echo -e "  ${GREEN}✓ npm đã được cài đặt${NC}"
    else
        echo -e "  ${YELLOW}⚠ npm chưa được cài đặt mặc dù Node.js đã được cài đặt. Vui lòng kiểm tra lại cài đặt Node.js${NC}"
    fi
else
    echo -e "  ${YELLOW}⚠ Node.js và npm chưa được cài đặt. Bạn sẽ không thể sử dụng Prettier cho định dạng code.${NC}"
    echo "    Xem hướng dẫn tại: https://nodejs.org/"
fi

# Kiểm tra Git
if type -p git > /dev/null; then
    echo -e "  ${GREEN}✓ Git đã được cài đặt${NC}"
else
    echo -e "  ${RED}✗ Git chưa được cài đặt. Vui lòng cài đặt Git để quản lý mã nguồn${NC}"
    echo "    Xem hướng dẫn tại: https://git-scm.com/downloads"
fi

echo ""

# Cài đặt dependencies
echo -e "${BLUE}2. Cài đặt các dependencies...${NC}"

# Cài đặt npm dependencies (nếu npm đã được cài đặt)
if type -p npm > /dev/null; then
    echo "  Cài đặt Prettier và các dependencies khác..."
    npm install
    if [ $? -eq 0 ]; then
        echo -e "  ${GREEN}✓ Cài đặt npm dependencies thành công${NC}"
    else
        echo -e "  ${RED}✗ Không thể cài đặt npm dependencies${NC}"
    fi
else
    echo -e "  ${YELLOW}⚠ Bỏ qua cài đặt npm dependencies vì npm chưa được cài đặt${NC}"
fi

echo ""

# Thiết lập cơ sở dữ liệu (nếu sử dụng Docker)
echo -e "${BLUE}3. Thiết lập cơ sở dữ liệu...${NC}"
echo "  Bạn muốn thiết lập cơ sở dữ liệu bằng Docker không? (y/n)"
read -p "  " USE_DOCKER

if [ "$USE_DOCKER" = "y" ] || [ "$USE_DOCKER" = "Y" ]; then
    if type -p docker > /dev/null && type -p docker-compose > /dev/null; then
        echo "  Khởi động PostgreSQL bằng Docker..."
        docker-compose up -d db
        if [ $? -eq 0 ]; then
            echo -e "  ${GREEN}✓ Khởi động PostgreSQL thành công${NC}"
        else
            echo -e "  ${RED}✗ Không thể khởi động PostgreSQL bằng Docker${NC}"
        fi
    else
        echo -e "  ${RED}✗ Docker hoặc docker-compose chưa được cài đặt. Không thể thiết lập cơ sở dữ liệu bằng Docker${NC}"
        echo "    Xem hướng dẫn cài đặt Docker tại: https://docs.docker.com/get-docker/"
    fi
else
    echo -e "  ${YELLOW}⚠ Bỏ qua thiết lập cơ sở dữ liệu. Vui lòng cấu hình thủ công trong file application.properties${NC}"
fi

echo ""

# Thiết lập Git hooks
echo -e "${BLUE}4. Thiết lập Git hooks...${NC}"
if type -p git > /dev/null; then
    echo "  Cấu hình Git hooks..."
    git config core.hooksPath .githooks
    chmod +x .githooks/pre-commit
    echo -e "  ${GREEN}✓ Cấu hình Git hooks thành công${NC}"
else
    echo -e "  ${YELLOW}⚠ Bỏ qua thiết lập Git hooks vì Git chưa được cài đặt${NC}"
fi

echo ""

# Biên dịch dự án
echo -e "${BLUE}5. Biên dịch dự án...${NC}"
if [ -f "./mvnw" ]; then
    echo "  Đang biên dịch dự án bằng Maven..."
    ./mvnw clean compile
    if [ $? -eq 0 ]; then
        echo -e "  ${GREEN}✓ Biên dịch dự án thành công${NC}"
    else
        echo -e "  ${RED}✗ Biên dịch dự án thất bại${NC}"
    fi
elif type -p mvn > /dev/null; then
    echo "  Đang biên dịch dự án bằng Maven..."
    mvn clean compile
    if [ $? -eq 0 ]; then
        echo -e "  ${GREEN}✓ Biên dịch dự án thành công${NC}"
    else
        echo -e "  ${RED}✗ Biên dịch dự án thất bại${NC}"
    fi
else
    echo -e "  ${RED}✗ Không thể biên dịch dự án vì Maven chưa được cài đặt và không tìm thấy Maven Wrapper (./mvnw)${NC}"
fi

echo ""

# Format code
echo -e "${BLUE}6. Định dạng code...${NC}"
chmod +x scripts/format.sh scripts/check-format.sh
./scripts/format.sh
if [ $? -eq 0 ]; then
    echo -e "  ${GREEN}✓ Định dạng code thành công${NC}"
else
    echo -e "  ${YELLOW}⚠ Có lỗi khi định dạng code. Vui lòng kiểm tra lại${NC}"
fi

echo ""
echo -e "${GREEN}=== Thiết lập hoàn tất! ===${NC}"
echo -e "Bạn có thể khởi động ứng dụng bằng lệnh: ${BLUE}./mvnw spring-boot:run${NC}"
echo -e "Hoặc sử dụng Docker: ${BLUE}docker-compose up -d${NC}"
echo ""
echo -e "Tài liệu API có sẵn tại: ${BLUE}http://localhost:8080/swagger-ui.html${NC}"
echo -e "Tham khảo tài liệu trong file README.md và CODE_STYLE.md để biết thêm chi tiết."
echo "" 