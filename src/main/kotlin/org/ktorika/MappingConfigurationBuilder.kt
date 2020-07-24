package org.ktorika

import ma.glasnost.orika.MappingContext
import kotlin.reflect.*

@DslMarker
annotation class DtoMappingDslMarker

@DtoMappingDslMarker
class Configuration {

    val mappers = mutableListOf<MapperConfiguration<*, *>>()

    @DtoMappingDslMarker
    infix fun <A : Any, B : Any> KClass<A>.mapTo(bClass: KClass<B>): MapperConfiguration<A, B> {
        val mapperConfiguration = MapperConfiguration(this, bClass)
        mappers.add(mapperConfiguration)
        return mapperConfiguration
    }
}

@DtoMappingDslMarker
fun createConfiguration(init: Configuration.() -> Unit): Configuration {
    return Configuration().apply { init() }
}

@DtoMappingDslMarker
class MapperConfiguration<A : Any, B : Any>(
    val aClass: KClass<A>, val bClass: KClass<B>) {

    var mapByDefaults = true
    var mapNulls = false
    var mapNullsInReverse = false
    var fieldRules = mutableListOf<FieldRule<A, B, *, *>>()

    @DtoMappingDslMarker
    infix fun with(init: MapperConfiguration<A, B>.() -> Unit): MapperConfiguration<A, B> {
        this.init()
        return this
    }

    @DtoMappingDslMarker
    infix fun <FA, FB> KProperty1<A, FA>.mapTo(to: KMutableProperty1<B, FB>): FieldRule<A, B, FA, FB> {
        val rule = FieldRule(this, to)
        fieldRules.add(rule)
        return rule
    }
}

@DtoMappingDslMarker
class FieldRule<A, B, FA, FB>(
    val from: KProperty1<A, FA>,
    val to: KMutableProperty1<B, FB>,
    var aToBConverter: ((FA?, MappingContext) -> FB?)? = null,
    var bToAConverter: ((FB?, MappingContext) -> FA?)? = null
) {

    @DtoMappingDslMarker
    infix fun withAtoB(converter: (FA?) -> FB?) {
        this.aToBConverter = { value, _ -> converter(value) }
    }

    @DtoMappingDslMarker
    infix fun withAtoB(converter: (FA?, MappingContext) -> FB?) {
        this.aToBConverter = converter
    }

    @DtoMappingDslMarker
    infix fun withBtoA(converter: (FB?) -> FA?) {
        this.bToAConverter = { value, _ -> converter(value) }
    }

    @DtoMappingDslMarker
    infix fun withBtoA(converter: (FB?, MappingContext) -> FA?) {
        this.bToAConverter = converter
    }
}