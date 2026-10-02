import { useState, type FormEvent } from 'react'
import { X } from 'lucide-react'
import { ProblemError, ValidationError } from '../api/apiError'
import {
  archiveCategory,
  createCategory,
  deleteCategory,
  unarchiveCategory,
  updateCategory,
  type Category,
  type CategoryKind,
} from '../api/categories'
import { ALLOWED_ICON_NAMES, ICON_LABELS } from '../lib/categoryIcons'
import { CATEGORY_KINDS } from '../lib/categoryKind'
import CategoryIcon from './CategoryIcon'
import ConfirmDialog from './ConfirmDialog'

const FIELD_MESSAGES: Record<string, string> = {
  name: 'Ingresá un nombre para la categoría.',
  kind: 'Elegí un tipo.',
  icon: 'Elegí un ícono.',
}

const GENERAL_ERROR_MESSAGE = 'No pudimos guardar la categoría. Probá de nuevo en unos segundos.'

type CategoryFormProps = {
  category?: Category
  onClose: () => void
  onSaved: (category: Category) => void
  onDeleted: () => void
  onArchiveChanged: (category: Category) => void
}

export default function CategoryForm({
  category,
  onClose,
  onSaved,
  onDeleted,
  onArchiveChanged,
}: CategoryFormProps) {
  const [name, setName] = useState(category?.name ?? '')
  const [kind, setKind] = useState<CategoryKind>(category?.kind ?? 'VARIABLE')
  const [icon, setIcon] = useState(category?.icon ?? '')
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({})
  const [nameError, setNameError] = useState<string | null>(null)
  const [generalError, setGeneralError] = useState<string | null>(null)
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false)
  const [showArchiveInsteadConfirm, setShowArchiveInsteadConfirm] = useState(false)

  const canSubmit = name.trim() !== '' && icon !== '' && !isSubmitting

  async function handleSubmit(event: FormEvent) {
    event.preventDefault()

    const trimmedName = name.trim()
    if (!trimmedName) {
      setFieldErrors((errors) => ({ ...errors, name: FIELD_MESSAGES.name }))
      return
    }
    if (!icon) {
      setFieldErrors((errors) => ({ ...errors, icon: FIELD_MESSAGES.icon }))
      return
    }

    setFieldErrors({})
    setNameError(null)
    setGeneralError(null)
    setIsSubmitting(true)

    try {
      const payload = { name: trimmedName, kind, icon }
      const saved = category
        ? await updateCategory(category.id, payload)
        : await createCategory(payload)
      onSaved(saved)
    } catch (error) {
      if (error instanceof ValidationError) {
        const mapped: Record<string, string> = {}
        for (const field of Object.keys(error.fieldErrors)) {
          mapped[field] = FIELD_MESSAGES[field] ?? 'Revisá este campo.'
        }
        setFieldErrors(mapped)
      } else if (error instanceof ProblemError && error.status === 409) {
        setNameError('Ya existe una categoría con ese nombre.')
      } else {
        setGeneralError(GENERAL_ERROR_MESSAGE)
      }
    } finally {
      setIsSubmitting(false)
    }
  }

  async function handleToggleArchive() {
    if (!category) return
    try {
      const updated = category.archived
        ? await unarchiveCategory(category.id)
        : await archiveCategory(category.id)
      onArchiveChanged(updated)
    } catch {
      setGeneralError(GENERAL_ERROR_MESSAGE)
    }
  }

  async function handleDelete() {
    if (!category) return
    setShowDeleteConfirm(false)
    try {
      await deleteCategory(category.id)
      onDeleted()
    } catch (error) {
      if (error instanceof ProblemError && error.status === 409) {
        setShowArchiveInsteadConfirm(true)
      } else {
        setGeneralError(GENERAL_ERROR_MESSAGE)
      }
    }
  }

  async function handleArchiveInstead() {
    if (!category) return
    setShowArchiveInsteadConfirm(false)
    try {
      const updated = await archiveCategory(category.id)
      onArchiveChanged(updated)
    } catch {
      setGeneralError(GENERAL_ERROR_MESSAGE)
    }
  }

  return (
    <div className="fixed inset-0 z-50 flex items-end justify-center bg-black/50">
      <div className="sheet-enter flex max-h-[88vh] w-full max-w-md flex-col rounded-t-[28px] bg-surface">
        <div className="flex shrink-0 flex-col items-center pt-3">
          <div className="h-1.5 w-10 rounded-full bg-line" />
        </div>

        <div className="flex shrink-0 items-center justify-between px-5 pt-3 pb-2">
          <h2 className="text-lg font-bold text-ink">
            {category ? 'Editar categoría' : 'Nueva categoría'}
          </h2>
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
          id="category-form"
          onSubmit={handleSubmit}
          className="flex-1 overflow-y-auto px-5 pb-4"
        >
          <div className="pt-2">
            <label htmlFor="category-name" className="mb-1 block text-sm font-medium text-ink-soft">
              Nombre
            </label>
            <input
              id="category-name"
              type="text"
              maxLength={50}
              value={name}
              onChange={(e) => {
                setName(e.target.value)
                setNameError(null)
                setFieldErrors((errors) => {
                  if (!('name' in errors)) return errors
                  const next = { ...errors }
                  delete next.name
                  return next
                })
              }}
              className="w-full rounded-icon border border-line px-4 py-2 text-ink outline-none focus:border-accent"
            />
            {(fieldErrors.name || nameError) && (
              <p className="mt-1 text-sm text-red-600">{fieldErrors.name ?? nameError}</p>
            )}
          </div>

          <div className="pt-5">
            <span className="mb-2 block text-sm font-medium text-ink-soft">Tipo</span>
            <div className="flex gap-2">
              {CATEGORY_KINDS.map((option) => {
                const selected = kind === option.value
                return (
                  <button
                    key={option.value}
                    type="button"
                    aria-pressed={selected}
                    onClick={() => setKind(option.value)}
                    className={`rounded-full px-4 py-2 text-sm font-medium ${
                      selected ? 'bg-ink text-white' : 'bg-ground text-ink-soft'
                    }`}
                  >
                    {option.label}
                  </button>
                )
              })}
            </div>
          </div>

          <div className="pt-5">
            <span className="mb-2 block text-sm font-medium text-ink-soft">Ícono</span>
            <div className="grid grid-cols-5 gap-2">
              {ALLOWED_ICON_NAMES.map((iconName) => {
                const selected = icon === iconName
                return (
                  <button
                    key={iconName}
                    type="button"
                    aria-pressed={selected}
                    aria-label={ICON_LABELS[iconName]}
                    onClick={() => {
                      setIcon(iconName)
                      setFieldErrors((errors) => {
                        if (!('icon' in errors)) return errors
                        const next = { ...errors }
                        delete next.icon
                        return next
                      })
                    }}
                    className={`flex h-12 items-center justify-center rounded-icon border-2 ${
                      selected ? 'border-accent bg-tint text-accent' : 'border-transparent bg-ground text-ink-soft'
                    }`}
                  >
                    <CategoryIcon name={iconName} className="h-5 w-5" />
                  </button>
                )
              })}
            </div>
            {fieldErrors.icon && <p className="mt-1 text-sm text-red-600">{fieldErrors.icon}</p>}
          </div>

          {generalError && <p className="pt-4 text-sm text-red-600">{generalError}</p>}

          {category && (
            <div className="mt-6 flex flex-col gap-3 border-t border-line pt-4">
              <button
                type="button"
                onClick={handleToggleArchive}
                className="text-sm font-semibold text-accent"
              >
                {category.archived ? 'Desarchivar' : 'Archivar'}
              </button>
              <button
                type="button"
                onClick={() => setShowDeleteConfirm(true)}
                className="text-sm font-semibold text-red-600"
              >
                Eliminar categoría
              </button>
            </div>
          )}
        </form>

        <div className="shrink-0 border-t border-line px-5 py-4">
          <button
            type="submit"
            form="category-form"
            disabled={!canSubmit}
            aria-disabled={!canSubmit}
            className="flex h-14 w-full items-center justify-center rounded-full bg-accent text-base font-semibold text-white disabled:cursor-not-allowed disabled:bg-mist"
          >
            {isSubmitting ? 'Guardando…' : 'Guardar'}
          </button>
        </div>
      </div>

      {showDeleteConfirm && (
        <ConfirmDialog
          message="¿Eliminar esta categoría?"
          confirmLabel="Eliminar"
          destructive
          onConfirm={handleDelete}
          onCancel={() => setShowDeleteConfirm(false)}
        />
      )}

      {showArchiveInsteadConfirm && (
        <ConfirmDialog
          message="Esta categoría tiene gastos asociados. ¿Querés archivarla en su lugar?"
          confirmLabel="Archivar"
          onConfirm={handleArchiveInstead}
          onCancel={() => setShowArchiveInsteadConfirm(false)}
        />
      )}
    </div>
  )
}
