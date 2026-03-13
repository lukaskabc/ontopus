import { IconButton, Menu, MenuItem, Tooltip } from '@mui/material'
import { Settings as SettingsIcon } from '@mui/icons-material'
import { useCallback, useEffect, useState } from 'preact/hooks'
import { loadSettingsMenuEntries, type MenuEntry } from '@/dashboard/actions.ts'
import { useLocation } from 'wouter-preact'
import { useTranslation } from 'react-i18next'
import type { TargetedMouseEvent } from 'preact'

const LOCATION_PREFIX = '~/settings/'

export default function SettingsMenuButton() {
  const { t } = useTranslation()
  const [location, navigate] = useLocation()
  const [isLoading, setIsLoading] = useState(true)
  const [menuEntries, setMenuEntries] = useState<MenuEntry[] | null>(null)
  const [menuAnchor, setMenuAnchor] = useState<null | HTMLElement>(null)

  useEffect(() => {
    loadSettingsMenuEntries()
      .then(setMenuEntries)
      .finally(() => setIsLoading(false))
  }, [setIsLoading, setMenuEntries])

  const onMenuOpen = useCallback(
    (event: TargetedMouseEvent<HTMLElement>) => setMenuAnchor(event.currentTarget),
    [setMenuAnchor]
  )
  const onMenuClose = useCallback(() => setMenuAnchor(null), [setMenuAnchor])
  const onMenuItemClick = useCallback(
    (path: string) => {
      navigate(path)
      onMenuClose()
    },
    [navigate, onMenuClose]
  )

  if (!isLoading && (!menuEntries || menuEntries.length === 0)) {
    return null
  }

  return (
    <>
      <Tooltip title="Settings">
        <IconButton aria-label="settings" color="primary" loading={isLoading} onClick={onMenuOpen}>
          <SettingsIcon />
        </IconButton>
      </Tooltip>
      <Menu
        anchorEl={menuAnchor}
        open={!!menuAnchor}
        onClose={onMenuClose}
        slotProps={{
          paper: {
            style: {
              maxHeight: 48 * 5,
            },
          },
        }}
      >
        {menuEntries &&
          menuEntries.map(([uuid, label]) => {
            const path = LOCATION_PREFIX + uuid
            return (
              <MenuItem
                key={'settings-menu-item-' + uuid}
                onClick={() => onMenuItemClick(path)}
                selected={path === location}
              >
                {t(label)}
              </MenuItem>
            )
          })}
      </Menu>
    </>
  )
}
