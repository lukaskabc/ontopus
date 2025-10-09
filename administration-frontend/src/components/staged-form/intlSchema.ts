import type { RJSFSchema } from '@rjsf/utils'
import { defaultTo, isEqual, mapValues, pickBy } from 'lodash'
import type { i18n } from 'i18next'

const getTypeScope = (typeName: string) => `types.${typeName}`
const getPropScope = (scope: string, propName: string) => `${scope}.fields.${propName}`
const tOrNull = (i18n: i18n, key?: string) => (key && i18n.exists(key) ? i18n.t(key) : null)

// Author https://github.com/rjsf-team/react-jsonschema-form/issues/739#issuecomment-443921904
// Recursively localises given json schema object. Original schema is not modified.
//   schema is json schema object
//   scope is path to root of form/type localised text in i18n object. eg 'forms.editUser'
//   i18n is object containing localised text for schema
//   name is default name for root property, leave undefined for root
export default function intlSchema(schema?: RJSFSchema, i18n?: i18n, scope?: string) {
  if (!schema) {
    return undefined
  }
  if (!i18n) {
    return schema
  }
  if (scope == null) {
    if (schema['$translationRoot']) {
      scope = schema['$translationRoot']
    } else {
      scope = ''
    }
  }
  const newSchema = {
    ...schema,
    ...pickBy({
      title: tOrNull(i18n, schema.title) || tOrNull(i18n, `${scope}.title`) || undefined,
      description: tOrNull(i18n, schema.description) || tOrNull(i18n, `${scope}.description`) || undefined,
      help: tOrNull(i18n, schema.help) || tOrNull(i18n, `${scope}.help`) || undefined,
    }),
  }

  if (schema.definitions) {
    newSchema.definitions = mapValues(schema.definitions, (typeSchema, typeName) =>
      intlSchema(typeSchema, i18n, getTypeScope(typeName))
    )
  }

  if (schema.type === 'object') {
    newSchema.properties = mapValues(schema.properties, (propSchema, propName) =>
      intlSchema(propSchema, i18n, getPropScope(scope!, propName))
    )
  }

  if (schema.enum && (!schema.enumNames || isEqual(schema.enum, schema.enumNames))) {
    newSchema.enumNames = schema.enum.map((option: string) =>
      defaultTo(tOrNull(i18n, `${scope}.options.${option}`), option)
    )
  }

  ;['oneOf', 'allOf', 'anyOf'].forEach((option) => {
    if (!schema[option]) {
      return
    }

    if (Array.isArray(schema[option])) {
      newSchema[option] = []
      schema[option].forEach((item: Object, i: number) => {
        if (typeof item === 'object') {
          newSchema[option].push(intlSchema(item, i18n, getPropScope(scope!, i?.toString())))
        } else {
          newSchema[option].push(item)
        }
      })
    } else {
      newSchema[option] = mapValues(schema[option], (propSchema, propName) =>
        intlSchema(propSchema, i18n, getPropScope(scope!, propName))
      )
    }
  })

  if (schema.items && schema.type === 'array') {
    if (schema.items.enum && !schema.items.enumNames) {
      newSchema.items = {
        ...newSchema.items,
        enumNames: schema.items.enum.map((option: string) =>
          defaultTo(tOrNull(i18n, `${scope}.options.${option}`), option)
        ),
      }
    } else if (schema.items.properties && schema.items.type === 'object') {
      newSchema.items = {
        ...newSchema.items,
        properties: mapValues(schema.items.properties, (propSchema, propName) =>
          intlSchema(propSchema, i18n, getPropScope(scope!, propName))
        ),
      }
    }
  }

  return newSchema
}
