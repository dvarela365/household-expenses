import { throwForErrorResponse } from './apiError'

export type CategoryKind = 'FIXED' | 'VARIABLE'

export type Category = {
  id: number
  name: string
  kind: CategoryKind
  icon: string
  archived: boolean
}

export type CategoryRequest = {
  name: string
  kind: CategoryKind
  icon: string
}

export async function fetchCategories(
  includeArchived = false,
  signal?: AbortSignal,
): Promise<Category[]> {
  const response = await fetch(`/api/categories?includeArchived=${includeArchived}`, { signal })
  if (!response.ok) {
    await throwForErrorResponse(response)
  }
  return response.json() as Promise<Category[]>
}

export async function createCategory(request: CategoryRequest): Promise<Category> {
  const response = await fetch('/api/categories', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(request),
  })

  if (response.status === 201) {
    return response.json() as Promise<Category>
  }
  throw await throwForErrorResponse(response)
}

export async function updateCategory(id: number, request: CategoryRequest): Promise<Category> {
  const response = await fetch(`/api/categories/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(request),
  })

  if (response.status === 200) {
    return response.json() as Promise<Category>
  }
  throw await throwForErrorResponse(response)
}

export async function deleteCategory(id: number): Promise<void> {
  const response = await fetch(`/api/categories/${id}`, { method: 'DELETE' })
  if (response.status === 204) {
    return
  }
  await throwForErrorResponse(response)
}

export async function archiveCategory(id: number): Promise<Category> {
  const response = await fetch(`/api/categories/${id}/archive`, { method: 'POST' })
  if (response.status === 200) {
    return response.json() as Promise<Category>
  }
  throw await throwForErrorResponse(response)
}

export async function unarchiveCategory(id: number): Promise<Category> {
  const response = await fetch(`/api/categories/${id}/unarchive`, { method: 'POST' })
  if (response.status === 200) {
    return response.json() as Promise<Category>
  }
  throw await throwForErrorResponse(response)
}
