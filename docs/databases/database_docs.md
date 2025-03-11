# Tài liệu hướng dẫn cơ sở dữ liệu - Hệ thống quản lý nhà máy nước sạch

## Mục lục
1. [Giới thiệu](#1-giới-thiệu)
2. [Sơ đồ quan hệ](#2-sơ-đồ-quan-hệ)
3. [Mô tả chi tiết các bảng](#3-mô-tả-chi-tiết-các-bảng)
4. [Hệ thống quản lý thanh toán dư nợ](#4-hệ-thống-quản-lý-thanh-toán-dư-nợ)
5. [Queries phổ biến](#5-queries-phổ-biến)
6. [Quy ước và lưu ý](#6-quy-ước-và-lưu-ý)
7. [Tối ưu hóa hiệu suất](#7-tối-ưu-hóa-hiệu-suất)

## 1. Giới thiệu

Cơ sở dữ liệu của hệ thống quản lý nhà máy nước sạch được thiết kế để quản lý toàn diện các hoạt động của doanh nghiệp cấp nước, từ việc quản lý khách hàng, đồng hồ nước, chỉ số tiêu thụ, hóa đơn, thanh toán đến quản lý nhà máy, thiết bị và sự cố.

### Công nghệ sử dụng:
- PostgreSQL: RDBMS chính cho hệ thống
- Kiểu dữ liệu JSONB: Lưu trữ dữ liệu động và có cấu trúc phức tạp
- Indexes: Tối ưu hiệu suất truy vấn

### Môi trường:
- Production: [Thông tin server sản xuất]
- Staging: [Thông tin server staging]
- Development: [Thông tin server phát triển]

## 2. Sơ đồ quan hệ

![Sơ đồ quan hệ cơ sở dữ liệu]

Các nhóm bảng chính:

1. **Quản lý người dùng và phân quyền**:
   - `permissions`, `roles`, `role_permission`, `users`, `user_role`

2. **Thông tin cơ bản**:
   - `informations`, `addresses`

3. **Quản lý khách hàng**:
   - `customers`

4. **Quản lý nhà máy và phân phối**:
   - `water_plants`, `distribution_zones`

5. **Quản lý đồng hồ nước**:
   - `meters`

6. **Quản lý thiết bị**:
   - `equipment`, `maintenance_records`

7. **Quản lý công việc**:
   - `tasks`

8. **Quản lý chỉ số và hóa đơn**:
   - `read_histories`, `tariffs`, `bills`, `payment_histories`, `payment_bill_allocations`, `customer_debt_history`

9. **Quản lý sự cố và báo cáo**:
   - `incidents`, `reports`, `notifications`

## 3. Mô tả chi tiết các bảng

### 3.1 Quản lý người dùng và phân quyền

#### Bảng `permissions`
Lưu trữ các quyền hạn trong hệ thống.
| Cột | Kiểu dữ liệu | Mô tả |
|-----|--------------|-------|
| id | SERIAL | Khóa chính |
| name | VARCHAR(100) | Tên quyền hạn |
| description | TEXT | Mô tả quyền hạn |

#### Bảng `roles`
Định nghĩa các vai trò trong hệ thống.
| Cột | Kiểu dữ liệu | Mô tả |
|-----|--------------|-------|
| id | SERIAL | Khóa chính |
| name | VARCHAR(100) | Tên vai trò |
| attributes | JSONB | Thuộc tính bổ sung của vai trò |

#### Bảng `role_permission`
Mối quan hệ nhiều-nhiều giữa vai trò và quyền hạn.
| Cột | Kiểu dữ liệu | Mô tả |
|-----|--------------|-------|
| id | SERIAL | Khóa chính |
| role_id | INTEGER | Khóa ngoại đến bảng roles |
| permission_id | INTEGER | Khóa ngoại đến bảng permissions |

#### Bảng `users`
Thông tin người dùng hệ thống (nhân viên).
| Cột | Kiểu dữ liệu | Mô tả |
|-----|--------------|-------|
| id | SERIAL | Khóa chính |
| information_id | INTEGER | Khóa ngoại đến bảng informations |
| role_id | INTEGER | Khóa ngoại đến bảng roles |
| employee_id | VARCHAR(50) | Mã nhân viên |
| department | VARCHAR(100) | Phòng ban |
| position | VARCHAR(100) | Chức vụ |
| hire_date | DATE | Ngày tuyển dụng |
| employment_status | VARCHAR(50) | Trạng thái làm việc |

#### Bảng `user_role`
Mối quan hệ nhiều-nhiều giữa người dùng và vai trò.
| Cột | Kiểu dữ liệu | Mô tả |
|-----|--------------|-------|
| id | SERIAL | Khóa chính |
| user_id | INTEGER | Khóa ngoại đến bảng users |
| role_id | INTEGER | Khóa ngoại đến bảng roles |

### 3.2 Thông tin cơ bản

#### Bảng `informations`
Thông tin chi tiết về cá nhân (nhân viên, khách hàng).
| Cột | Kiểu dữ liệu | Mô tả |
|-----|--------------|-------|
| id | SERIAL | Khóa chính |
| indentity_number | VARCHAR(50) | Số CMND/CCCD |
| full_name | VARCHAR(255) | Họ và tên |
| date_of_birth | DATE | Ngày sinh |
| email | VARCHAR(255) | Email |
| phone_number | VARCHAR(20) | Số điện thoại |
| address_id | INTEGER | Khóa ngoại đến bảng addresses |
| ... | ... | ... |

#### Bảng `addresses`
Quản lý địa chỉ với hỗ trợ phân cấp hành chính.
| Cột | Kiểu dữ liệu | Mô tả |
|-----|--------------|-------|
| id | SERIAL | Khóa chính |
| name | VARCHAR(255) | Tên địa chỉ |
| parent_id | INTEGER | Khóa ngoại đến chính bảng addresses (self-reference) |
| type | VARCHAR(50) | Loại địa chỉ |
| city | VARCHAR(100) | Thành phố |
| district | VARCHAR(100) | Quận/Huyện |
| ward | VARCHAR(100) | Phường/Xã |
| ... | ... | ... |

### 3.3 Quản lý khách hàng

#### Bảng `customers`
Thông tin khách hàng sử dụng nước.
| Cột | Kiểu dữ liệu | Mô tả |
|-----|--------------|-------|
| id | SERIAL | Khóa chính |
| customer_code | VARCHAR(50) | Mã khách hàng |
| information_id | INTEGER | Khóa ngoại đến bảng informations |
| customer_type | VARCHAR(100) | Loại khách hàng (hộ gia đình, doanh nghiệp...) |
| contract_number | VARCHAR(100) | Số hợp đồng |
| contract_start_date | DATE | Ngày bắt đầu hợp đồng |
| current_debt | DECIMAL(15, 2) | Dư nợ hiện tại của khách hàng |
| credit_limit | DECIMAL(15, 2) | Hạn mức dư nợ tối đa |
| is_active | BOOLEAN | Trạng thái hoạt động |
| credit_score | INTEGER | Điểm tín dụng của khách hàng |
| ... | ... | ... |

### 3.4 Quản lý nhà máy và phân phối

#### Bảng `water_plants`
Thông tin về nhà máy nước sạch.
| Cột | Kiểu dữ liệu | Mô tả |
|-----|--------------|-------|
| id | SERIAL | Khóa chính |
| name | VARCHAR(255) | Tên nhà máy |
| code | VARCHAR(50) | Mã nhà máy |
| capacity | DECIMAL(15, 2) | Công suất (m3/ngày) |
| ... | ... | ... |

#### Bảng `distribution_zones`
Các khu vực phân phối nước.
| Cột | Kiểu dữ liệu | Mô tả |
|-----|--------------|-------|
| id | SERIAL | Khóa chính |
| name | VARCHAR(255) | Tên khu vực |
| code | VARCHAR(50) | Mã khu vực |
| plant_id | INTEGER | Khóa ngoại đến bảng water_plants |
| ... | ... | ... |

### 3.5 Quản lý hóa đơn và thanh toán

#### Bảng `bills`
Thông tin hóa đơn tiền nước.
| Cột | Kiểu dữ liệu | Mô tả |
|-----|--------------|-------|
| id | SERIAL | Khóa chính |
| bill_number | VARCHAR(50) | Số hóa đơn (duy nhất) |
| customer_id | INTEGER | Khóa ngoại đến bảng customers |
| meter_id | INTEGER | Khóa ngoại đến bảng meters |
| reading_id | INTEGER | Khóa ngoại đến bảng read_histories |
| water_volume | DECIMAL(10, 2) | Lượng nước tiêu thụ (m3) |
| total_amount | DECIMAL(10, 2) | Tổng số tiền hóa đơn |
| paid_amount | DECIMAL(10, 2) | Số tiền đã thanh toán |
| remaining_amount | DECIMAL(10, 2) | Số tiền còn lại cần thanh toán |
| billing_period_start | DATE | Ngày bắt đầu kỳ hóa đơn |
| billing_period_end | DATE | Ngày kết thúc kỳ hóa đơn |
| due_date | DATE | Hạn thanh toán |
| payment_status | VARCHAR(50) | Trạng thái thanh toán (unpaid, partially_paid, paid) |
| ... | ... | ... |

#### Bảng `payment_histories`
Lịch sử các khoản thanh toán.
| Cột | Kiểu dữ liệu | Mô tả |
|-----|--------------|-------|
| id | SERIAL | Khóa chính |
| payment_number | VARCHAR(50) | Mã thanh toán (duy nhất) |
| customer_id | INTEGER | Khóa ngoại đến bảng customers |
| amount | DECIMAL(10, 2) | Tổng số tiền thanh toán |
| previous_debt | DECIMAL(10, 2) | Dư nợ trước khi thanh toán |
| remaining_debt | DECIMAL(10, 2) | Dư nợ sau khi thanh toán |
| payment_method | VARCHAR(50) | Phương thức thanh toán |
| payment_date | TIMESTAMP | Thời gian thanh toán |
| ... | ... | ... |

#### Bảng `payment_bill_allocations`
Phân bổ khoản thanh toán cho các hóa đơn.
| Cột | Kiểu dữ liệu | Mô tả |
|-----|--------------|-------|
| id | SERIAL | Khóa chính |
| payment_id | INTEGER | Khóa ngoại đến bảng payment_histories |
| bill_id | INTEGER | Khóa ngoại đến bảng bills |
| allocated_amount | DECIMAL(10, 2) | Số tiền phân bổ cho hóa đơn này |
| created_at | TIMESTAMP | Thời gian tạo |

#### Bảng `customer_debt_history`
Lịch sử thay đổi dư nợ của khách hàng.
| Cột | Kiểu dữ liệu | Mô tả |
|-----|--------------|-------|
| id | SERIAL | Khóa chính |
| customer_id | INTEGER | Khóa ngoại đến bảng customers |
| previous_debt | DECIMAL(10, 2) | Dư nợ trước khi thay đổi |
| new_debt | DECIMAL(10, 2) | Dư nợ sau khi thay đổi |
| change_amount | DECIMAL(10, 2) | Số tiền thay đổi |
| change_type | VARCHAR(50) | Loại thay đổi (bill_issued, payment, adjustment) |
| reference_id | INTEGER | ID tham chiếu (hóa đơn hoặc thanh toán) |
| reference_type | VARCHAR(50) | Loại tham chiếu (bill, payment) |
| description | TEXT | Mô tả |
| created_at | TIMESTAMP | Thời gian tạo |

### Các bảng còn lại được mô tả tương tự theo cấu trúc trên...

## 4. Hệ thống quản lý thanh toán dư nợ

Hệ thống quản lý thanh toán dư nợ cho phép khách hàng có thể thanh toán một phần hoặc toàn bộ hóa đơn, đồng thời theo dõi và quản lý dư nợ trong giới hạn cho phép.

### 4.1 Mô hình dữ liệu thanh toán dư nợ

Hệ thống thanh toán dư nợ bao gồm các thành phần chính:

1. **Theo dõi dư nợ khách hàng**:
   - Bảng `customers` lưu trữ thông tin `current_debt` (dư nợ hiện tại) và `credit_limit` (hạn mức dư nợ) của khách hàng
   - Mỗi khách hàng có một hạn mức dư nợ tối đa (mặc định 1 triệu đồng)

2. **Hóa đơn và thanh toán từng phần**:
   - Bảng `bills` theo dõi số tiền đã thanh toán (`paid_amount`) và số tiền còn lại (`remaining_amount`) của mỗi hóa đơn
   - Một hóa đơn có thể ở trạng thái `unpaid` (chưa thanh toán), `partially_paid` (thanh toán một phần), hoặc `paid` (đã thanh toán)

3. **Phân bổ thanh toán**:
   - Một khoản thanh toán (bảng `payment_histories`) có thể được phân bổ cho nhiều hóa đơn
   - Bảng `payment_bill_allocations` liên kết khoản thanh toán với các hóa đơn và số tiền được phân bổ

4. **Lịch sử dư nợ**:
   - Bảng `customer_debt_history` ghi lại lịch sử thay đổi dư nợ của khách hàng

### 4.2 Quy trình thanh toán dư nợ

#### Phát sinh hóa đơn:
1. Khi phát sinh hóa đơn mới, tăng `current_debt` của khách hàng
2. Cập nhật `customer_debt_history` với loại thay đổi `bill_issued`

#### Thanh toán hóa đơn:
1. Tạo bản ghi trong `payment_histories` với số tiền thanh toán
2. Phân bổ thanh toán cho các hóa đơn theo quy tắc:
   - Ưu tiên thanh toán hóa đơn cũ nhất trước (FIFO)
   - Tạo bản ghi trong `payment_bill_allocations` cho mỗi hóa đơn được thanh toán
3. Cập nhật `paid_amount` và `remaining_amount` của mỗi hóa đơn
4. Cập nhật `payment_status` của mỗi hóa đơn tùy theo tình trạng thanh toán
5. Giảm `current_debt` của khách hàng
6. Cập nhật `customer_debt_history` với loại thay đổi `payment`

#### Kiểm soát hạn mức dư nợ:
1. Trước khi phát sinh hóa đơn mới, kiểm tra `current_debt` của khách hàng
2. Nếu `current_debt + new_bill_amount > credit_limit`, yêu cầu khách hàng thanh toán một phần
3. Số tiền tối thiểu cần thanh toán = `current_debt + new_bill_amount - credit_limit`

### 4.3 Ví dụ thực tế

**Tháng 1:**
- Khách hàng A sử dụng nước, phát sinh hóa đơn 1 triệu đồng
- A thanh toán 500k:
  - Dư nợ giảm từ 1 triệu xuống 500k
  - Hóa đơn tháng 1 chuyển sang trạng thái "partially_paid"

**Tháng 2:**
- A sử dụng nước, phát sinh hóa đơn 2 triệu đồng
- Tổng dư nợ = 500k (tháng 1) + 2 triệu (tháng 2) = 2.5 triệu
- Do vượt hạn mức dư nợ (1 triệu), A cần thanh toán ít nhất 1.5 triệu để đưa dư nợ về mức cho phép
- Khi A thanh toán 1.5 triệu:
  - 500k được phân bổ để thanh toán hết hóa đơn tháng 1
  - 1 triệu thanh toán một phần hóa đơn tháng 2
  - Dư nợ giảm xuống còn 1 triệu

## 5. Queries phổ biến

### 5.1 Lấy thông tin khách hàng và dư nợ
```sql
SELECT c.id, c.customer_code, i.full_name, i.phone_number, 
       a.name as address, c.current_debt, c.credit_limit
FROM customers c
JOIN informations i ON c.information_id = i.id
LEFT JOIN addresses a ON i.address_id = a.id
WHERE c.customer_code = 'KH001';
```

### 5.2 Lấy danh sách hóa đơn chưa thanh toán hoặc thanh toán một phần
```sql
SELECT b.id, b.bill_number, c.customer_code, i.full_name,
       b.total_amount, b.paid_amount, b.remaining_amount,
       b.due_date, b.payment_status
FROM bills b
JOIN customers c ON b.customer_id = c.id
JOIN informations i ON c.information_id = i.id
WHERE b.payment_status IN ('unpaid', 'partially_paid')
ORDER BY b.due_date ASC;
```

### 5.3 Lấy lịch sử thanh toán của khách hàng
```sql
SELECT ph.id, ph.payment_number, ph.amount, ph.payment_method, 
       ph.payment_date, ph.previous_debt, ph.remaining_debt
FROM payment_histories ph
WHERE ph.customer_id = 1
ORDER BY ph.payment_date DESC;
```

### 5.4 Lấy chi tiết phân bổ thanh toán
```sql
SELECT ph.payment_number, ph.amount as total_payment_amount,
       b.bill_number, pba.allocated_amount,
       b.billing_period_start, b.billing_period_end
FROM payment_bill_allocations pba
JOIN payment_histories ph ON pba.payment_id = ph.id
JOIN bills b ON pba.bill_id = b.id
WHERE ph.id = 1;
```

### 5.5 Thêm thanh toán mới và phân bổ cho các hóa đơn
```sql
-- Bắt đầu transaction
BEGIN;

-- 1. Thêm thanh toán mới
INSERT INTO payment_histories (
    payment_number, customer_id, amount, 
    previous_debt, remaining_debt, payment_method, payment_date
)
VALUES (
    'PMT-2023-0001', -- payment_number
    1, -- customer_id
    1500000, -- amount
    2500000, -- previous_debt
    1000000, -- remaining_debt
    'cash', -- payment_method
    CURRENT_TIMESTAMP
) RETURNING id INTO payment_id;

-- 2. Phân bổ thanh toán cho hóa đơn cũ (tháng 1)
INSERT INTO payment_bill_allocations (
    payment_id, bill_id, allocated_amount
)
VALUES (
    payment_id, -- payment_id
    1, -- bill_id (hóa đơn tháng 1)
    500000 -- allocated_amount
);

-- 3. Cập nhật trạng thái hóa đơn tháng 1
UPDATE bills
SET paid_amount = total_amount,
    remaining_amount = 0,
    payment_status = 'paid',
    updated_at = CURRENT_TIMESTAMP
WHERE id = 1;

-- 4. Phân bổ thanh toán cho hóa đơn mới (tháng 2)
INSERT INTO payment_bill_allocations (
    payment_id, bill_id, allocated_amount
)
VALUES (
    payment_id, -- payment_id
    2, -- bill_id (hóa đơn tháng 2)
    1000000 -- allocated_amount
);

-- 5. Cập nhật trạng thái hóa đơn tháng 2
UPDATE bills
SET paid_amount = 1000000,
    remaining_amount = 1000000,
    payment_status = 'partially_paid',
    updated_at = CURRENT_TIMESTAMP
WHERE id = 2;

-- 6. Cập nhật dư nợ khách hàng
UPDATE customers
SET current_debt = 1000000,
    updated_at = CURRENT_TIMESTAMP
WHERE id = 1;

-- 7. Ghi lại lịch sử dư nợ
INSERT INTO customer_debt_history (
    customer_id, previous_debt, new_debt, change_amount,
    change_type, reference_id, reference_type, description
)
VALUES (
    1, -- customer_id
    2500000, -- previous_debt
    1000000, -- new_debt
    -1500000, -- change_amount (negative for reduction)
    'payment', -- change_type
    payment_id, -- reference_id
    'payment', -- reference_type
    'Thanh toán hóa đơn tháng 1 và một phần tháng 2'
);

-- Kết thúc transaction
COMMIT;
```

## 6. Quy ước và lưu ý

### 6.1 Quy ước đặt tên
- **Bảng**: Sử dụng danh từ số nhiều, chữ thường, snake_case (ví dụ: `customers`, `read_histories`)
- **Cột**: Sử dụng snake_case, chữ thường (ví dụ: `customer_id`, `created_at`)
- **Khoá chính**: Mặc định là `id` cho tất cả các bảng
- **Khoá ngoại**: Đặt tên theo cấu trúc `<tên_bảng_tham_chiếu số ít>_id` (ví dụ: `customer_id`, `meter_id`)

### 6.2 Quy ước kiểu dữ liệu
- **Số định danh**: `SERIAL` hoặc `INTEGER`
- **Mã/Code**: `VARCHAR(50)`
- **Tên/Tiêu đề**: `VARCHAR(255)`
- **Mô tả ngắn**: `VARCHAR(500)` hoặc `TEXT`
- **Nội dung dài**: `TEXT`
- **Tiền, số lượng**: `DECIMAL(10, 2)` hoặc `DECIMAL(15, 2)` cho số lớn
- **Ngày tháng**: `DATE` hoặc `TIMESTAMP`
- **Trạng thái**: `VARCHAR(50)`
- **Dữ liệu có cấu trúc phức tạp**: `JSONB`

### 6.3 Lưu ý quan trọng về thanh toán dư nợ
- **Tính toàn vẹn dữ liệu**: Sử dụng transaction cho mọi thao tác thanh toán
- **Phân bổ thanh toán**: Luôn ưu tiên thanh toán hóa đơn cũ nhất trước (FIFO)
- **Kiểm tra hạn mức**: Thường xuyên kiểm tra dư nợ của khách hàng trước khi phát hành hóa đơn mới
- **Thống nhất trạng thái**: Đảm bảo trạng thái payment_status của hóa đơn luôn phản ánh đúng thực tế thanh toán
- **Backup dữ liệu**: Đặc biệt quan trọng đối với dữ liệu tài chính

## 7. Tối ưu hóa hiệu suất

### 7.1 Indexes đã tạo
- Tất cả các cột khóa ngoại đều đã được đánh index (ví dụ: `idx_meters_customer_id`)
- Cột trạng thái và số tiền thanh toán: `idx_bills_payment_status`, `idx_bills_remaining_amount`
- Dư nợ khách hàng: `idx_customers_current_debt`
- Phân bổ thanh toán: `idx_payment_bill_allocations_payment_id`, `idx_payment_bill_allocations_bill_id`
- Lịch sử dư nợ: `idx_customer_debt_history_customer_id`

### 7.2 Các truy vấn cần chú ý hiệu năng
- Truy vấn báo cáo dư nợ có thể chậm với dữ liệu lớn, cần sử dụng phân trang và lọc
- Truy vấn thống kê thanh toán nên được cache
- Thao tác phân bổ thanh toán cho nhiều hóa đơn nên được thực hiện trong transaction

### 7.3 Bảng lớn
Các bảng sau có thể trở nên rất lớn và cần được tối ưu/phân vùng:
- `payment_histories`: Lưu lịch sử thanh toán (phân vùng theo tháng)
- `payment_bill_allocations`: Lưu phân bổ thanh toán (phân vùng theo tháng)
- `customer_debt_history`: Lưu lịch sử dư nợ (phân vùng theo tháng và khách hàng)

---

Tài liệu này được cập nhật lần cuối: [Ngày hiện tại]

Liên hệ: [Thông tin liên hệ người phụ trách] 