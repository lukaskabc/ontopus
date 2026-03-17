import { useState } from 'preact/hooks'
import Menu from '@mui/material/Menu'
import MenuItem from '@mui/material/MenuItem'
import type { OntopusOptionEntry } from '@/model/MuiModelListEntry.ts'
import { IconButton } from '@mui/material'
import MoreVertIcon from '@mui/icons-material/MoreVert'
import { useTranslation } from 'react-i18next'
import { useLocation } from '@/utils/hooks.ts'

interface OntopusOptionsMenuProps {
  pathPrefix: '' | '/artifact'
  options: OntopusOptionEntry[]
  series: string
}

export default function OntopusOptionsMenu({ options, pathPrefix, series }: OntopusOptionsMenuProps) {
  const { t } = useTranslation()
  const { navigate } = useLocation()
  const [anchorEl, setAnchorEl] = useState<HTMLElement | null>(null)
  const isOpen = Boolean(anchorEl)

  const handleMenuOpen = (event: MouseEvent) => {
    setAnchorEl(event.currentTarget as HTMLElement)
  }

  const handleMenuClose = () => {
    setAnchorEl(null)
  }

  const handleOptionClick = (option: OntopusOptionEntry) => {
    handleMenuClose()
    navigate(`${pathPrefix}/options/${option.optionIdentifier}/${encodeURIComponent(series)}`)
  }

  if (options.length < 1) {
    return null
  }

  return (
    <>
      <IconButton onClick={handleMenuOpen}>
        <MoreVertIcon />
      </IconButton>
      <Menu anchorEl={anchorEl} id="ontopus-options-menu" onClose={handleMenuClose} open={isOpen}>
        {options.map((option) => (
          <MenuItem key={option.optionIdentifier} onClick={() => handleOptionClick(option)}>
            {t(option.label)}
          </MenuItem>
        ))}
      </Menu>
    </>
  )
}
