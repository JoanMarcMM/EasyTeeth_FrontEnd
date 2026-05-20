package utils

/**
 * Centralized validation functions for form fields
 * Provides validators for common critical fields: DNI, SSN, Phone, Email, etc.
 */
object Validators {

    /**
     * Validates Spanish DNI (Documento Nacional de Identidad)
     * Format: 8 digits + 1 letter (e.g., 12345678A)
     * Optional: Can include X or Y prefix for foreign residents
     */
    fun isValidDNI(dni: String): Boolean {
        if (dni.isBlank()) return true // DNI is optional
        val cleanDni = dni.uppercase().trim()
        
        // Pattern: Optional X/Y/Z prefix for foreign residents, then 7-8 digits and 1-2 letters
        val dniPattern = "^[XYZ]?\\d{7,8}[A-Z]$".toRegex()
        if (!dniPattern.matches(cleanDni)) return false
        
        // Validate DNI letter (optional but good practice)
        return validateDNILetter(cleanDni)
    }

    /**
     * Validates the check letter of a Spanish DNI
     * Uses the official algorithm: number % 23 = position in letter table
     */
    private fun validateDNILetter(dni: String): Boolean {
        val letters = "TRWAGMYFPDXBNJZSQVHLCKE"
        
        // Extract just the numbers part
        val numbers = dni.filter { it.isDigit() }
        if (numbers.isEmpty()) return false
        
        val numberPart = numbers.toLongOrNull() ?: return false
        val letterIndex = (numberPart % 23).toInt()
        val expectedLetter = letters[letterIndex]
        val actualLetter = dni.last()
        
        return actualLetter == expectedLetter
    }

    /**
     * Validates Spanish SSN (Seguridad Social / Social Security Number)
     * Format: 12 digits (NNNNNNNNNNNN)
     */
    fun isValidSSN(ssn: String): Boolean {
        if (ssn.isBlank()) return true // SSN is optional
        val cleanSsn = ssn.trim().replace("-", "").replace(" ", "")
        
        // Pattern: 12 digits
        val ssnPattern = "^\\d{12}$".toRegex()
        return cleanSsn.matches(ssnPattern)
    }

    /**
     * Validates email address
     * More comprehensive pattern than the original implementation
     * Allows: alphanumeric, dots, hyphens, underscores
     * Requires: @ symbol and valid domain with TLD
     */
    fun isValidEmail(email: String): Boolean {
        if (email.isBlank()) return true // Email is optional
        
        val cleanEmail = email.trim()
        
        // Comprehensive email pattern
        // Local part: alphanumeric, dots, hyphens, underscores, plus sign
        // Domain: alphanumeric, dots, hyphens
        // TLD: 2+ letters
        val emailPattern = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        
        if (!cleanEmail.matches(emailPattern)) return false
        
        // Additional checks
        // - Local part shouldn't start or end with a dot
        val (localPart, domain) = cleanEmail.split("@")
        if (localPart.startsWith(".") || localPart.endsWith(".")) return false
        if (localPart.contains("..")) return false
        if (domain.startsWith("-") || domain.endsWith("-")) return false
        if (domain.contains(".-") || domain.contains("-.")) return false
        
        return true
    }

    /**
     * Validates Spanish phone number
     * Formats accepted:
     * - 9 digits (standard: 6XX XXXXXX or 9XX XXXXXX)
     * - With +34 prefix (international)
     * - With spaces or hyphens as separators
     */
    fun isValidPhoneNumber(phoneNumber: String): Boolean {
        if (phoneNumber.isBlank()) return true // Phone is optional
        
        val cleanPhone = phoneNumber.trim().replace(" ", "").replace("-", "").replace(".", "")
        
        // Remove +34 prefix if present
        val normalizedPhone = if (cleanPhone.startsWith("+34")) {
            cleanPhone.substring(3)
        } else if (cleanPhone.startsWith("0034")) {
            cleanPhone.substring(4)
        } else {
            cleanPhone
        }
        
        // Spanish phone: should be 9 digits starting with 6 or 9
        val phonePattern = "^[6789]\\d{8}$".toRegex()
        return normalizedPhone.matches(phonePattern)
    }

