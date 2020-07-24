package org.bp.labs.dtomapper

import ma.glasnost.orika.MappingContext
import kotlin.reflect.*

@DslMarker
annotation class DtoMappingDslMarker

@DtoMappingDslMarker
class Configuration {

    private val mappers = mutableListOf<MapperConfiguration<*, *>>()

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
    val aClass: KClass<A>, val bClass: KClass<B>,
    var directMappingRules: MappingRules<A, B>? = null,
    var reverseMappingRules: MappingRules<B, A>? = null
) {

    @DtoMappingDslMarker
    infix fun withRules(init: MappingRules<A, B>.() -> Unit): MapperConfiguration<A, B> {
        val rules = MappingRules(aClass, bClass)
        rules.init()
        directMappingRules = rules
        return this
    }

    @DtoMappingDslMarker
    infix fun withRulesInReverse(init: MappingRules<B, A>.() -> Unit): MapperConfiguration<A, B> {
        val rules = MappingRules(bClass, aClass)
        rules.init()
        reverseMappingRules = rules
        return this
    }

}

@DtoMappingDslMarker
class MappingRules<A : Any, B : Any>(val fromClass: KClass<A>, val toClass: KClass<B>) {

    var mapByDefaults = true
    var mapNulls = false
    var fieldRules = mutableListOf<FieldRule<*, *, *, *>>()

    @DtoMappingDslMarker
    infix fun <FA, FB> KProperty1<A, FA>.mapTo(to: KMutableProperty1<B, FB>): FieldRule<A, B, FA, FB> {
        val rule = FieldRule<A, B, FA, FB>()
        fieldRules.add(rule)
        return rule
    }
}

@DtoMappingDslMarker
class FieldRule<A, B, FA, FB>(private var converter: ((FA?, MappingContext) -> FB?)? = null) {

    @DtoMappingDslMarker
    infix fun withConverting(converter: (FA?) -> FB?) {
        this.converter = { value, _ -> converter(value) }
    }

    @DtoMappingDslMarker
    infix fun withConverting(converter: (FA?, MappingContext) -> FB?) {
        this.converter = converter
    }
}