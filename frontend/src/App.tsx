import { useEffect, useState } from 'react'

type Category = {
  id: number
  name: string
  kind: 'FIXED' | 'VARIABLE'
  icon: string
}

export default function App() {
  const [categories, setCategories] = useState<Category[]>([])
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    fetch('/api/categories')
      .then((res) => {
        if (!res.ok) throw new Error(`HTTP ${res.status}`)
        return res.json() as Promise<Category[]>
      })
      .then(setCategories)
      .catch((e: Error) => setError(e.message))
  }, [])

  return (
    <main className="mx-auto max-w-md p-6">
      <h1 className="text-2xl font-semibold">Categorías</h1>
      {error && <p className="mt-4 text-red-600">No se pudieron cargar: {error}</p>}
      <ul className="mt-4 divide-y divide-gray-200">
        {categories.map((c) => (
          <li key={c.id} className="py-2">{c.name}</li>
        ))}
      </ul>
    </main>
  )
}