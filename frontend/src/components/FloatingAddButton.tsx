import { Plus } from 'lucide-react'

type FloatingAddButtonProps = {
  onClick: () => void
}

export default function FloatingAddButton({ onClick }: FloatingAddButtonProps) {
  return (
    <div className="fixed inset-x-0 bottom-20 z-40 flex justify-center">
      <button
        type="button"
        onClick={onClick}
        className="flex h-14 items-center gap-2 rounded-full bg-accent px-8 text-base font-semibold text-white shadow-[0_12px_24px_rgba(15,35,56,0.25)]"
      >
        <Plus className="h-5 w-5" aria-hidden="true" />
        Cargar gasto
      </button>
    </div>
  )
}
