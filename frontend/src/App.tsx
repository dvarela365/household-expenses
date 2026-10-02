import { useEffect, useState } from 'react'
import { fetchCategories, type Category } from './api/categories'
import { fetchMonthlyExpenses, type Expense, type MonthlyExpenses } from './api/expenses'
import BottomNav, { type Tab } from './components/BottomNav'
import CategoriesScreen from './components/CategoriesScreen'
import CategoryBreakdownCard from './components/CategoryBreakdownCard'
import EmptyState from './components/EmptyState'
import ExpenseList from './components/ExpenseList'
import FloatingAddButton from './components/FloatingAddButton'
import HeroCard from './components/HeroCard'
import MonthHeader from './components/MonthHeader'
import QuickEntryForm from './components/QuickEntryForm'
import Toast from './components/Toast'
import { currentYearMonth, shiftYearMonth } from './lib/date'
import { isAbortError } from './lib/http'

type RequestState<T> =
  | { status: 'loading' }
  | { status: 'error' }
  | { status: 'ready'; data: T }

type SheetState = { mode: 'create' } | { mode: 'edit'; expense: Expense }

export default function App() {
  const [month, setMonth] = useState(currentYearMonth())
  const [expensesState, setExpensesState] = useState<RequestState<MonthlyExpenses>>({
    status: 'loading',
  })
  const [categoriesState, setCategoriesState] = useState<RequestState<Category[]>>({
    status: 'loading',
  })
  const [activeTab, setActiveTab] = useState<Tab>('expenses')
  const [sheetState, setSheetState] = useState<SheetState | null>(null)
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

    fetchCategories(false, controller.signal)
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

  function handleExpenseSaved() {
    const wasEdit = sheetState?.mode === 'edit'
    setSheetState(null)
    setToastMessage(wasEdit ? 'Gasto actualizado' : 'Gasto guardado')
    refreshExpenses()
  }

  function handleExpenseDeleted() {
    setSheetState(null)
    setToastMessage('Gasto eliminado')
    refreshExpenses()
  }

  function handleExpenseStaleOrGone(message: string) {
    setSheetState(null)
    setToastMessage(message)
    refreshExpenses()
  }

  return (
    <div className="min-h-screen bg-ground">
      <div className="mx-auto flex min-h-screen w-full max-w-md flex-col px-4 pb-32">
        {activeTab === 'expenses' ? (
          <>
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
                    <EmptyState month={month} onAddClick={() => setSheetState({ mode: 'create' })} />
                  ) : (
                    <>
                      <CategoryBreakdownCard
                        expenses={expensesState.data.expenses}
                        total={expensesState.data.total}
                      />
                      <ExpenseList
                        expenses={expensesState.data.expenses}
                        onSelectExpense={(expense) => setSheetState({ mode: 'edit', expense })}
                      />
                    </>
                  )}
                </>
              )}
            </main>
          </>
        ) : (
          <CategoriesScreen
            onCategoriesChanged={() => setCategoriesReloadToken((token) => token + 1)}
            onToast={setToastMessage}
          />
        )}
      </div>

      {activeTab === 'expenses' && (
        <FloatingAddButton onClick={() => setSheetState({ mode: 'create' })} />
      )}

      <BottomNav activeTab={activeTab} onTabChange={setActiveTab} />

      {sheetState &&
        (categoriesState.status === 'ready' ? (
          <QuickEntryForm
            categories={categoriesState.data}
            expense={sheetState.mode === 'edit' ? sheetState.expense : undefined}
            onClose={() => setSheetState(null)}
            onSaved={handleExpenseSaved}
            onDeleted={handleExpenseDeleted}
            onStaleOrGone={handleExpenseStaleOrGone}
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
                onClick={() => setSheetState(null)}
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
