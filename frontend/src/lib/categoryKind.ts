import type { CategoryKind } from '../api/categories'

export const CATEGORY_KINDS: { value: CategoryKind; label: string }[] = [
  { value: 'FIXED', label: 'Fijo' },
  { value: 'VARIABLE', label: 'Variable' },
]

export const CATEGORY_KIND_LABELS: Record<CategoryKind, string> = Object.fromEntries(
  CATEGORY_KINDS.map((kind) => [kind.value, kind.label]),
) as Record<CategoryKind, string>
