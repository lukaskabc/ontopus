import { VersionSeriesResponse } from '@/model/VersionSeriesResponse.ts'
import DatasetResponseDetail from '@/ontologies/detail/DatasetResponseDetail.tsx'

export interface VersionSeriesDetailProps {
  versionSeries: VersionSeriesResponse | null
}

export default function VersionSeriesResponseDetail({ versionSeries }: VersionSeriesDetailProps) {
  return <DatasetResponseDetail dataset={versionSeries} />
}
