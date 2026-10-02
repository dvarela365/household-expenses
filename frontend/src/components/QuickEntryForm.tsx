import { useState, type FormEvent } from 'react'
import { X } from 'lucide-react'
import type { Category } from '../api/categories'
import { createExpense, ValidationError, type Expense, type PaymentMethod } from '../api/expenses'
import { parseAmountInput } from '../lib/amount'
import { todayLocalDateString } from '../lib/date'
import CategoryIcon from './CategoryIcon'

const PAYMENT_METHODS: { value: PaymentMethod; label: string }[] = [
  { value: 'CASH', label: 'Efectivo' },
  { value: 'DEBIT', label: 'Débito' },
  { value: 'CREDIT', label: 'Crédito' },
  { value: 'TRANSFER', label: 'Transferencia' },
  { value: 'QR', label: 'QR' },
]

const FIELD_MESSAGES: Record<string, string> = {
  amount: 'Ingresá un monto válido, por ejemplo 1500,50.',
  date: 'Ingresá una fecha válida.',
  categoryId: 'Elegí una categoría.',
  paymentMethod: 'Elegí un medio de pago.',
  merchant: 'El comercio es demasiado largo.',
  note: 'La nota es demasiado larga.',
}

const GENERAL_ERROR_MESSAGE = 'No pudimos guardar el gasto. Probá de nuevo en unos segundos.'

type QuickEntryFormProps = {
  categories: Category[]
  onClose: () => void
  onCreated: (expense: Expense) => void
}

