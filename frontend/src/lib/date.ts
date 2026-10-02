function pad(value: number): string {
  return String(value).padStart(2, '0')
}

/** Parses a plain "YYYY-MM-DD" string as a local date, never going through UTC. */
export function parseLocalDate(isoDate: string): Date {
  const [year, month, day] = isoDate.split('-').map(Number)
  return new Date(year, month - 1, day)
}

/** Today's date as "YYYY-MM-DD" in local time (not toISOString, which is UTC). */
export function todayLocalDateString(): string {
  const today = new Date()
  return `${today.getFullYear()}-${pad(today.getMonth() + 1)}-${pad(today.getDate())}`
}

/** Current month as "YYYY-MM" in local time. */
export function currentYearMonth(): string {
  const today = new Date()
  return `${today.getFullYear()}-${pad(today.getMonth() + 1)}`
}

/** Shifts a "YYYY-MM" string by `delta` months, handling year rollover. */
export function shiftYearMonth(yearMonth: string, delta: number): string {
  const [year, month] = yearMonth.split('-').map(Number)
  const date = new Date(year, month - 1 + delta, 1)
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}`
}

function capitalize(text: string): string {
  return text.charAt(0).toUpperCase() + text.slice(1)
}

/** Formats a "YYYY-MM" string as a capitalized Spanish month + year, e.g. "Octubre 2026". */
export function formatMonthLabel(yearMonth: string): string {
  const date = parseLocalDate(`${yearMonth}-01`)
  return capitalize(date.toLocaleDateString('es-AR', { month: 'long', year: 'numeric' }))
}

/**
 * Formats a "YYYY-MM-DD" string as a day-group heading, e.g. "Hoy, viernes 2",
 * "Ayer, jueves 1", or "Miércoles 30" for older dates.
 */
export function formatDayGroupLabel(isoDate: string): string {
  const date = parseLocalDate(isoDate)
  const today = parseLocalDate(todayLocalDateString())
  const diffDays = Math.round((today.getTime() - date.getTime()) / 86_400_000)
  const weekday = date.toLocaleDateString('es-AR', { weekday: 'long' })
  const day = date.getDate()

  if (diffDays === 0) return `Hoy, ${weekday} ${day}`
  if (diffDays === 1) return `Ayer, ${weekday} ${day}`
  return `${capitalize(weekday)} ${day}`
}

/** Formats a "YYYY-MM-DD" string as a short date chip label, e.g. "Hoy, 2 oct" or "28 sep". */
export function formatDateChipLabel(isoDate: string): string {
  const dayMonth = parseLocalDate(isoDate).toLocaleDateString('es-AR', {
    day: 'numeric',
    month: 'short',
  })
  return isoDate === todayLocalDateString() ? `Hoy, ${dayMonth}` : dayMonth
}
