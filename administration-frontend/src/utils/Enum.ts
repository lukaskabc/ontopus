/**
 * Enum alternative implementation for typescript type safety
 * with erasable syntax support.
 *
 * @see https://stackoverflow.com/a/79688572/12690791
 * @author Behemoth
 */

export type Enum<T, Brand extends string> = T & { __brand: Brand }

/**
 * Construct erasable syntax compatible enum.
 *
 * Usage:
 * <code>
 *   <pre>
 *    export const MyEnum = makeEnum({ENUM_VALUE: "enum_value"}, "MyEnum");
 *    export type MyEnum = typeof MyEnum[keyof typeof MyEnum];
 *   </pre>
 * </code>
 *
 * @param obj enum values
 * @param brand the name of the enum
 */
export const makeEnum = <const T extends Record<string, string>>(obj: T, brand: string) => {
  const eObj = Object.fromEntries(Object.entries(obj).map(([k, v]) => [k, v as Enum<typeof v, typeof brand>])) as {
    [K in keyof T]: Enum<T[K], typeof brand>
  }
  const from = (str: string) => {
    const entry = Object.entries(obj).find(([_, v]) => v === str)
    if (entry) {
      return eObj[entry[0] as keyof T]
    }
    return undefined
  }
  return Object.assign({ from }, eObj)
}
