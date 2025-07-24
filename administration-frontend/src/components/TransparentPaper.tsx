import { Paper, type PaperTypeMap } from '@mui/material'
import type { DefaultComponentProps } from '@mui/material/OverridableComponent'

export default function (props: DefaultComponentProps<PaperTypeMap>) {
  return (
    <>
      <Paper {...props} children={null} className={'paper-transparent'}></Paper>
      {props.children}
    </>
  )
}
