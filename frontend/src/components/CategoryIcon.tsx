import { Circle } from 'lucide-react'
import { ICONS_BY_NAME } from '../lib/categoryIcons'

type CategoryIconProps = {
  name: string
  className?: string
}

export default function CategoryIcon({ name, className }: CategoryIconProps) {
  const Icon = ICONS_BY_NAME[name] ?? Circle
  return <Icon className={className} aria-hidden="true" />
}
