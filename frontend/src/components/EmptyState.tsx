export default function EmptyState() {
  return (
    <div className="flex flex-col items-center justify-center gap-2 px-4 py-16 text-center text-gray-500">
      <p className="text-lg font-medium">Todavía no hay gastos este mes</p>
      <p className="text-sm">Tocá el botón + para cargar el primero.</p>
    </div>
  )
}
