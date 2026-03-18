package com.example.easyteeth.model

data class DocumentRequest(
    val name: String,
    val type: String,
    val file: ByteArray,
    val creationDate: String,
    val patientId: Long
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DocumentRequest

        if (patientId != other.patientId) return false
        if (name != other.name) return false
        if (type != other.type) return false
        if (!file.contentEquals(other.file)) return false
        if (creationDate != other.creationDate) return false

        return true
    }

    override fun hashCode(): Int {
        var result = patientId.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + file.contentHashCode()
        result = 31 * result + creationDate.hashCode()
        return result
    }
}