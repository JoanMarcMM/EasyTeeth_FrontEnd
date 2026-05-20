package utils

/**
 * Centralized validation functions for form fields
 * Provides validators for common critical fields: DNI, NSS, Phone, Email, etc.
 */
object Validators {

    /**
     * Validates Spanish DNI/NIE (Documento Nacional de Identidad / Número de Identidad de Extranjero)
     * Format validation only - does not check if letter is correct
     * DNI: 8 digits + 1 letter (e.g., 12345678A)
     * NIE: X/Y/Z + 7-8 digits + 1 letter (e.g., X1234567L)
     * Accepts with or without spaces/hyphens
     */
    fun isValidDNI(dni: String): Boolean {
        if (dni.isBlank()) return true // DNI is optional
        // Remove spaces, hyphens and convert to uppercase
        val cleanDni = dni.replace(" ", "").replace("-", "").uppercase().trim()
        
        // Pattern: Either standard DNI (8 digits + letter) or NIE (X/Y/Z + 7-8 digits + letter)
        val dniPattern = "^\\d{8}[A-Z]$".toRegex()
        val niePattern = "^[XYZ]\\d{7,8}[A-Z]$".toRegex()
        
        return dniPattern.matches(cleanDni) || niePattern.matches(cleanDni)
    }

    /**
     * Validates Spanish NSS (Número de Seguridad Social / Social Security Number)
     * Format: 12 digits (NNNNNNNNNNNN)
     */
    fun isValidSSN(ssn: String): Boolean {
        if (ssn.isBlank()) return true // NSS is optional
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
            return true // Format validation only
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
     * Spanish IBAN format: ES + 2 check digits + 20 account digits (24 chars total)
     * Or basic validation for 16-20 digits
     */
    fun isValidBankAccountNumber(accountNumber: String): Boolean {
        if (accountNumber.isBlank()) return true // Bank account is optional
        
        val clean = accountNumber.trim().replace(" ", "").replace("-", "").uppercase()
        
        // Check if it's IBAN format (ES + 22 digits: 2 check + 20 account)
        if (clean.startsWith("ES")) {
            val ibanPattern = "^ES\\d{22}$".toRegex()
            return clean.matches(ibanPattern)
        }
        
        // Alternative: Basic account number (16-20 digits)
        val basicPattern = "^\\d{16,20}$".toRegex()
        return clean.matches(basicPattern)
    }

    /**
     * Validates postal address (not too strict)
     * Allows any non-empty address
     */
    fun isValidAddress(address: String): Boolean {
        // Address is optional - any value is accepted
        return true
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

    // ========== FORMAT FUNCTIONS ==========

    /**
     * Format bank account number with spaces every 4 digits
     * Smart formatting that maintains cursor position by only removing/adding spaces as needed
     * Examples: "1234567890123456" → "1234 5678 9012 3456"
     *          "ES9121000418450200051332" → "ES91 2100 0418 4502 0005 1332"
     */
    fun formatBankAccountNumber(input: String): String {
        // Remove all spaces to get clean input
        val cleanInput = input.replace(" ", "").replace("-", "").uppercase()
        
        // If empty, return as is
        if (cleanInput.isEmpty()) return input
        
        // Format with spaces every 4 characters
        val formatted = cleanInput.chunked(4).joinToString(" ")
        
        return formatted
    }

    /**
     * Format phone number for display
     * Removes existing separators and adds format: 612-345-678 or +34-612-345-678
     */
    fun formatPhoneNumber(input: String): String {
        // Remove existing separators
        val cleanPhone = input.replace(" ", "").replace("-", "").replace(".", "")
        
        if (cleanPhone.isEmpty()) return input
        
        // If starts with +34 or 0034, keep it and format the rest
        if (cleanPhone.startsWith("+34")) {
            val numberPart = cleanPhone.substring(3)
            if (numberPart.length == 9) {
                return "+34-${numberPart.substring(0, 3)}-${numberPart.substring(3, 6)}-${numberPart.substring(6)}"
            }
        } else if (cleanPhone.startsWith("0034")) {
            val numberPart = cleanPhone.substring(4)
            if (numberPart.length == 9) {
                return "0034-${numberPart.substring(0, 3)}-${numberPart.substring(3, 6)}-${numberPart.substring(6)}"
            }
        }
        
        // Standard Spanish format: 612-345-678
        if (cleanPhone.length == 9 && (cleanPhone.startsWith("6") || cleanPhone.startsWith("9"))) {
            return "${cleanPhone.substring(0, 3)}-${cleanPhone.substring(3, 6)}-${cleanPhone.substring(6)}"
        }
        
        return input
    }

    /**
     * Format DNI/NIE with standard format
     * Accepts digits with optional spaces/hyphens and converts to: 12345678-A or X1234567-L
     */
    fun formatDNI(input: String): String {
        val cleanInput = input.replace(" ", "").replace("-", "").uppercase()
        
        if (cleanInput.isEmpty()) return input
        
        // Find where the letters start
        val lastDigitIndex = cleanInput.indexOfLast { it.isDigit() }
        
        if (lastDigitIndex == -1) return input // No digits found
        
        val numberPart = cleanInput.substring(0, lastDigitIndex + 1)
        val letterPart = cleanInput.substring(lastDigitIndex + 1)
        
        return if (letterPart.isNotEmpty()) {
            "$numberPart-$letterPart"
        } else {
            numberPart
        }
    }
}
