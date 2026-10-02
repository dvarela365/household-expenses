import type { PaymentMethod } from '../api/expenses'

export const PAYMENT_METHODS: { value: PaymentMethod; label: string }[] = [
  { value: 'CASH', label: 'Efectivo' },
  { value: 'DEBIT', label: 'Débito' },
  { value: 'CREDIT', label: 'Crédito' },
  { value: 'TRANSFER', label: 'Transferencia' },
  { value: 'QR', label: 'QR' },
]

export const PAYMENT_METHOD_LABELS: Record<PaymentMethod, string> = Object.fromEntries(
  PAYMENT_METHODS.map((method) => [method.value, method.label]),
) as Record<PaymentMethod, string>
