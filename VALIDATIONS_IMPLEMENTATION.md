# Implementación de Validadores para Formularios Críticos

## Resumen Ejecutivo

Se ha completado la implementación de un sistema centralizado de validadores para garantizar que todos los campos críticos de formularios sigan patrones específicos y sean auténticos. Se han mejorado significativamente las validaciones existentes y se han agregado validaciones nuevas para campos que estaban sin restricciones.

## Cambios Realizados

### 1. Creación de Archivo de Validadores Centralizados
**Archivo:** `app/src/main/java/utils/Validators.kt`

Un objeto Kotlin singleton que proporciona funciones reutilizables para validación de:

#### Validadores Implementados:

- **`isValidDNI(dni: String)`** 
  - Valida DNI español (Documento Nacional de Identidad)
  - Formato: 8 dígitos + 1 letra (ej: 12345678A)
  - Soporta prefijos X/Y/Z para residentes extranjeros
  - Valida el dígito de control usando el algoritmo oficial (número % 23)

- **`isValidSSN(ssn: String)`**
  - Valida Números de Seguridad Social española
  - Formato: 12 dígitos exactos
  - Soporte para formato con guiones (removidos automáticamente)

- **`isValidEmail(email: String)`**
  - Patrón mejorado: `^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$`
  - Previene errores comunes: puntos consecutivos, dominios inválidos
  - Validaciones adicionales en local part y dominio

- **`isValidPhoneNumber(phoneNumber: String)`**
  - Específico para números españoles
  - Formatos aceptados: 9 dígitos (6XX o 9XX), +34 prefix, espacios/guiones como separadores
  - Normaliza automáticamente prefijos internacionales (+34, 0034)

- **`isValidNIF_CIF(nifCif: String)`**
  - Valida tanto NIF (personas físicas) como CIF (empresas)
  - NIF: mismo patrón que DNI
  - CIF: formato para empresas (ej: A12345678)

- **`isValidBankAccountNumber(accountNumber: String)`**
  - Soporta IBAN español: ES + 20 dígitos
  - Alternativa: números de cuenta españoles (16-20 dígitos)
  - Elimina espacios y guiones automáticamente

- **`formatBankAccountNumber(accountNumber: String)`**
  - Formatea números bancarios con espacios cada 4 dígitos
  - Ejemplo: `ES9121000418450200051332` → `ES91 2100 0418 4502 0005 1332`
  - Se aplica al perder el foco del campo (onFocusChanged)

- **`isValidAddress(address: String)`**
  - Longitud: 5-200 caracteres
  - Permite letras, números, acentos, símbolos comunes
  - Previene inyección de código

- **`isValidName(name: String)`**
  - Longitud: 2-50 caracteres
  - Permite letras (incluyendo acentos), espacios, guiones, apóstrofos
  - Validación para nombres propios

- **`isValidMedicalField(text: String)`**
  - Campos médicos (antecedentes, alergias, medicación)
  - Máximo 2000 caracteres
  - Permite escritura libre para campos médicos

- **`isValidTextField(text: String, minLength, maxLength)`**
  - Validador genérico para campos de texto
  - Parámetros configurables de longitud

## Mejoras a Validaciones Existentes

### Email (Anteriormente)
```kotlin
// ❌ Demasiado permisivo - permite puntos consecutivos
val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
```

### Email (Ahora)
```kotlin
// ✅ Más riguroso - previene casos inválidos
val emailPattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
// + validaciones adicionales:
// - No comienza/termina con punto
// - Sin puntos consecutivos
// - Dominio sin guiones al inicio/fin
```

### Phone (Anteriormente)
```kotlin
// ❌ Demasiado genérico - acepta cualquier país
val phonePattern = "^[+]?[0-9]{6,}$"
```

### Phone (Ahora)
```kotlin
// ✅ Específico para España
// - 9 dígitos que comienzan con 6, 7, 8 o 9
// - Soporta +34 y 0034
// - Normaliza automáticamente separadores
```

## Pantallas Actualizadas

### 1. NewPatientScreen.kt
**Cambios:**
- Agregado import: `import utils.Validators`
- Validaciones agregadas (en orden de validación):
  1. Nombre completo (nombre, apellidos)
  2. DNI (campo crítico de identificación)
  3. SSN (campo crítico de identificación)
  4. Email (contacto)
  5. Teléfono (contacto, mejorado)
  6. Dirección de facturación
  7. Número de cuenta bancaria
  8. NIF/CIF (identificación fiscal)

- Removidas funciones locales duplicadas:
  - `isValidEmail()` → ahora usa `Validators.isValidEmail()`
  - `isValidPhoneNumber()` → ahora usa `Validators.isValidPhoneNumber()`

**Campos Validados:**
- `name` ✅ Validación de nombre
- `lastname1` ✅ Validación de apellido
- `lastname2` ✅ Validación de apellido
- `dni` ✅ Validación DNI español
- `ssn` ✅ Validación SSN (12 dígitos)
- `phoneNumber` ✅ Validación teléfono español mejorada
- `email` ✅ Validación email mejorada
- `billingAddress` ✅ Validación de dirección
- `bankAccountNumber` ✅ Validación IBAN/cuenta
- `taxIdentificationNumber` ✅ Validación NIF/CIF

