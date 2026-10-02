const AMOUNT_PATTERN = /^\d+([.,]\d{1,2})?$/

/**
 * Parses a user-typed amount like "15300,50" or "15300.50" into a number.
 * Rejects thousands separators and anything that isn't digits plus an
 * optional single decimal separator with at most two decimals.
 */
export function parseAmountInput(raw: string): number | null {
  const trimmed = raw.trim()
  if (!AMOUNT_PATTERN.test(trimmed)) {
    return null
  }
  const value = Number(trimmed.replace(',', '.'))
  return value > 0 ? value : null
}
