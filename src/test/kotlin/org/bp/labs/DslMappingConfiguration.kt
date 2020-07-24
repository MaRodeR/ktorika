package org.bp.labs

import org.bp.labs.dtomapper.createConfiguration
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.Period
import java.util.*



class DslMappingConfiguration {

    @Test
    internal fun `simple configuration`() {
        createConfiguration {

            CarEntity::class mapTo CarDTO::class withRules {
                mapNulls = false

                CarEntity::dateOfManufacture mapTo CarDTO::age withConverting { value ->
                    value?.let {
                        Period.between(value.toLocalDateWithoutTimezone(), LocalDate.now()).years
                    }
                }
            } withRulesInReverse {
                mapNulls = false
            }
        }
    }
}