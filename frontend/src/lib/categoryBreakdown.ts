import type { Expense } from '../api/expenses'

const MAX_SLICES = 3

export type CategorySlice = {
  key: string
  name: string
  amount: number
  percentage: number
}

/**
 * Groups the month's expenses by category, keeping the top 3 by amount and
 * folding the rest into an "Otras" slice. Percentages are rounded to an integer.
 */
export function computeCategoryBreakdown(expenses: Expense[], total: number): CategorySlice[] {
  const sums = new Map<number, { name: string; amount: number }>()
  for (const expense of expenses) {
    const entry = sums.get(expense.categoryId)
    if (entry) entry.amount += expense.amount
    else sums.set(expense.categoryId, { name: expense.categoryName, amount: expense.amount })
  }

  const sorted = [...sums.values()].sort((a, b) => b.amount - a.amount)
  const top = sorted.slice(0, MAX_SLICES)
  const rest = sorted.slice(MAX_SLICES)

  const slices = [...top]
  if (rest.length > 0) {
    slices.push({ name: 'Otras', amount: rest.reduce((sum, item) => sum + item.amount, 0) })
  }

  return slices.map((slice, index) => ({
    key: `${slice.name}-${index}`,
    name: slice.name,
    amount: slice.amount,
    percentage: total > 0 ? Math.round((slice.amount / total) * 100) : 0,
  }))
}
