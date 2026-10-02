export type PaymentMethod = 'CASH' | 'DEBIT' | 'CREDIT' | 'TRANSFER' | 'QR'

export type Expense = {
  id: number
  amount: number
  date: string
  categoryId: number
  categoryName: string
  categoryIcon: string
  paymentMethod: PaymentMethod
  merchant: string | null
  note: string | null
}

export type MonthlyExpenses = {
  month: string
  total: number
  expenses: Expense[]
}

export type CreateExpenseRequest = {
  amount: number
  date: string
  categoryId: number
  paymentMethod: PaymentMethod
  merchant?: string
  note?: string
}

/** Thrown on a 400 response; carries the server's field -> message map. */
export class ValidationError extends Error {
  fieldErrors: Record<string, string>

  constructor(fieldErrors: Record<string, string>) {
    super('Validation failed')
    this.name = 'ValidationError'
    this.fieldErrors = fieldErrors
  }
}

export async function fetchMonthlyExpenses(
  month: string,
  signal?: AbortSignal,
): Promise<MonthlyExpenses> {
  const response = await fetch(`/api/expenses?month=${month}`, { signal })
  if (!response.ok) {
    throw new Error(`HTTP ${response.status}`)
  }
  return response.json() as Promise<MonthlyExpenses>
}

export async function createExpense(request: CreateExpenseRequest): Promise<Expense> {
  const response = await fetch('/api/expenses', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(request),
  })

  if (response.status === 201) {
    return response.json() as Promise<Expense>
  }

  if (response.status === 400) {
    const problem = (await response.json()) as { errors?: Record<string, string> }
    throw new ValidationError(problem.errors ?? {})
  }

  throw new Error(`HTTP ${response.status}`)
}
