import { formatArs } from '../lib/currency'

type HeroCardProps = {
  total: number
  count: number
}

export default function HeroCard({ total, count }: HeroCardProps) {
  const countLabel = count === 0 ? 'Sin gastos' : `${count} ${count === 1 ? 'gasto' : 'gastos'}`

  return (
    <section className="rounded-card bg-accent px-5 py-6 text-white">
      <p className="text-sm font-medium text-white/80">Gastaste este mes</p>
      <p className="mt-1 text-[38px] font-extrabold leading-tight tabular-nums">{formatArs(total)}</p>
      <p className="mt-1 text-sm text-white/80">{countLabel}</p>
    </section>
  )
}
