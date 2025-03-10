# Standard API Response Structure

This document describes the standard API response structure used throughout the AquaPure application.

## General Structure

All API endpoints return a consistent JSON response structure, including the following fields:

```json
{
  "success": boolean,
  "message": string,
  "code": string,
  "data": any,
  "timestamp": string,
  "errors": string[],
  "validationErrors": object,
  "pageNumber": number,
  "pageSize": number,
  "totalElements": number,
  "totalPages": number
}
```

### Basic Fields

| Field | Data Type | Description |
|-------|-----------|-------------|
| `success` | boolean | Request status (true: success, false: failure) |
| `message` | string | Message from the server |
| `code` | string | Error code (only appears when `success = false`) |
| `data` | any | Data returned from the server |
| `timestamp` | string | Request processing time (format `yyyy-MM-dd HH:mm:ss`) |

### Error Fields

| Field | Data Type | Description |
|-------|-----------|-------------|
| `errors` | string[] | List of error messages |
| `validationErrors` | object | List of validation errors as key-value pairs, where key is the field name and value is the error message |

### Pagination Fields

| Field | Data Type | Description |
|-------|-----------|-------------|
| `pageNumber` | number | Current page number |
| `pageSize` | number | Page size |
| `totalElements` | number | Total number of elements |
| `totalPages` | number | Total number of pages |

## Response Examples

### Success Response

```json
{
  "success": true,
  "message": "Products retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "Water Purifier Model A",
      "description": "Advanced water purification system",
      "price": 1200.00
    },
    {
      "id": 2,
      "name": "Water Filter Cartridge",
      "description": "Replacement cartridge for water purifier",
      "price": 50.00
    }
  ],
  "timestamp": "2025-03-10 10:15:30"
}
```

### Success Response with Pagination

```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "name": "Water Purifier Model A",
      "description": "Advanced water purification system",
      "price": 1200.00
    },
    {
      "id": 2,
      "name": "Water Filter Cartridge",
      "description": "Replacement cartridge for water purifier",
      "price": 50.00
    }
  ],
  "pageNumber": 0,
  "pageSize": 10,
  "totalElements": 52,
  "totalPages": 6,
  "timestamp": "2025-03-10 10:15:30"
}
```

### Error Response

```json
{
  "success": false,
  "message": "Product not found",
  "code": "RESOURCE_NOT_FOUND",
  "timestamp": "2025-03-10 10:15:30"
}
```

### Validation Error Response

```json
{
  "success": false,
  "message": "Validation error",
  "code": "VALIDATION_ERROR",
  "validationErrors": {
    "name": "Name cannot be empty",
    "price": "Price must be greater than 0"
  },
  "timestamp": "2025-03-10 10:15:30"
}
```

## Error Codes (Error Codes)

Below is a list of error codes used in the application:

| Error Code | Description |
|------------|-------------|
| `INTERNAL_SERVER_ERROR` | Internal server error |
| `RESOURCE_NOT_FOUND` | Resource not found |
| `BAD_REQUEST` | Invalid request |
| `VALIDATION_ERROR` | Data validation error |
| `DATA_INTEGRITY_VIOLATION` | Data integrity violation |
| `UNAUTHORIZED` | Unauthorized access |
| `FORBIDDEN` | Access forbidden |

## Usage in Frontend

When handling responses from the API in the frontend, you should always check the `success` field to determine if the request was successful:

```javascript
// Example of handling response with Axios
axios.get('/api/products')
  .then(response => {
    const apiResponse = response.data;
    
    if (apiResponse.success) {
      // Handle successful data
      const products = apiResponse.data;
      // Display products...
    } else {
      // Handle error
      const errorMessage = apiResponse.message;
      const errorCode = apiResponse.code;
      // Display error message...
    }
  })
  .catch(error => {
    // Handle network or server error
    console.error('API Error:', error);
  });
``` 