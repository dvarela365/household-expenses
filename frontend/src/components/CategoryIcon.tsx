import {
  Bike,
  Bus,
  Circle,
  Coffee,
  GraduationCap,
  HeartPulse,
  Home,
  Popcorn,
  ShoppingCart,
  Zap,
  type LucideIcon,
} from 'lucide-react'

const ICONS_BY_NAME: Record<string, LucideIcon> = {
  'shopping-cart': ShoppingCart,
  zap: Zap,
  bike: Bike,
  coffee: Coffee,
  bus: Bus,
  'heart-pulse': HeartPulse,
  'graduation-cap': GraduationCap,
  home: Home,
  popcorn: Popcorn,
  circle: Circle,
}

type CategoryIconProps = {
  name: string
  className?: string
}

export default function CategoryIcon({ name, className }: CategoryIconProps) {
  const Icon = ICONS_BY_NAME[name] ?? Circle
  return <Icon className={className} aria-hidden="true" />
}
