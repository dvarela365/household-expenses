import type { Expense } from '../api/expenses'
import { formatDayGroupLabel } from '../lib/date'
import ExpenseListItem from './ExpenseListItem'

type ExpenseListProps = {
  expenses: Expense[]
}

function groupByDay(expenses: Expense[]): [string, Expense[]][] {
  const groups = new Map<string, Expense[]>()
  for (const expense of expenses) {
    const existing = groups.get(expense.date)
    if (existing) existing.push(expense)
    else groups.set(expense.date, [expense])
  }
  return [...groups.entries()].sort(([a], [b]) => (a < b ? 1 : -1))
}

export default function ExpenseList({ expenses }: ExpenseListProps) {
  const groups = groupByDay(expenses)

  return (
    <div className="flex flex-col gap-4">
      {groups.map(([date, dayExpenses]) => (
        <section key={date}>
          <h2 className="mb-2 px-1 text-sm font-semibold text-ink-soft">{formatDayGroupLabel(date)}</h2>
          <ul className="divide-y divide-line rounded-card bg-surface">
            {dayExpenses.map((expense) => (
              <ExpenseListItem key={expense.id} expense={expense} />
            ))}
          </ul>
        </section>
      ))}
    </div>
  )
}
