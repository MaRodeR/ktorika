package org.ktorika

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.Period
import java.util.*


class DslMappingConfiguration {

    @Test
    internal fun `simple configuration`() {
        val configuration = createConfiguration {

            CarEntity::class mapTo CarDTO::class with {
                mapByDefaults = true
                mapNulls = true
                mapNullsInReverse = false

                CarEntity::dateOfManufacture mapTo CarDTO::age withAtoB { value ->
                    value?.let {
                        Period.between(value.toLocalDateWithoutTimezone(), LocalDate.now()).years
                    }
                }

                CarEntity::name mapTo CarDTO::model
            }
        }

        val mapper = KConfigurableMapper.create(configuration)

        val carEntity = CarEntity(id = 1, name = "Mazda 3", dateOfManufacture = Date(), driverId = 5, price = 20000.0)
        val carDTO = mapper.map(carEntity, CarDTO::class.java)

        assertNotNull(carDTO)
        assertEquals("Mazda 3", carDTO.model)
        assertEquals(0, carDTO.age)
        assertEquals(20000, carDTO.price)
    }
}