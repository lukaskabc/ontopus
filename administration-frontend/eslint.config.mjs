// @ts-check

import eslint from '@eslint/js'
import { defineConfig, globalIgnores } from 'eslint/config'
import tseslint from 'typescript-eslint'
import reactHooks from 'eslint-plugin-react-hooks'

export default defineConfig(
    [globalIgnores(['dist/*'])],
    eslint.configs.recommended,
    tseslint.configs.strict,
    tseslint.configs.stylistic,
    [reactHooks.configs.flat.recommended]
)
