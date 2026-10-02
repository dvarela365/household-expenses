/** Thrown on a 400 response; carries the server's field -> message map. */
export class ValidationError extends Error {
  fieldErrors: Record<string, string>

  constructor(fieldErrors: Record<string, string>) {
    super('Validation failed')
    this.name = 'ValidationError'
    this.fieldErrors = fieldErrors
  }
}

/**
 * Thrown on a 404/409/422 ProblemDetail response. Callers disambiguate by
 * `status` plus which API call they made (e.g. a 409 from `createCategory`
 * always means the name is taken, a 409 from `deleteCategory` always means
 * the category is in use) — never by inspecting the ProblemDetail `title`.
 */
export class ProblemError extends Error {
  status: number

  constructor(status: number, detail: string) {
    super(detail)
    this.name = 'ProblemError'
    this.status = status
  }
}

export async function throwForErrorResponse(response: Response): Promise<never> {
  if (response.status === 400) {
    const problem = (await response.json()) as { errors?: Record<string, string> }
    throw new ValidationError(problem.errors ?? {})
  }

  const problem = (await response.json().catch(() => null)) as { detail?: string } | null
  throw new ProblemError(response.status, problem?.detail ?? `HTTP ${response.status}`)
}
