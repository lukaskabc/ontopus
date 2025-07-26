import Button from '@mui/material/Button'
import Dialog from '@mui/material/Dialog'
import DialogTitle from '@mui/material/DialogTitle'
import DialogContent from '@mui/material/DialogContent'
import DialogActions from '@mui/material/DialogActions'
import type { DialogProps } from '@toolpad/core'
import Form from '@rjsf/mui'
import type { RJSFSchema, UiSchema } from '@rjsf/utils'
import validator from '@rjsf/validator-ajv8'

const schema: RJSFSchema = {
  type: 'object',
  properties: {
    person: {
      title: 'Person Info',
      type: 'object',
      properties: {
        first: {
          title: 'First Name',
          minLength: 1,
          maxLength: 200,
          type: 'string',
        },
        middle: {
          title: 'Middle Name',
          minLength: 1,
          maxLength: 200,
          type: 'string',
        },
        last: {
          title: 'Last Name',
          minLength: 1,
          maxLength: 200,
          type: 'string',
        },
      },
      required: ['first', 'last'],
    },
  },
}
const uiSchema: UiSchema = {
  'ui:field': 'LayoutGridField',
  'ui:layoutGrid': {
    'ui:row': {
      className: 'row',
      children: [
        {
          'ui:row': [
            {
              'ui:col': {
                className: 'col-xs-12',
                children: ['person'],
              },
            },
          ],
        },
        {
          'ui:row': [
            {
              'ui:col': {
                className: 'col-xs-4',
                children: [
                  {
                    name: 'person.first',
                  },
                ],
              },
            },
            {
              'ui:col': {
                className: 'col-xs-4',
                children: [
                  {
                    name: 'person.middle',
                  },
                ],
              },
            },
            {
              'ui:col': {
                className: 'col-xs-4',
                children: [
                  {
                    name: 'person.last',
                  },
                ],
              },
            },
          ],
        },
      ],
    },
  },
  person: {
    'ui:field': 'LayoutHeaderField',
  },
}

export default function OntologyPublishSourceSelectDialog({
  payload,
  open,
  onClose,
}: DialogProps<string>) {
  return (
    <Dialog open={open} onClose={() => onClose()}>
      <DialogTitle>Select Ontology source</DialogTitle>
      <DialogContent>
        <Form schema={schema} uiSchema={uiSchema} validator={validator} />
      </DialogContent>
      <DialogActions>
        <Button onClick={() => onClose()}>Close me</Button>
      </DialogActions>
    </Dialog>
  )
}
