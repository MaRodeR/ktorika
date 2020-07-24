package org.ktorika

import ma.glasnost.orika.*
import ma.glasnost.orika.impl.ConfigurableMapper
import ma.glasnost.orika.metadata.ClassMapBuilder
import ma.glasnost.orika.metadata.Type

class KConfigurableMapper private constructor(private val configuration: Configuration) : ConfigurableMapper(false) {
    init {
        super.init()
    }

    override fun configure(factory: MapperFactory) {
        super.configure(factory)
        configuration.mappers.forEach { mapper ->
            factory.createClassMapBy(mapper)
                .register()
        }
    }

    private fun <A : Any, B : Any> MapperFactory.createClassMapBy(mapper: MapperConfiguration<A, B>): ClassMapBuilder<A, B> {
        val classMap: ClassMapBuilder<A, B> = classMap(mapper.aClass.java, mapper.bClass.java)

        mapper.fieldRules.forEach { rule ->
            val fieldMap = classMap.fieldMap(rule.from.name, rule.to.name)

            val aToBConverter: ((Nothing?, MappingContext) -> Any?)? = rule.aToBConverter
            if (aToBConverter != null) {
                val converterId =
                    "${mapper.aClass.simpleName}.${rule.from.name}-${mapper.bClass.simpleName}.${rule.to.name}"
                registerConverter(converterId, aToBConverter)
                fieldMap.aToB().converter(converterId).add()
            }

            val bToAConverter = rule.bToAConverter
            if (bToAConverter != null) {
                val converterId =
                    "${mapper.bClass.simpleName}.${rule.to.name}-${mapper.aClass.simpleName}.${rule.from.name}"
                registerConverter(converterId, bToAConverter)
                fieldMap.bToA().converter(converterId).add()
            }
            fieldMap.add()
        }

        if (mapper.mapByDefaults) {
            classMap.byDefault()
        }
        classMap.mapNulls(mapper.mapNulls)
        classMap.mapNullsInReverse(mapper.mapNullsInReverse)
        return classMap
    }

    private fun MapperFactory.registerConverter(
        converterId: String,
        bToAConverter: (Nothing?, MappingContext) -> Any?
    ) {
        converterFactory.registerConverter(converterId, object : CustomConverter<Any, Any>() {
            override fun convert(value: Any?, type: Type<out Any>, mappingContext: MappingContext): Any? {
                return (bToAConverter as (Any?, MappingContext) -> Any?)(value, mappingContext)
            }
        })
    }

    companion object {
        fun create(configuration: Configuration): KConfigurableMapper {
            return KConfigurableMapper(configuration)
        }
    }
}