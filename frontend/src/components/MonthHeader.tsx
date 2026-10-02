import { ChevronLeft, ChevronRight } from 'lucide-react'
import { formatMonthLabel } from '../lib/date'

type MonthHeaderProps = {
  month: string
  onPrevMonth: () => void
  onNextMonth: () => void
}

export default function MonthHeader({ month, onPrevMonth, onNextMonth }: MonthHeaderProps) {
  return (
    <header className="flex items-center justify-between py-3">
      <button
        type="button"
        onClick={onPrevMonth}
        aria-label="Mes anterior"
        className="flex h-11 w-11 shrink-0 items-center justify-center rounded-full bg-surface text-ink-soft"
      >
        <ChevronLeft aria-hidden="true" />
      </button>

      <div className="text-center">
        <p className="text-xs font-semibold uppercase tracking-wide text-ink-soft">Gastos de</p>
        <p className="text-lg font-bold text-ink">{formatMonthLabel(month)}</p>
      </div>

      <button
        type="button"
        onClick={onNextMonth}
        aria-label="Mes siguiente"
        className="flex h-11 w-11 shrink-0 items-center justify-center rounded-full bg-surface text-ink-soft"
      >
        <ChevronRight aria-hidden="true" />
      </button>
    </header>
  )
}
