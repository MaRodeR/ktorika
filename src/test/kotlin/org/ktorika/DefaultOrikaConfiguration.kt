package org.ktorika

import ma.glasnost.orika.CustomConverter
import ma.glasnost.orika.CustomMapper
import ma.glasnost.orika.MapperFactory
import ma.glasnost.orika.MappingContext
import ma.glasnost.orika.impl.ConfigurableMapper
import ma.glasnost.orika.metadata.Property
import ma.glasnost.orika.metadata.Type
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.Period
import java.util.*

class DefaultOrikaConfiguration {

    @Test
    internal fun `default orika`() {
        object : ConfigurableMapper() {

            override fun configure(factory: MapperFactory) {
                super.configure(factory)

                factory.converterFactory.registerConverter("dateOfManufactureToAgeConverter",
                    object : CustomConverter<Date, Int>() {

                        override fun convert(value: Date?, type: Type<out Int>, context: MappingContext): Int =
                            value?.let {
                                Period.between(value.toLocalDateWithoutTimezone(), LocalDate.now()).years
                            } ?: 0
                    })

                factory.classMap(CarEntity::class.java, CarDTO::class.java)
                    .fieldMap("dateOfManufacture", "age").converter("dateOfManufactureToAgeConverter").add()
                    .mapNulls(false)
                    .mapNullsInReverse(false)
                    .byDefault()
                    .register()
            }
        }
    }

    companion object {
        const val CAR_ID_TO_DRIVER_MAP = "carIdToDriverMap"
    }

    /*
    // verbose declaration of custom mappper
                    .customize(object : CustomMapper<CarEntity, CarDTO>() {

                        override fun mapAtoB(carEntity: CarEntity, carDTO: CarDTO, context: MappingContext) {

                            // a lot of repetitive (duplicated) code if I have links by id between entities
                            val drivers = context.getProperty(CAR_ID_TO_DRIVER_MAP)
                            if (drivers is Map<*, *>) {
                                val driverEntity = drivers[carEntity.id] as? DriverEntity
                                if (driverEntity != null) {
                                    carDTO.driver = map(driverEntity, DriverDTO::class.java, context)
                                }
                            }
                        }
                    })
     */
}