### 2. UpdatePatientScreen.kt
**Cambios:**
- Agregado import: `import utils.Validators`
- Mismas validaciones que NewPatientScreen aplicadas al actualizar
- Validaciones se ejecutan ANTES de hacer la llamada API

### 3. UpdateBackgroundScreen.kt
**Cambios:**
- Agregado import: `import utils.Validators`
- Validaciones para campos médicos:
  - `familyHistory` → Validación de campo médico (máx 2000 caracteres)
  - `healthState` → Validación de campo médico
  - `lifeHabits` → Validación de campo médico
  - `allergies` → Validación de campo médico
  - `medication` → Validación de campo médico

**Prevención de Problemas:**
- Límite de caracteres para prevenir saturación de base de datos
- Permite escritura libre para profesionales médicos

### 4. LoginScreen.kt
**Cambios:**
- Agregado import: `import utils.Validators`
- Validación básica de campos obligatorios:
  - Username: mínimo 3 caracteres
  - Password: mínimo 1 carácter (no puede estar vacío)
- Validaciones se ejecutan ANTES de intentar login

**Beneficios:**
- Retroalimentación inmediata al usuario
- Previene requests innecesarios a servidor

## Beneficios de la Implementación

### 1. **Seguridad Mejorada**
- Validación de datos críticos en cliente
- Prevención de inyección de código
- Formato garantizado para datos sensibles (DNI, SSN, números bancarios)

### 2. **Experiencia de Usuario Mejorada**
- Mensajes de error específicos en catalán
- Validación inmediata sin enviar requests al servidor
- Instrucciones claras sobre formatos esperados

### 3. **Mantenibilidad Mejorada**
- Validadores centralizados en un único archivo
- Fácil de mantener y actualizar
- Reutilizable en múltiples pantallas

### 4. **Confiabilidad de Datos**
- Garantiza que datos críticos sigan patrones específicos
- Reduce errores de entrada de usuario
- Facilita procesamiento en backend

## Especificaciones Técnicas

### Lengua
Todos los mensajes de error están en **catalán** (idioma de la aplicación)

### Comportamiento de Validación
- **Campos Opcionales**: Permiten texto en blanco pero validan si hay contenido
- **Campos Requeridos**: Nombre y apellidos deben tener 2-50 caracteres
- **Casos Límite**: Se manejan espacios en blanco con `.trim()`

### Ejemplo de Validación en Uso

```kotlin
// En NewPatientScreen
if (!Validators.isValidDNI(dni)) {
    errorMessage = "El DNI no és vàlid. Format: 12345678A o X12345678A"
    return@launch
}

if (!Validators.isValidPhoneNumber(phoneNumber)) {
    errorMessage = "El telèfon no és vàlid. Usa format espanyol: 6XX-XXX-XXX, 9XX-XXX-XXX o +34-9XX-XXX-XXX"
    return@launch
}
```

## Pruebas Recomendadas

### 1. Pruebas de DNI
- ✅ `12345678Z` - válido
- ❌ `12345678A` - letra incorrecta
- ❌ `1234567A` - muy pocos dígitos
- ❌ `X1234567L` - válido para extranjero

### 2. Pruebas de Teléfono
- ✅ `612345678` - formato estándar
- ✅ `+34612345678` - con prefijo
- ✅ `612-345-678` - con separadores
- ❌ `5612345678` - comienza con 5 (inválido)
- ❌ `61234567` - 8 dígitos (insuficiente)

### 3. Pruebas de Email
- ✅ `usuario@ejemplo.com` - válido
- ❌ `usuario..nombre@ejemplo.com` - puntos consecutivos
- ❌ `.usuario@ejemplo.com` - comienza con punto
- ❌ `usuario@` - sin dominio

### 4. Pruebas de IBAN
- ✅ `ES9121000418450200051332` - formato IBAN correcto (sin formatear)
- ✅ `ES91 2100 0418 4502 0005 1332` - formato IBAN con espacios (post-validación)
- ✅ `1234567890123456` - 16 dígitos
- ❌ `ES912100041845020005133` - IBAN incompleto

## Historial de Cambios en Rama

Esta implementación fue realizada en la rama **`80-add-mandatory-format-for-every-form`**

Todos los cambios están listos para:
- Revisar en Pull Request
- Mergear a rama principal
- Testear en environment de staging

## Recomendaciones Futuras

1. **Backend Validation**: Agregar las mismas validaciones en backend para seguridad
2. **Visual Feedback**: Agregar validación en tiempo real mientras escribe (live validation)
3. **Regex Patterns File**: Centralizar patrones regex en un archivo de configuración
4. **Validación Multidioma**: Hacer los validadores agnósticos del idioma para internacionalización
5. **Unit Tests**: Agregar pruebas unitarias para cada validador
6. **Formateo Avanzado**: Considerar usar TextFieldValue de Compose para mejor control del cursor durante formateo en tiempo real

---

**Implementado:** Mayo 2026
**Rama:** `92-fix-validator-errors`
**Status:** ✅ Completado sin errores de compilación
**Cambios Recientes:**
- ✅ Agregada función `formatBankAccountNumber()` para formatear IBANs
- ✅ Formateo aplicado al perder el foco (mejor UX)
- ✅ Cursor se mantiene en posición correcta sin desplazamientos
