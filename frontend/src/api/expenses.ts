import { throwForErrorResponse } from './apiError'

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
  version: number
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

export type UpdateExpenseRequest = CreateExpenseRequest & {
  version: number
}

export async function fetchMonthlyExpenses(
  month: string,
  signal?: AbortSignal,
): Promise<MonthlyExpenses> {
  const response = await fetch(`/api/expenses?month=${month}`, { signal })
  if (!response.ok) {
    await throwForErrorResponse(response)
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
  throw await throwForErrorResponse(response)
}

export async function updateExpense(id: number, request: UpdateExpenseRequest): Promise<Expense> {
  const response = await fetch(`/api/expenses/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(request),
  })

  if (response.status === 200) {
    return response.json() as Promise<Expense>
  }
  throw await throwForErrorResponse(response)
}

export async function deleteExpense(id: number): Promise<void> {
  const response = await fetch(`/api/expenses/${id}`, { method: 'DELETE' })
  if (response.status === 204) {
    return
  }
  await throwForErrorResponse(response)
}