    /**
     * Validates Spanish NIF/CIF (Tax Identification Number)
     * NIF: Same as DNI (for individuals)
     * CIF: Format for companies (e.g., A12345678)
     */
    fun isValidNIF_CIF(nifCif: String): Boolean {
        if (nifCif.isBlank()) return true // NIF/CIF is optional
        
        val clean = nifCif.uppercase().trim()
        
        // Check if it's a DNI (NIF for individuals)
        if (clean.matches("^[XYZ]?\\d{7,8}[A-Z]$".toRegex())) {
            return validateDNILetter(clean)
        }
        
        // Check if it's a CIF (for companies)
        // Format: 1 letter + 7 digits + 1 letter/digit
        val cifPattern = "^[ABCDEFGHJNPQRSUVW]\\d{7}[0-9A-Z]$".toRegex()
        if (!clean.matches(cifPattern)) return false
        
        // Basic CIF validation (without full algorithm)
        return true
    }

    /**
     * Validates bank account number (IBAN or Spanish account)
     * Spanish format: IBAN starting with ES (ES + 22 alphanumeric characters)
     * Or basic validation for 16-20 digits
     */
    fun isValidBankAccountNumber(accountNumber: String): Boolean {
        if (accountNumber.isBlank()) return true // Bank account is optional
        
        val clean = accountNumber.trim().replace(" ", "").replace("-", "").uppercase()
        
        // Check if it's IBAN format (ES + 20 digits)
        if (clean.startsWith("ES")) {
            val ibanPattern = "^ES\\d{20}$".toRegex()
            return clean.matches(ibanPattern)
        }
        
        // Alternative: Basic account number (16-20 digits)
        val basicPattern = "^\\d{16,20}$".toRegex()
        return clean.matches(basicPattern)
    }

    /**
     * Validates postal address (not too strict)
     * Ensures it's not empty and has reasonable length (5-200 chars)
     * Allows letters, numbers, common symbols
     */
    fun isValidAddress(address: String): Boolean {
        if (address.isBlank()) return true // Address is optional
        
        val clean = address.trim()
        
        // Address should be 5-200 characters
        if (clean.length < 5 || clean.length > 200) return false
        
        // Allow letters, numbers, spaces, commas, dots, hyphens, and some special chars
        val addressPattern = "^[A-Za-z0-9\\s,.\\/\\-ªº°]+$".toRegex()
        return clean.matches(addressPattern)
    }

    /**
     * Validates person's name (first name or last name)
     * Allows letters, spaces, hyphens, and common accented characters
     * Should be 2-50 characters
     */
    fun isValidName(name: String): Boolean {
        if (name.isBlank()) return false // Names are required
        
        val clean = name.trim()
        
        // Name should be 2-50 characters
        if (clean.length < 2 || clean.length > 50) return false
        
        // Allow letters (including accented), spaces, hyphens, apostrophes
        val namePattern = "^[A-Za-zÀ-ÿ\\s\\-']+$".toRegex()
        return clean.matches(namePattern)
    }

    /**
     * General purpose field validator for required text fields
     * Ensures non-empty and reasonable length (1-200 chars)
     */
    fun isValidTextField(text: String, minLength: Int = 1, maxLength: Int = 200): Boolean {
        val clean = text.trim()
        return clean.length >= minLength && clean.length <= maxLength
    }

    /**
     * Validates medical field content (healthState, lifeHabits, allergies, medication)
     * Allows up to 2000 characters for comprehensive medical information
     * Allows letters, numbers, common symbols, and line breaks
     */
    fun isValidMedicalField(text: String): Boolean {
        if (text.isBlank()) return true // Medical fields are typically optional
        
        val clean = text.trim()
        
        // Medical fields should not exceed 2000 characters
        if (clean.length > 2000) return false
        
        // Allow letters, numbers, spaces, punctuation, and special medical chars
        // This is quite permissive to allow doctors to write freely
        return true
    }
}
