import { useEffect, useState } from 'react'
import { fetchCategories, type Category } from '../api/categories'
import { CATEGORY_KIND_LABELS } from '../lib/categoryKind'
import { isAbortError } from '../lib/http'
import CategoryForm from './CategoryForm'
import CategoryIcon from './CategoryIcon'

type RequestState<T> =
  | { status: 'loading' }
  | { status: 'error' }
  | { status: 'ready'; data: T }

type SheetState = { mode: 'create' } | { mode: 'edit'; category: Category }

type CategoriesScreenProps = {
  onCategoriesChanged: () => void
  onToast: (message: string) => void
}

export default function CategoriesScreen({ onCategoriesChanged, onToast }: CategoriesScreenProps) {
  const [categoriesState, setCategoriesState] = useState<RequestState<Category[]>>({
    status: 'loading',
  })
  const [reloadToken, setReloadToken] = useState(0)
  const [sheetState, setSheetState] = useState<SheetState | null>(null)

  useEffect(() => {
    const controller = new AbortController()

    fetchCategories(true, controller.signal)
      .then((data) => setCategoriesState({ status: 'ready', data }))
      .catch((error: unknown) => {
        if (isAbortError(error)) return
        setCategoriesState({ status: 'error' })
      })

    return () => controller.abort()
  }, [reloadToken])

  function retry() {
    setCategoriesState({ status: 'loading' })
    setReloadToken((token) => token + 1)
  }

  function refresh() {
    setReloadToken((token) => token + 1)
  }

  function handleSaved() {
    const wasEdit = sheetState?.mode === 'edit'
    setSheetState(null)
    onToast(wasEdit ? 'Categoría actualizada' : 'Categoría creada')
    refresh()
    onCategoriesChanged()
  }

  function handleDeleted() {
    setSheetState(null)
    onToast('Categoría eliminada')
    refresh()
    onCategoriesChanged()
  }

  function handleArchiveChanged(category: Category) {
    setSheetState(null)
    onToast(category.archived ? 'Categoría archivada' : 'Categoría desarchivada')
    refresh()
    onCategoriesChanged()
  }

  const active = categoriesState.status === 'ready' ? categoriesState.data.filter((c) => !c.archived) : []
  const archived = categoriesState.status === 'ready' ? categoriesState.data.filter((c) => c.archived) : []

  return (
    <main className="flex flex-1 flex-col gap-4 pt-2">
      <div className="flex items-center justify-between pt-2">
        <h1 className="text-xl font-bold text-ink">Categorías</h1>
        <button
          type="button"
          onClick={() => setSheetState({ mode: 'create' })}
          className="rounded-full bg-accent px-4 py-2 text-sm font-semibold text-white"
        >
          Agregar categoría
        </button>
      </div>

      {categoriesState.status === 'loading' && (
        <p className="px-4 py-16 text-center text-ink-soft">Cargando categorías…</p>
      )}

      {categoriesState.status === 'error' && (
        <div className="flex flex-col items-center gap-3 px-4 py-16 text-center">
          <p className="text-ink-soft">No pudimos cargar las categorías.</p>
          <button
            type="button"
            onClick={retry}
            className="rounded-full bg-accent px-4 py-2 font-semibold text-white"
          >
            Reintentar
          </button>
        </div>
      )}

      {categoriesState.status === 'ready' && (
        <div className="flex flex-col gap-4">
          {active.length === 0 && archived.length === 0 && (
            <p className="px-4 py-16 text-center text-ink-soft">No hay categorías todavía.</p>
          )}

          {active.length > 0 && (
            <ul className="divide-y divide-line rounded-card bg-surface">
              {active.map((category) => (
                <li key={category.id}>
                  <button
                    type="button"
                    onClick={() => setSheetState({ mode: 'edit', category })}
                    className="flex w-full items-center gap-3 px-4 py-3 text-left active:bg-ground"
                  >
                    <div className="flex h-11 w-11 shrink-0 items-center justify-center rounded-icon bg-tint text-accent">
                      <CategoryIcon name={category.icon} className="h-5 w-5" />
                    </div>
                    <div className="min-w-0 flex-1">
                      <p className="truncate font-semibold text-ink">{category.name}</p>
                      <p className="truncate text-sm text-ink-soft">{CATEGORY_KIND_LABELS[category.kind]}</p>
                    </div>
                  </button>
                </li>
              ))}
            </ul>
          )}

          {archived.length > 0 && (
            <section>
              <h2 className="mb-2 px-1 text-sm font-semibold text-ink-soft">Archivadas</h2>
              <ul className="divide-y divide-line rounded-card bg-surface">
                {archived.map((category) => (
                  <li key={category.id}>
                    <button
                      type="button"
                      onClick={() => setSheetState({ mode: 'edit', category })}
                      className="flex w-full items-center gap-3 px-4 py-3 text-left active:bg-ground"
                    >
                      <div className="flex h-11 w-11 shrink-0 items-center justify-center rounded-icon bg-tint text-accent">
                        <CategoryIcon name={category.icon} className="h-5 w-5" />
                      </div>
                      <div className="min-w-0 flex-1">
                        <p className="truncate font-semibold text-ink">{category.name}</p>
                        <p className="truncate text-sm text-ink-soft">{CATEGORY_KIND_LABELS[category.kind]}</p>
                      </div>
                      <span className="shrink-0 rounded-full bg-tint px-2 py-0.5 text-xs font-medium text-ink-soft">
                        Archivada
                      </span>
                    </button>
                  </li>
                ))}
              </ul>
            </section>
          )}
        </div>
      )}

      {sheetState && (
        <CategoryForm
          category={sheetState.mode === 'edit' ? sheetState.category : undefined}
          onClose={() => setSheetState(null)}
          onSaved={handleSaved}
          onDeleted={handleDeleted}
          onArchiveChanged={handleArchiveChanged}
        />
      )}
    </main>
  )
}
