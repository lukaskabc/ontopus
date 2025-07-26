import '@/config/i18n.ts'
import { DashboardLayout, DialogsProvider } from '@toolpad/core'
import OntologiesList from '@/deployments/OntologiesList.tsx'
import TransparentPaper from '@/components/TransparentPaper.tsx'

export default function Dashboard() {
  return (
    <div id={'dashboard-wrapper'}>
      <DashboardLayout disableCollapsibleSidebar={true} hideNavigation={true}>
        <TransparentPaper>
          <DialogsProvider>
            <OntologiesList />
          </DialogsProvider>
        </TransparentPaper>
      </DashboardLayout>
    </div>
  )
}
