import { Receipt, Tags } from 'lucide-react'

export type Tab = 'expenses' | 'categories'

type BottomNavProps = {
  activeTab: Tab
  onTabChange: (tab: Tab) => void
}

export default function BottomNav({ activeTab, onTabChange }: BottomNavProps) {
  return (
    <nav className="fixed inset-x-0 bottom-0 z-40 flex h-16 border-t border-line bg-surface">
      <button
        type="button"
        onClick={() => onTabChange('expenses')}
        aria-pressed={activeTab === 'expenses'}
        className={`flex flex-1 flex-col items-center justify-center gap-0.5 text-xs font-medium ${
          activeTab === 'expenses' ? 'text-accent' : 'text-ink-soft'
        }`}
      >
        <Receipt className="h-5 w-5" aria-hidden="true" />
        Movimientos
      </button>
      <button
        type="button"
        onClick={() => onTabChange('categories')}
        aria-pressed={activeTab === 'categories'}
        className={`flex flex-1 flex-col items-center justify-center gap-0.5 text-xs font-medium ${
          activeTab === 'categories' ? 'text-accent' : 'text-ink-soft'
        }`}
      >
        <Tags className="h-5 w-5" aria-hidden="true" />
        Categorías
      </button>
    </nav>
  )
}
