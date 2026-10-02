import type { Expense } from '../api/expenses'
import EmptyState from './EmptyState'
import ExpenseListItem from './ExpenseListItem'

type ExpenseListProps = {
  expenses: Expense[]
}

export default function ExpenseList({ expenses }: ExpenseListProps) {
  if (expenses.length === 0) {
    return <EmptyState />
  }

  return (
    <ul className="divide-y divide-gray-100 bg-white">
      {expenses.map((expense) => (
        <ExpenseListItem key={expense.id} expense={expense} />
      ))}
    </ul>
  )
}
