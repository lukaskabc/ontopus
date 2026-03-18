import { type PaperTypeMap } from '@mui/material'
import type { DefaultComponentProps } from '@mui/material/OverridableComponent'
import Paper from '@mui/material/Paper'

export default function (props: DefaultComponentProps<PaperTypeMap>) {
  return (
    <>
      <Paper {...props} children={null} className={'paper-transparent'}></Paper>
      {props.children}
    </>
  )
}
