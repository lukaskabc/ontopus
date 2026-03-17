import { defineConfig } from 'vite'
import preact from '@preact/preset-vite'
import svgr from 'vite-plugin-svgr'

// https://vite.dev/config/
export default defineConfig({
  plugins: [svgr(), preact()],
  resolve: {
    alias: {
      '@': '/src',
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
