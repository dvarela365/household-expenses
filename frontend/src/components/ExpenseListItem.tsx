import type { Expense } from '../api/expenses'
import { formatArs } from '../lib/currency'
import { PAYMENT_METHOD_LABELS } from '../lib/paymentMethod'
import CategoryIcon from './CategoryIcon'

type ExpenseListItemProps = {
  expense: Expense
  onSelect: (expense: Expense) => void
}

export default function ExpenseListItem({ expense, onSelect }: ExpenseListItemProps) {
  const paymentLabel = PAYMENT_METHOD_LABELS[expense.paymentMethod]
  const subtitle = expense.merchant ? `${expense.merchant} · ${paymentLabel}` : paymentLabel

  return (
    <li>
      <button
        type="button"
        onClick={() => onSelect(expense)}
        className="flex w-full items-center gap-3 px-4 py-3 text-left active:bg-ground"
      >
        <div className="flex h-11 w-11 shrink-0 items-center justify-center rounded-icon bg-tint text-accent">
          <CategoryIcon name={expense.categoryIcon} className="h-5 w-5" />
        </div>

        <div className="min-w-0 flex-1">
          <p className="truncate font-semibold text-ink">{expense.categoryName}</p>
          <p className="truncate text-sm text-ink-soft">{subtitle}</p>
        </div>

        <p className="shrink-0 tabular-nums font-semibold text-ink">− {formatArs(expense.amount)}</p>
      </button>
    </li>
  )
}
