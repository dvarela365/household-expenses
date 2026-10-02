import type { Expense } from '../api/expenses'
import { computeCategoryBreakdown } from '../lib/categoryBreakdown'
import { formatArs } from '../lib/currency'

const SLICE_COLORS = ['bg-accent', 'bg-celeste', 'bg-sun', 'bg-mist']

type CategoryBreakdownCardProps = {
  expenses: Expense[]
  total: number
}

export default function CategoryBreakdownCard({ expenses, total }: CategoryBreakdownCardProps) {
  const slices = computeCategoryBreakdown(expenses, total)
  if (slices.length === 0) return null

  return (
    <section className="rounded-card bg-surface p-4">
      <h2 className="mb-3 text-sm font-semibold text-ink">En qué gastaste</h2>

      <div className="flex h-2.5 w-full overflow-hidden rounded-full bg-tint">
        {slices.map((slice, index) => (
          <div key={slice.key} className={SLICE_COLORS[index % SLICE_COLORS.length]} style={{ width: `${slice.percentage}%` }} />
        ))}
      </div>

      <ul className="mt-3 flex flex-col gap-2">
        {slices.map((slice, index) => (
          <li key={slice.key} className="flex items-center gap-2 text-sm">
            <span
              className={`h-2.5 w-2.5 shrink-0 rounded-full ${SLICE_COLORS[index % SLICE_COLORS.length]}`}
              aria-hidden="true"
            />
            <span className="flex-1 truncate text-ink">{slice.name}</span>
            <span className="tabular-nums text-ink-soft">{formatArs(slice.amount)}</span>
            <span className="w-9 shrink-0 text-right tabular-nums text-ink-soft">{slice.percentage}%</span>
          </li>
        ))}
      </ul>
    </section>
  )
}
