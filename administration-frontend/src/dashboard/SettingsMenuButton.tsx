import { useCallback, useEffect, useState } from 'preact/hooks'
import { loadSettingsMenuEntries, type MenuEntry } from '@/dashboard/actions.ts'
import { useLocation } from 'wouter-preact'
import { useTranslation } from 'react-i18next'
import type { TargetedMouseEvent } from 'preact'
import IconButton from '@mui/material/IconButton'
import Menu from '@mui/material/Menu'
import MenuItem from '@mui/material/MenuItem'
import Tooltip from '@mui/material/Tooltip'
import SettingsIcon from '@mui/icons-material/Settings'
import Constants from '@/Constants.ts'

const LOCATION_PREFIX = '~' + Constants.BASE_URL + '/settings/'

export default function SettingsMenuButton() {
  const { t } = useTranslation()
  const [location, navigate] = useLocation()
  const [isLoading, setIsLoading] = useState(true)
  const [menuEntries, setMenuEntries] = useState<MenuEntry[] | null>(null)
  const [menuAnchor, setMenuAnchor] = useState<null | HTMLElement>(null)

  useEffect(() => {
    return loadSettingsMenuEntries()
      .then(setMenuEntries)
      .finally(() => setIsLoading(false)).abort
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
        <span>
          <IconButton aria-label="settings" color="primary" loading={isLoading} onClick={onMenuOpen}>
            <SettingsIcon />
          </IconButton>
        </span>
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
