import { defineConfig } from 'vite'
import preact from '@preact/preset-vite'
import svgr from 'vite-plugin-svgr'
import visualizer from 'rollup-plugin-visualizer'

// https://vite.dev/config/
export default defineConfig({
  plugins: [svgr(), preact(), visualizer({ open: true })],
  resolve: {
    alias: {
      '@': '/src',
      lodash: 'lodash-es',
    },
  },
  server: {
    // required for dev auto-refresh in WSL
    watch: {
      ignored: ['/node_modules/**', '/dist/**'],
      usePolling: true,
    },
    warmup: {
      clientFiles: ['./src/**'],
    },
  },
})
