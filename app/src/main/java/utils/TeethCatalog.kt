package com.example.easyteeth.utils

data class ToothCatalogItem(
    val id: Long,
    val number: Int
)

val upperPermanentLeft = listOf(
    ToothCatalogItem(8, 18),
    ToothCatalogItem(7, 17),
    ToothCatalogItem(6, 16),
    ToothCatalogItem(5, 15),
    ToothCatalogItem(4, 14),
    ToothCatalogItem(3, 13),
    ToothCatalogItem(2, 12),
    ToothCatalogItem(1, 11)
)

val upperPermanentRight = listOf(
    ToothCatalogItem(9, 21),
    ToothCatalogItem(10, 22),
    ToothCatalogItem(11, 23),
    ToothCatalogItem(12, 24),
    ToothCatalogItem(13, 25),
    ToothCatalogItem(14, 26),
    ToothCatalogItem(15, 27),
    ToothCatalogItem(16, 28)
)

val lowerPermanentLeft = listOf(
    ToothCatalogItem(32, 48),
    ToothCatalogItem(31, 47),
    ToothCatalogItem(30, 46),
    ToothCatalogItem(29, 45),
    ToothCatalogItem(28, 44),
    ToothCatalogItem(27, 43),
    ToothCatalogItem(26, 42),
    ToothCatalogItem(25, 41)
)

val lowerPermanentRight = listOf(
    ToothCatalogItem(17, 31),
    ToothCatalogItem(18, 32),
    ToothCatalogItem(19, 33),
    ToothCatalogItem(20, 34),
    ToothCatalogItem(21, 35),
    ToothCatalogItem(22, 36),
    ToothCatalogItem(23, 37),
    ToothCatalogItem(24, 38)
)

val upperChildLeft = listOf(
    ToothCatalogItem(42, 55),
    ToothCatalogItem(41, 54),
    ToothCatalogItem(40, 53),
    ToothCatalogItem(39, 52),
    ToothCatalogItem(38, 51)
)

val upperChildRight = listOf(
    ToothCatalogItem(43, 61),
    ToothCatalogItem(44, 62),
    ToothCatalogItem(45, 63),
    ToothCatalogItem(46, 64),
    ToothCatalogItem(47, 65)
)

val lowerChildLeft = listOf(
    ToothCatalogItem(52, 85),
    ToothCatalogItem(51, 84),
    ToothCatalogItem(50, 83),
    ToothCatalogItem(49, 82),
    ToothCatalogItem(48, 81)
)

val lowerChildRight = listOf(
    ToothCatalogItem(33, 71),
    ToothCatalogItem(34, 72),
    ToothCatalogItem(35, 73),
    ToothCatalogItem(36, 74),
    ToothCatalogItem(37, 75)
)

val allTeethCatalog = listOf(
    *upperPermanentLeft.toTypedArray(),
    *upperPermanentRight.toTypedArray(),
    *lowerPermanentLeft.toTypedArray(),
    *lowerPermanentRight.toTypedArray(),
    *upperChildLeft.toTypedArray(),
    *upperChildRight.toTypedArray(),
    *lowerChildLeft.toTypedArray(),
    *lowerChildRight.toTypedArray()
)