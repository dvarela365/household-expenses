import { Plus, Receipt } from 'lucide-react'
import { formatMonthLabel } from '../lib/date'

type EmptyStateProps = {
  month: string
  onAddClick: () => void
}

export default function EmptyState({ month, onAddClick }: EmptyStateProps) {
  const monthName = formatMonthLabel(month).split(' ')[0].toLowerCase()

  return (
    <div className="flex flex-col items-center gap-4 px-6 py-10 text-center">
      <div className="flex h-[88px] w-[88px] shrink-0 items-center justify-center rounded-card bg-tint text-accent">
        <Receipt className="h-9 w-9" aria-hidden="true" />
      </div>

      <div className="flex flex-col gap-1">
        <h2 className="text-lg font-bold text-ink">Todavía no hay gastos en {monthName}</h2>
        <p className="text-sm text-ink-soft">
          Cargá el primero y acá vas a ver el total y en qué se va la plata.
        </p>
      </div>

      <button
        type="button"
        onClick={onAddClick}
        className="mt-1 inline-flex h-12 items-center gap-2 rounded-full bg-accent px-6 text-sm font-semibold text-white"
      >
        <Plus className="h-4 w-4" aria-hidden="true" />
        Cargar gasto
      </button>
    </div>
  )
}
