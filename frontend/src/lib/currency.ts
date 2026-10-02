const formatter = new Intl.NumberFormat('es-AR', { style: 'currency', currency: 'ARS' })

export function formatArs(amount: number): string {
  return formatter.format(amount)
}
