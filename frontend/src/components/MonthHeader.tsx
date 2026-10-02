import { ChevronLeft, ChevronRight } from 'lucide-react'
import { formatArs } from '../lib/currency'
import { formatMonthLabel } from '../lib/date'

type MonthHeaderProps = {
  month: string
  total: number | null
  onPrevMonth: () => void
  onNextMonth: () => void
}

export default function MonthHeader({ month, total, onPrevMonth, onNextMonth }: MonthHeaderProps) {
  return (
    <header className="flex items-center justify-between bg-white px-4 py-4 shadow-sm">
      <button
        type="button"
        onClick={onPrevMonth}
        aria-label="Mes anterior"
        className="rounded-full p-2 text-gray-500 hover:bg-gray-100"
      >
        <ChevronLeft aria-hidden="true" />
      </button>

      <div className="text-center">
        <p className="text-sm font-medium text-gray-500">{formatMonthLabel(month)}</p>
        <p className="text-2xl font-semibold text-gray-900">
          {total === null ? '—' : formatArs(total)}
        </p>
      </div>

      <button
        type="button"
        onClick={onNextMonth}
        aria-label="Mes siguiente"
        className="rounded-full p-2 text-gray-500 hover:bg-gray-100"
      >
        <ChevronRight aria-hidden="true" />
      </button>
    </header>
  )
}
