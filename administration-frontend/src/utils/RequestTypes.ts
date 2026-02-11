import type { Pageable } from '@hallysonh/pageable'

export interface PagedResult<C> {
  items: C[]
  itemCount: number
}

export function toPageRequest(pageable: Pageable) {
  const params = new URLSearchParams({
    page: pageable.page.toString(),
    size: pageable.size.toString(),
  })
  pageable.sort?.forEach((property, direction) => {
    params.append('sort', `${property},${direction}`)
  })
  return params
}
