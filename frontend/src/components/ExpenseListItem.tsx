import type { Expense } from '../api/expenses'
import { formatArs } from '../lib/currency'
import { formatExpenseDate } from '../lib/date'
import CategoryIcon from './CategoryIcon'

type ExpenseListItemProps = {
  expense: Expense
}

export default function ExpenseListItem({ expense }: ExpenseListItemProps) {
  return (
    <li className="flex items-center gap-3 px-4 py-3">
      <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-gray-100 text-gray-600">
        <CategoryIcon name={expense.categoryIcon} className="h-5 w-5" />
      </div>

      <div className="min-w-0 flex-1">
        <p className="truncate font-medium text-gray-900">{expense.categoryName}</p>
        <p className="truncate text-sm text-gray-500">
          {formatExpenseDate(expense.date)}
          {expense.merchant ? ` · ${expense.merchant}` : ''}
        </p>
      </div>

      <p className="shrink-0 font-semibold text-gray-900">{formatArs(expense.amount)}</p>
    </li>
  )
}
