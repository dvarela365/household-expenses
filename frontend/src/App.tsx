import { useEffect, useState } from 'react'
import { fetchCategories, type Category } from './api/categories'
import { fetchMonthlyExpenses, type MonthlyExpenses } from './api/expenses'
import CategoryBreakdownCard from './components/CategoryBreakdownCard'
import EmptyState from './components/EmptyState'
import ExpenseList from './components/ExpenseList'
import FloatingAddButton from './components/FloatingAddButton'
import HeroCard from './components/HeroCard'
import MonthHeader from './components/MonthHeader'
import QuickEntryForm from './components/QuickEntryForm'
import Toast from './components/Toast'
import { currentYearMonth, shiftYearMonth } from './lib/date'

type RequestState<T> =
  | { status: 'loading' }
  | { status: 'error' }
  | { status: 'ready'; data: T }

function isAbortError(error: unknown): boolean {
  return error instanceof DOMException && error.name === 'AbortError'
}

export default function App() {
  const [month, setMonth] = useState(currentYearMonth())
  const [expensesState, setExpensesState] = useState<RequestState<MonthlyExpenses>>({
    status: 'loading',
  })
  const [categoriesState, setCategoriesState] = useState<RequestState<Category[]>>({
    status: 'loading',
  })
  const [isFormOpen, setIsFormOpen] = useState(false)
  const [toastMessage, setToastMessage] = useState<string | null>(null)
  const [reloadToken, setReloadToken] = useState(0)
  const [categoriesReloadToken, setCategoriesReloadToken] = useState(0)

  // `month`/`reloadToken` changes are always paired (by the handlers below) with a
  // synchronous "loading" state update, so this effect only needs to run the fetch
  // and record its outcome — it never needs to set the loading state itself.
  useEffect(() => {
    const controller = new AbortController()

    fetchMonthlyExpenses(month, controller.signal)
      .then((data) => setExpensesState({ status: 'ready', data }))
      .catch((error: unknown) => {
        if (isAbortError(error)) return
        setExpensesState({ status: 'error' })
      })

    return () => controller.abort()
  }, [month, reloadToken])

  function goToMonth(next: string) {
    setExpensesState({ status: 'loading' })
    setMonth(next)
  }

  function retryExpenses() {
    setExpensesState({ status: 'loading' })
    setReloadToken((token) => token + 1)
  }

  // Re-fetches the current month in the background, keeping whatever is on
  // screen (ready or error) visible until the new data arrives.
  function refreshExpenses() {
    setReloadToken((token) => token + 1)
  }

  useEffect(() => {
    const controller = new AbortController()

    fetchCategories(controller.signal)
      .then((data) => setCategoriesState({ status: 'ready', data }))
      .catch((error: unknown) => {
        if (isAbortError(error)) return
        setCategoriesState({ status: 'error' })
      })

    return () => controller.abort()
  }, [categoriesReloadToken])

  function retryCategories() {
    setCategoriesState({ status: 'loading' })
    setCategoriesReloadToken((token) => token + 1)
  }

  function handleExpenseCreated() {
    setIsFormOpen(false)
    setToastMessage('Gasto guardado')
    refreshExpenses()
  }

  return (
    <div className="min-h-screen bg-ground">
      <div className="mx-auto flex min-h-screen w-full max-w-md flex-col px-4 pb-28">
        <MonthHeader
          month={month}
          onPrevMonth={() => goToMonth(shiftYearMonth(month, -1))}
          onNextMonth={() => goToMonth(shiftYearMonth(month, 1))}
        />

        <main className="flex flex-1 flex-col gap-4 pt-2">
          {expensesState.status === 'loading' && (
            <p className="px-4 py-16 text-center text-ink-soft">Cargando gastos…</p>
          )}

          {expensesState.status === 'error' && (
            <div className="flex flex-col items-center gap-3 px-4 py-16 text-center">
              <p className="text-ink-soft">No pudimos cargar los gastos de este mes.</p>
              <button
                type="button"
                onClick={retryExpenses}
                className="rounded-full bg-accent px-4 py-2 font-semibold text-white"
              >
                Reintentar
              </button>
            </div>
          )}

          {expensesState.status === 'ready' && (
            <>
              <HeroCard total={expensesState.data.total} count={expensesState.data.expenses.length} />

              {expensesState.data.expenses.length === 0 ? (
                <EmptyState month={month} onAddClick={() => setIsFormOpen(true)} />
              ) : (
                <>
                  <CategoryBreakdownCard
                    expenses={expensesState.data.expenses}
                    total={expensesState.data.total}
                  />
                  <ExpenseList expenses={expensesState.data.expenses} />
                </>
              )}
            </>
          )}
        </main>
      </div>

      <FloatingAddButton onClick={() => setIsFormOpen(true)} />

      {isFormOpen &&
        (categoriesState.status === 'ready' ? (
          <QuickEntryForm
            categories={categoriesState.data}
            onClose={() => setIsFormOpen(false)}
            onCreated={handleExpenseCreated}
          />
        ) : (
          <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
            <div className="rounded-card bg-surface p-6 text-center">
              {categoriesState.status === 'loading' && <p className="text-ink">Cargando categorías…</p>}
              {categoriesState.status === 'error' && (
                <div className="flex flex-col items-center gap-3">
                  <p className="text-ink-soft">No pudimos cargar las categorías.</p>
                  <button
                    type="button"
                    onClick={retryCategories}
                    className="rounded-full bg-accent px-4 py-2 font-semibold text-white"
                  >
                    Reintentar
                  </button>
                </div>
              )}
              <button
                type="button"
                onClick={() => setIsFormOpen(false)}
                className="mt-4 text-sm font-medium text-ink-soft"
              >
                Cerrar
              </button>
            </div>
          </div>
        ))}

      {toastMessage && <Toast message={toastMessage} onDismiss={() => setToastMessage(null)} />}
    </div>
  )
}
