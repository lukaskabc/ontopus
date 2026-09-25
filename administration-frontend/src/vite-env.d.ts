/// <reference types="vite/client" />
/// <reference types="vite-plugin-svgr/client" />

interface ImportMetaEnv {
  readonly VITE_ONTOPUS_URL?: string
  readonly VITE_ONTOPUS_VERSION?: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}
