export type CategoryKind = 'FIXED' | 'VARIABLE'

export type Category = {
  id: number
  name: string
  kind: CategoryKind
  icon: string
}

export async function fetchCategories(signal?: AbortSignal): Promise<Category[]> {
  const response = await fetch('/api/categories', { signal })
  if (!response.ok) {
    throw new Error(`HTTP ${response.status}`)
  }
  return response.json() as Promise<Category[]>
}