export default function QuickEntryForm({ categories, onClose, onCreated }: QuickEntryFormProps) {
  const [amountInput, setAmountInput] = useState('')
  const [categoryId, setCategoryId] = useState<number | null>(null)
  const [paymentMethod, setPaymentMethod] = useState<PaymentMethod>('DEBIT')
  const [date, setDate] = useState(todayLocalDateString())
  const [merchant, setMerchant] = useState('')
  const [note, setNote] = useState('')
  const [showMoreDetails, setShowMoreDetails] = useState(false)
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({})
  const [generalError, setGeneralError] = useState<string | null>(null)
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [amountTouched, setAmountTouched] = useState(false)

  const parsedAmount = parseAmountInput(amountInput)
  const canSubmit = parsedAmount !== null && categoryId !== null && !isSubmitting
  const amountError =
    fieldErrors.amount ??
    (amountTouched && amountInput.trim() !== '' && parsedAmount === null
      ? FIELD_MESSAGES.amount
      : undefined)

  function clearFieldError(field: string) {
    setFieldErrors((errors) => {
      if (!(field in errors)) return errors
      const next = { ...errors }
      delete next[field]
      return next
    })
  }

  async function handleSubmit(event: FormEvent) {
    event.preventDefault()

    if (parsedAmount === null) {
      setAmountTouched(true)
      return
    }
    if (categoryId === null) {
      setFieldErrors((errors) => ({ ...errors, categoryId: FIELD_MESSAGES.categoryId }))
      return
    }

    setFieldErrors({})
    setGeneralError(null)
    setIsSubmitting(true)

    try {
      const expense = await createExpense({
        amount: parsedAmount,
        date,
        categoryId,
        paymentMethod,
        merchant: showMoreDetails && merchant.trim() ? merchant.trim() : undefined,
        note: showMoreDetails && note.trim() ? note.trim() : undefined,
      })
      onCreated(expense)
    } catch (error) {
      if (error instanceof ValidationError) {
        const mapped: Record<string, string> = {}
        for (const field of Object.keys(error.fieldErrors)) {
          mapped[field] = FIELD_MESSAGES[field] ?? 'Revisá este campo.'
        }
        setFieldErrors(mapped)
      } else {
        setGeneralError(GENERAL_ERROR_MESSAGE)
      }
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <div className="fixed inset-0 z-50 flex items-end justify-center bg-black/50 sm:items-center">
      <div className="max-h-[90vh] w-full overflow-y-auto rounded-t-2xl bg-white p-4 sm:max-w-md sm:rounded-2xl">
        <div className="mb-4 flex items-center justify-between">
          <h2 className="text-lg font-semibold text-gray-900">Nuevo gasto</h2>
          <button
            type="button"
            onClick={onClose}
            aria-label="Cerrar"
            className="rounded-full p-2 text-gray-500 hover:bg-gray-100"
          >
            <X aria-hidden="true" />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="flex flex-col gap-4">
          <div>
            <label htmlFor="expense-amount" className="mb-1 block text-sm font-medium text-gray-700">
              Monto
            </label>
            <input
              id="expense-amount"
              type="text"
              inputMode="decimal"
              placeholder="0,00"
              value={amountInput}
              onChange={(e) => {
                setAmountInput(e.target.value)
                clearFieldError('amount')
              }}
              onBlur={() => setAmountTouched(true)}
              className="w-full rounded-lg border border-gray-300 px-4 py-3 text-2xl font-semibold"
            />
            {amountError && <p className="mt-1 text-sm text-red-600">{amountError}</p>}
          </div>

          <div>
            <span className="mb-1 block text-sm font-medium text-gray-700">Categoría</span>
            <div className="grid grid-cols-3 gap-2">
              {categories.map((category) => (
                <button
                  key={category.id}
                  type="button"
                  aria-pressed={categoryId === category.id}
                  onClick={() => {
                    setCategoryId(category.id)
                    clearFieldError('categoryId')
                  }}
                  className={`flex flex-col items-center gap-1 rounded-lg border px-2 py-3 text-xs ${
                    categoryId === category.id
                      ? 'border-blue-600 bg-blue-50 text-blue-700'
                      : 'border-gray-200 text-gray-600'
                  }`}
                >
                  <CategoryIcon name={category.icon} className="h-5 w-5" />
                  <span className="truncate">{category.name}</span>
                </button>
              ))}
            </div>
            {fieldErrors.categoryId && (
              <p className="mt-1 text-sm text-red-600">{fieldErrors.categoryId}</p>
            )}
          </div>

          <div>
            <span className="mb-1 block text-sm font-medium text-gray-700">Medio de pago</span>
            <div className="flex flex-wrap gap-2">
              {PAYMENT_METHODS.map((method) => (
                <button
                  key={method.value}
                  type="button"
                  aria-pressed={paymentMethod === method.value}
                  onClick={() => setPaymentMethod(method.value)}
                  className={`rounded-full border px-3 py-1.5 text-sm ${
                    paymentMethod === method.value
                      ? 'border-blue-600 bg-blue-50 text-blue-700'
                      : 'border-gray-200 text-gray-600'
                  }`}
                >
                  {method.label}
                </button>
              ))}
            </div>
            {fieldErrors.paymentMethod && (
              <p className="mt-1 text-sm text-red-600">{fieldErrors.paymentMethod}</p>
            )}
          </div>

          <div>
            <label htmlFor="expense-date" className="mb-1 block text-sm font-medium text-gray-700">
              Fecha
            </label>
            <input
              id="expense-date"
              type="date"
              value={date}
              onChange={(e) => setDate(e.target.value)}
              className="w-full rounded-lg border border-gray-300 px-4 py-2"
            />
            {fieldErrors.date && <p className="mt-1 text-sm text-red-600">{fieldErrors.date}</p>}
          </div>

          <button
            type="button"
            onClick={() => setShowMoreDetails((value) => !value)}
            className="self-start text-sm font-medium text-blue-600"
          >
            {showMoreDetails ? 'Ocultar detalles' : 'Más detalles'}
          </button>

          {showMoreDetails && (
            <>
              <div>
                <label htmlFor="expense-merchant" className="mb-1 block text-sm font-medium text-gray-700">
                  Comercio
                </label>
                <input
                  id="expense-merchant"
                  type="text"
                  value={merchant}
                  onChange={(e) => setMerchant(e.target.value)}
                  className="w-full rounded-lg border border-gray-300 px-4 py-2"
                />
                {fieldErrors.merchant && (
                  <p className="mt-1 text-sm text-red-600">{fieldErrors.merchant}</p>
                )}
              </div>

              <div>
                <label htmlFor="expense-note" className="mb-1 block text-sm font-medium text-gray-700">
                  Nota
                </label>
                <input
                  id="expense-note"
                  type="text"
                  value={note}
                  onChange={(e) => setNote(e.target.value)}
                  className="w-full rounded-lg border border-gray-300 px-4 py-2"
                />
                {fieldErrors.note && <p className="mt-1 text-sm text-red-600">{fieldErrors.note}</p>}
              </div>
            </>
          )}

          {generalError && <p className="text-sm text-red-600">{generalError}</p>}

          <button
            type="submit"
            disabled={!canSubmit}
            aria-disabled={!canSubmit}
            className="rounded-lg bg-blue-600 px-4 py-3 font-semibold text-white disabled:cursor-not-allowed disabled:bg-gray-300"
          >
            {isSubmitting ? 'Guardando…' : 'Guardar'}
          </button>
        </form>
      </div>
    </div>
  )
}
