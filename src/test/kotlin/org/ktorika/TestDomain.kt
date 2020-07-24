package org.ktorika

import java.time.LocalDate
import java.time.ZoneId
import java.util.*

class CarEntity(
    var id: Long? = null,
    var name: String? = null,
    var price: Double? = null,
    var dateOfManufacture: Date? = null,
    var driverId: Long? = null
) {
    fun getCarName() = name
}

class DriverEntity(val id: Long, val name: String)

class CarDTO(
    var id: Long? = null,
    var model: String? = null,
    var price: Int? = null,
    var age: Int? = null,
    var driver: DriverDTO? = null
)

class DriverDTO(var id: Long? = null, var name: String? = null)

fun Date.toLocalDateWithoutTimezone(): LocalDate =
    toInstant().atZone(ZoneId.of("UTC+00")).toLocalDate()