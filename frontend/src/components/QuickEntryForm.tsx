import { useState, type FormEvent } from 'react'
import { Calendar, ChevronDown, X } from 'lucide-react'
import type { Category } from '../api/categories'
import { createExpense, ValidationError, type Expense, type PaymentMethod } from '../api/expenses'
import { parseAmountInput } from '../lib/amount'
import { formatArs } from '../lib/currency'
import { formatDateChipLabel, todayLocalDateString } from '../lib/date'
import { PAYMENT_METHODS } from '../lib/paymentMethod'
import CategoryIcon from './CategoryIcon'

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
  const saveLabel = isSubmitting
    ? 'Guardando…'
    : parsedAmount !== null
      ? `Guardar ${formatArs(parsedAmount)}`
      : 'Guardar'

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
    <div className="fixed inset-0 z-50 flex items-end justify-center bg-black/50">
      <div className="sheet-enter flex max-h-[88vh] w-full max-w-md flex-col rounded-t-[28px] bg-surface">
        <div className="flex shrink-0 flex-col items-center pt-3">
          <div className="h-1.5 w-10 rounded-full bg-line" />
        </div>

        <div className="flex shrink-0 items-center justify-between px-5 pt-3 pb-2">
          <h2 className="text-lg font-bold text-ink">Nuevo gasto</h2>
          <button
            type="button"
            onClick={onClose}
            aria-label="Cerrar"
            className="flex h-9 w-9 items-center justify-center rounded-full bg-tint text-ink-soft"
          >
            <X className="h-4 w-4" aria-hidden="true" />
          </button>
        </div>

        <form
          id="quick-entry-form"
          onSubmit={handleSubmit}
          className="flex-1 overflow-y-auto px-5 pb-4"
        >
          <div className="pt-2">
            <label htmlFor="expense-amount" className="mb-1 block text-sm font-medium text-ink-soft">
              Monto
            </label>
            <div className="flex items-end gap-1 border-b-2 border-accent pb-1">
              <span className="text-2xl font-bold text-ink-soft">$</span>
              <input
                id="expense-amount"
                type="text"
                inputMode="decimal"
                placeholder="0,00"
                autoFocus
                value={amountInput}
                onChange={(e) => {
                  setAmountInput(e.target.value)
                  clearFieldError('amount')
                }}
                onBlur={() => setAmountTouched(true)}
                className="h-10 flex-1 bg-transparent text-[28px] font-bold text-ink outline-none"
              />
            </div>
            <p className="mt-1 text-xs text-ink-soft">Usá coma para los centavos</p>
            {amountError && <p className="mt-1 text-sm text-red-600">{amountError}</p>}
          </div>

          <div className="pt-5">
            <span className="mb-2 block text-sm font-medium text-ink-soft">Categoría</span>
            <div className="grid grid-cols-4 gap-2">
              {categories.map((category) => {
                const selected = categoryId === category.id
                return (
                  <button
                    key={category.id}
                    type="button"
                    aria-pressed={selected}
                    onClick={() => {
                      setCategoryId(category.id)
                      clearFieldError('categoryId')
                    }}
                    className={`flex h-16 flex-col items-center justify-center gap-1 rounded-icon border-2 px-1 text-[11px] font-medium ${
                      selected ? 'border-accent bg-tint text-accent' : 'border-transparent bg-ground text-ink-soft'
                    }`}
                  >
                    <CategoryIcon name={category.icon} className="h-5 w-5" />
                    <span className="w-full truncate text-center leading-tight">{category.name}</span>
                  </button>
                )
              })}
            </div>
            {fieldErrors.categoryId && (
              <p className="mt-1 text-sm text-red-600">{fieldErrors.categoryId}</p>
            )}
          </div>

          <div className="pt-5">
            <span className="mb-2 block text-sm font-medium text-ink-soft">Medio de pago</span>
            <div className="flex gap-2 overflow-x-auto pb-1">
              {PAYMENT_METHODS.map((method) => {
                const selected = paymentMethod === method.value
                return (
                  <button
                    key={method.value}
                    type="button"
                    aria-pressed={selected}
                    onClick={() => setPaymentMethod(method.value)}
                    className={`shrink-0 rounded-full px-4 py-2 text-sm font-medium ${
                      selected ? 'bg-ink text-white' : 'bg-ground text-ink-soft'
                    }`}
                  >
                    {method.label}
                  </button>
                )
              })}
            </div>
            {fieldErrors.paymentMethod && (
              <p className="mt-1 text-sm text-red-600">{fieldErrors.paymentMethod}</p>
            )}
          </div>

          <div className="pt-5">
            <span className="mb-2 block text-sm font-medium text-ink-soft">Fecha</span>
            <div className="relative inline-flex">
              <div className="pointer-events-none inline-flex items-center gap-2 rounded-full bg-ground px-4 py-2 text-sm font-medium text-ink">
                <Calendar className="h-4 w-4 text-ink-soft" aria-hidden="true" />
                {formatDateChipLabel(date)}
              </div>
              <input
                id="expense-date"
                type="date"
                value={date}
                onChange={(e) => setDate(e.target.value)}
                aria-label="Fecha"
                className="absolute inset-0 h-full w-full cursor-pointer opacity-0"
              />
            </div>
            {fieldErrors.date && <p className="mt-1 text-sm text-red-600">{fieldErrors.date}</p>}
          </div>

          <button
            type="button"
            onClick={() => setShowMoreDetails((value) => !value)}
            className="mt-5 flex items-center gap-1 text-sm font-semibold text-accent"
          >
            {showMoreDetails ? 'Ocultar detalles' : 'Más detalles'}
            <ChevronDown
              className={`h-4 w-4 transition-transform ${showMoreDetails ? 'rotate-180' : ''}`}
              aria-hidden="true"
            />
          </button>

          {showMoreDetails && (
            <div className="flex flex-col gap-4 pt-4">
              <div>
                <label htmlFor="expense-merchant" className="mb-1 block text-sm font-medium text-ink-soft">
                  Comercio
                </label>
                <input
                  id="expense-merchant"
                  type="text"
                  value={merchant}
                  onChange={(e) => setMerchant(e.target.value)}
                  className="w-full rounded-icon border border-line px-4 py-2 text-ink outline-none focus:border-accent"
                />
                {fieldErrors.merchant && (
                  <p className="mt-1 text-sm text-red-600">{fieldErrors.merchant}</p>
                )}
              </div>

              <div>
                <label htmlFor="expense-note" className="mb-1 block text-sm font-medium text-ink-soft">
                  Nota
                </label>
                <input
                  id="expense-note"
                  type="text"
                  value={note}
                  onChange={(e) => setNote(e.target.value)}
                  className="w-full rounded-icon border border-line px-4 py-2 text-ink outline-none focus:border-accent"
                />
                {fieldErrors.note && <p className="mt-1 text-sm text-red-600">{fieldErrors.note}</p>}
              </div>
            </div>
          )}

          {generalError && <p className="pt-4 text-sm text-red-600">{generalError}</p>}
        </form>

        <div className="shrink-0 border-t border-line px-5 py-4">
          <button
            type="submit"
            form="quick-entry-form"
            disabled={!canSubmit}
            aria-disabled={!canSubmit}
            className="flex h-14 w-full items-center justify-center rounded-full bg-accent text-base font-semibold text-white tabular-nums disabled:cursor-not-allowed disabled:bg-mist"
          >
            {saveLabel}
          </button>
        </div>
      </div>
    </div>
  )
}
