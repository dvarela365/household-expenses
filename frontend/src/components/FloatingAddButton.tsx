import { Plus } from 'lucide-react'

type FloatingAddButtonProps = {
  onClick: () => void
}

export default function FloatingAddButton({ onClick }: FloatingAddButtonProps) {
  return (
    <button
      type="button"
      onClick={onClick}
      aria-label="Agregar gasto"
      className="fixed bottom-6 right-6 flex h-14 w-14 items-center justify-center rounded-full bg-blue-600 text-white shadow-lg hover:bg-blue-700"
    >
      <Plus className="h-7 w-7" aria-hidden="true" />
    </button>
  )
}
