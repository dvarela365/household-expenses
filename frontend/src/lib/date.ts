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

/** Formats a "YYYY-MM-DD" string as a short day/month for list rows, e.g. "2 oct". */
export function formatExpenseDate(isoDate: string): string {
  const date = parseLocalDate(isoDate)
  return date.toLocaleDateString('es-AR', { day: 'numeric', month: 'short' })
}
