import { useState } from 'preact/hooks'
import Menu from '@mui/material/Menu'
import MenuItem from '@mui/material/MenuItem'
import type { OntopusOptionEntry } from '@/model/MuiModelListEntry.ts'
import { IconButton } from '@mui/material'
import MoreVertIcon from '@mui/icons-material/MoreVert'
import { useTranslation } from 'react-i18next'

interface OntopusOptionsMenuProps {
  options: OntopusOptionEntry[]
}

export default function OntopusOptionsMenu({ options }: OntopusOptionsMenuProps) {
  const { t } = useTranslation()
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
    // TODO implement option click
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
